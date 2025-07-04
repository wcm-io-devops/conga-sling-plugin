/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.plugins.sling.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.provisioning.model.Configuration;
import org.apache.sling.provisioning.model.Feature;
import org.apache.sling.provisioning.model.Model;
import org.apache.sling.provisioning.model.ModelUtility;
import org.apache.sling.provisioning.model.ModelUtility.ResolverOptions;
import org.apache.sling.provisioning.model.RunMode;
import org.apache.sling.provisioning.model.Section;
import org.apache.sling.provisioning.model.io.ModelReader;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Helper for handling provisioning file format.
 */
public final class ProvisioningUtil {

  /**
   * Provisioning file extension (this is currently not standardized)
   */
  public static final String FILE_EXTENSION = "provisioning";

  /**
   * Alternative "txt" file extension for provisioning files, which is currently used officially.
   */
  public static final String TEXT_FILE_EXTENSION = "txt";

  static final String REPOINIT_SECTION = "repoinit";
  static final String REPOINIT_PROPERTY_RUNMODES = "runModes";
  private static final String REPOINIT_PID = "org.apache.sling.jcr.repoinit.RepositoryInitializer";

  private ProvisioningUtil() {
    // static methods only
  }

  /**
   * Check if given file is a sling provisioning file.
   * @param file File
   * @return true if it seems to be so
   */
  public static boolean isProvisioningFile(FileContext file) {
    if (FileUtil.matchesExtension(file, FILE_EXTENSION)) {
      return true;
    }
    try {
      // check for generic txt extension and do some heuristics on the content of the file
      return FileUtil.matchesExtension(file.getFile(), TEXT_FILE_EXTENSION)
          && StringUtils.contains(FileUtils.readFileToString(file.getFile(), file.getCharset()), "[feature ");
    }
    catch (IOException ex) {
      return false;
    }
  }

  /**
   * Parse provisioning file to model
   * @param file File
   * @return Model
   * @throws IOException I/O exception
   */
  public static Model getModel(FileContext file) throws IOException {
    try (InputStream is = new BufferedInputStream(new FileInputStream(file.getFile()));
        Reader reader = new InputStreamReader(is, file.getCharset())) {
      Model model = ModelReader.read(reader, null);
      model = ModelUtility.getEffectiveModel(model, new ResolverOptions());
      return model;
    }
  }

  /**
   * Visits OSGi configuration for all feature and run modes.
   * @param model Provisioning Model
   * @param consumer Configuration consumer
   * @param <R> Result type
   * @return List of non-null results
   * @throws IOException I/O exception
   */
  @SuppressWarnings("java:S3776") // ignore complexity
  public static <R> List<R> visitOsgiConfigurations(Model model, ConfigConsumer<R> consumer) throws IOException {
    List<R> results = new ArrayList<>();
    for (Feature feature : model.getFeatures()) {

      // OSGi configurations
      for (RunMode runMode : feature.getRunModes()) {
        for (Configuration configuration : runMode.getConfigurations()) {
          String path = getPathForConfiguration(configuration, runMode);
          R result = consumer.accept(path, configuration.getProperties());
          if (result != null) {
            results.add(result);
          }
        }
      }

      // repoinit statements
      for (Section section : feature.getAdditionalSections(REPOINIT_SECTION)) {

        // repoinit script
        String script = section.getContents();
        if (StringUtils.isBlank(script)) {
          continue;
        }

        // associated run modes
        String runModesString = section.getAttributes().get(REPOINIT_PROPERTY_RUNMODES);
        RunMode runMode;
        if (runModesString != null) {
          runMode = new RunMode(StringUtils.split(runModesString, ","));
        }
        else {
          runMode = new RunMode(null);
        }

        // prepare repoinit OSGi configuration
        String pid = Objects.toString(feature.getName(), "conga")
            + (runModesString != null ? "-" + StringUtils.replace(runModesString, ",", "-") : "");
        Configuration configuration = new Configuration(pid, REPOINIT_PID);
        configuration.getProperties().put("scripts", new String[] { script });
        String path = getPathForConfiguration(configuration, runMode);
        R result = consumer.accept(path, configuration.getProperties());
        if (result != null) {
          results.add(result);
        }
      }

    }
    return results;
  }

  /**
   * Get the relative path for a configuration
   */
  private static String getPathForConfiguration(Configuration configuration, RunMode runMode) {
    SortedSet<String> runModesList = new TreeSet<>(new RunModeComparator());
    if (runMode.getNames() != null) {
      runModesList.addAll(Arrays.asList(runMode.getNames()));
    }

    // run modes directory
    StringBuilder path = new StringBuilder();
    if (!runModesList.isEmpty() && !runMode.isSpecial()) {
      path.append(StringUtils.join(runModesList, ".")).append("/");
    }

    // main name
    if (configuration.getFactoryPid() != null) {
      path.append(configuration.getFactoryPid()).append("-");
    }
    path.append(configuration.getPid()).append(".cfg.json");

    return path.toString();
  }

  /**
   * Generate OSGi configuration for all feature and run modes.
   * @param model Provisioning Model
   * @param dir Target directory
   * @param context Post processor context
   * @return Generated files
   * @throws IOException I/O exception
   */
  public static List<FileContext> generateOsgiConfigurations(Model model, File dir, PostProcessorContext context) throws IOException {
    return visitOsgiConfigurations(model, new ConfigConsumer<FileContext>() {

      @Override
      @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
      public FileContext accept(String path, Dictionary<String, Object> properties) throws IOException {
        context.getLogger().info("  Generate {}", path);

        File confFile = new File(dir, path);
        confFile.getParentFile().mkdirs();
        try (FileOutputStream os = new FileOutputStream(confFile)) {
          OsgiConfigUtil.write(os, properties);
        }

        return new FileContext().file(confFile).charset(StandardCharsets.UTF_8);
      }
    });
  }

}
