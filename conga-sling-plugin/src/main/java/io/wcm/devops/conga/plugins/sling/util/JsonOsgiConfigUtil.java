/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2024 wcm.io
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.provisioning.model.Configuration;
import org.apache.sling.provisioning.model.Feature;
import org.apache.sling.provisioning.model.Model;
import org.apache.sling.provisioning.model.RunMode;
import org.apache.sling.provisioning.model.Section;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.wcm.devops.conga.plugins.sling.postprocessor.JsonOsgiConfigPostProcessor;

/**
 * Transforms a combined JSON file to provisioning model with OSGi configurations and repoinit statements.
 */
public final class JsonOsgiConfigUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {
    // default implementation
  };

  private static final Pattern KEY_PATTERN_CONFIGURATIONS = Pattern.compile("^configurations(:(.*))?$");
  private static final Pattern KEY_PATTERN_REPOINIT = Pattern.compile("^repoinit(:(.*))?$");
  private static final int RUNMODES_INDEX = 2;

  private JsonOsgiConfigUtil() {
    // static methods only
  }

  /**
   * Read JSON file content to a map.
   * @param file JSON file
   * @return Map containing JSON content
   * @throws IOException I/O exception
   */
  static Map<String, Object> readToMap(File file) throws IOException {
    String jsonString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    return OBJECT_MAPPER.readValue(jsonString, MAP_TYPE_REFERENCE);
  }

  /**
   * Read JSON file content to a map.
   * @param file JSON file
   * @return Map containing JSON content
   * @throws IOException I/O exception
   */
  public static Model readToProvisioningModel(File file) throws IOException {
    Model model = new Model();
    String featureName = StringUtils.substringBeforeLast(file.getName(), JsonOsgiConfigPostProcessor.FILE_EXTENSION);
    Feature feature = model.getOrCreateFeature(featureName);

    Map<String, Object> data = readToMap(file);
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      processEntry(feature, entry.getKey(), entry.getValue());
    }

    return model;
  }

  /**
   * Detect entries describing OSGi configurations and repoinit statements.
   */
  @SuppressWarnings("unchecked")
  private static void processEntry(Feature feature, String key, Object value) throws IOException {
    Matcher configurationsKeyMatcher = KEY_PATTERN_CONFIGURATIONS.matcher(key);
    if (configurationsKeyMatcher.matches()) {
      if (value instanceof Map) {
        String[] runModes = toRunModes(configurationsKeyMatcher.group(RUNMODES_INDEX));
        processOsgiConfiguration(feature, runModes, (Map<String, Object>)value);
      }
      else {
        throw new IOException("Unexpected data for key " + key + ": " + value.getClass().getName());
      }
    }
    else {
      Matcher repoinitKeyMatcher = KEY_PATTERN_REPOINIT.matcher(key);
      if (repoinitKeyMatcher.matches()) {
        if (value instanceof Collection) {
          String[] runModes = toRunModes(repoinitKeyMatcher.group(RUNMODES_INDEX));
          processRepoInit(feature, runModes, (Collection<String>)value);
        }
        else {
          throw new IOException("Unexpected data for key " + key + ": " + value.getClass().getName());
        }
      }
      else {
        throw new IOException("Invalid toplevel key in JSON file: " + key);
      }
    }
  }

  private static String @Nullable [] toRunModes(String runModesString) {
    if (StringUtils.isBlank(runModesString)) {
      return null;
    }
    return StringUtils.split(runModesString, ",");
  }

  /**
   * Convert OSGi configurations to Provisioning model configurations with associated run modes.
   */
  @SuppressWarnings("unchecked")
  private static void processOsgiConfiguration(Feature feature, String[] runModes, Map<String, Object> configurations) throws IOException {
    RunMode runMode = feature.getOrCreateRunMode(runModes);
    for (Map.Entry<String, Object> entry : configurations.entrySet()) {
      String pid = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map<String, Object> configProperties = (Map<String, Object>)value;
        Configuration config = runMode.getOrCreateConfiguration(pid, null);
        Dictionary<String, Object> properties = config.getProperties();
        for (Map.Entry<String, Object> configProperty : configProperties.entrySet()) {
          properties.put(configProperty.getKey(), configProperty.getValue());
        }
      }
      else {
        throw new IOException("Unexpected configurations data for " + pid + ": " + value.getClass().getName());
      }
    }
  }

  /**
   * Convert repoinit statements to Provisioning model additional sections with associated run modes.
   */
  private static void processRepoInit(Feature feature, String[] runModes, Collection<String> repoinits) {
    Section section = new Section(ProvisioningUtil.REPOINIT_SECTION);
    feature.getAdditionalSections().add(section);
    if (runModes != null) {
      section.getAttributes().put(ProvisioningUtil.REPOINIT_PROPERTY_RUNMODES, StringUtils.join(runModes, ","));
    }
    section.setContents(StringUtils.join(repoinits, "\n"));
  }

}
