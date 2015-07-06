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
package io.wcm.devops.conga.plugins.sling.fileheader;

import io.wcm.devops.conga.generator.plugins.fileheader.AbstractFileHeader;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.plugins.sling.util.ProvisioningUtil;

/**
 * Adds file headers to Sling provisioning files.
 */
public final class ProvisioningFileHeader extends AbstractFileHeader {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, FileHeaderContext context) {
    return ProvisioningUtil.isProvisioningFile(file);
  }

  @Override
  protected String getCommentLinePrefix() {
    return "# ";
  }

  @Override
  protected String getBlockSuffix() {
    return getLineBreak();
  }

}
