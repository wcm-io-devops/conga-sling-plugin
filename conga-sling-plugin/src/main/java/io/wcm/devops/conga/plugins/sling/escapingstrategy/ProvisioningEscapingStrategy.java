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
package io.wcm.devops.conga.plugins.sling.escapingstrategy;

import io.wcm.devops.conga.generator.spi.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.util.FileUtil;
import io.wcm.devops.conga.plugins.sling.util.ProvisioningUtil;

import com.github.jknack.handlebars.EscapingStrategy;

/**
 * Escapes for Sling Provisioning files.
 */
public class ProvisioningEscapingStrategy implements EscapingStrategyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String fileExtension) {
    // currently only provisioning files with explicit extension are supported, not with "txt"
    return FileUtil.matchesExtension(fileExtension, ProvisioningUtil.FILE_EXTENSION);
  }

  @Override
  public EscapingStrategy getEscapingStrategy() {
    // use same escaping rules as for OSGi configurations
    return OsgiConfigEscapingStrategy.ESCAPING_STRATEGY;
  }

}
