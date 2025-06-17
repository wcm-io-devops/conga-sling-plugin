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
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.sling.provisioning.model.Model;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.plugins.sling.util.ProvisioningUtil;

/**
 * Transforms a Sling Provisioning file into OSGi configuration files (.cfg.json).
 * Repoinit statements are supported as well, all other provisioning contents are ignored
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
      List<FileContext> files = ProvisioningUtil.generateOsgiConfigurations(model, file.getParentFile(), context);

      // delete provisioning file after transformation
      Files.delete(file.toPath());

      // return list of generated osgi configuration files
      return files;
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to post-process Sling Provisioning OSGi configurations.", ex);
    }
  }

}
