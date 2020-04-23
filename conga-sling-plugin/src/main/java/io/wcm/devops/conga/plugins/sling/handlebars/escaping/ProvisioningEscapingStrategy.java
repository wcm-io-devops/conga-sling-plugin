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
package io.wcm.devops.conga.plugins.sling.handlebars.escaping;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.EscapingStrategyContext;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.plugins.sling.util.ProvisioningUtil;

/**
 * Escapes for Sling Provisioning files.
 */
public class ProvisioningEscapingStrategy implements EscapingStrategyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning";

  private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^\\{\\}]+)\\}");

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String fileExtension, EscapingStrategyContext pluginContext) {
    // currently only provisioning files with explicit extension are supported, not with "txt"
    return FileUtil.matchesExtension(fileExtension, ProvisioningUtil.FILE_EXTENSION);
  }

  @Override
  public String escape(CharSequence value, EscapingStrategyContext pluginContext) {
    if (value == null) {
      return null;
    }

    // use same escaping rules as for OSGi configurations
    String escapedValue = OsgiConfigEscapingStrategy.ESCAPE_OSGI_CONFIG.translate(value);

    // escape variables ${...} with \${...} - otherwise lead to "Unknown variable" error when validating sling provisioning file
    StringBuffer result = new StringBuffer();
    Matcher matcher = VARIABLE_PATTERN.matcher(escapedValue);
    while (matcher.find()) {
      matcher.appendReplacement(result, "\\\\\\$\\{" + matcher.group(1) + "\\}");
    }
    matcher.appendTail(result);

    return result.toString();
  }

}
