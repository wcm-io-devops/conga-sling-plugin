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
package io.wcm.devops.conga.plugins.sling;

import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.ValidatorContext;

import java.io.IOException;

/**
 * Validates Sling Provisioning Model files.
 */
public class ProvisioningValidator implements ValidatorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, ValidatorContext context) {
    return ProvisioningUtil.isProvisioningFile(file);
  }

  @Override
  public Void apply(FileContext file, ValidatorContext context) throws ValidationException {
    try {
      ProvisioningUtil.getModel(file);
    }
    catch (IOException ex) {
      throw new ValidationException("Sling Provision Model is invalid: " + ex.getMessage(), ex);
    }
    return null;
  }

}
