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
package io.wcm.devops.conga.plugins.sling.postprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

import org.apache.commons.lang3.CharEncoding;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.apache.sling.provisioning.model.Model;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.plugins.sling.util.ConfigConsumer;
import io.wcm.devops.conga.plugins.sling.util.ProvisioningUtil;

/**
 * Transforms a Sling Provisioning file into OSGi configurations (ignoring all other provisioning contents).
 */
public class ProvisioningOsgiConfigPostProcessor implements PostProcessorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning-osgiconfig";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, PostProcessorContext context) {
    return ProvisioningUtil.isProvisioningFile(file);
  }

  @Override
  public List<FileContext> apply(FileContext fileContext, PostProcessorContext context) {
    File file = fileContext.getFile();
    try {
      // generate OSGi configurations
      Model model = ProvisioningUtil.getModel(fileContext);
      List<FileContext> files = generateOsgiConfigurations(model, file.getParentFile(), context);

      // delete provisioning file after transformation
      file.delete();

      // return list of generated osgi configuration files
      return files;
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to post-process sling provisioning OSGi configurations.", ex);
    }
  }

  /**
   * Generate OSGi configuration for all feature and run modes.
   * @param model Provisioning Model
   * @param dir Target directory
   * @param context Post processor context
   * @throws IOException
   */
  private List<FileContext> generateOsgiConfigurations(Model model, File dir, PostProcessorContext context) throws IOException {
    return ProvisioningUtil.visitOsgiConfigurations(model, new ConfigConsumer<FileContext>() {
      @Override
      public FileContext accept(String path, Dictionary<String, Object> properties) throws IOException {
        context.getLogger().info("  Generate " + path);

        File confFile = new File(dir, path);
        confFile.getParentFile().mkdirs();
        try (FileOutputStream os = new FileOutputStream(confFile)) {
          ConfigurationHandler.write(os, properties);
        }

        return new FileContext().file(confFile).charset(CharEncoding.UTF_8);
      }
    });
  }

}
