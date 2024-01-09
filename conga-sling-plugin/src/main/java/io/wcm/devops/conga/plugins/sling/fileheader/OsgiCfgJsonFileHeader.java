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
package io.wcm.devops.conga.plugins.sling.fileheader;

import org.apache.commons.lang3.StringUtils;

import io.wcm.devops.conga.generator.plugins.fileheader.AbstractFileHeader;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Adds file headers to OSGi .cfg.json files.
 * <p>
 * Those are actually JSON files, but inline comments are supported, see
 * <a href="https://docs.osgi.org/specification/osgi.cmpn/7.0.0/service.configurator.html#d0e131566">Specs</a>
 * </p>
 */
public final class OsgiCfgJsonFileHeader extends AbstractFileHeader {

  /**
   * Plugin name
   */
  public static final String NAME = "osgi-cfg-json";

  private static final String FILE_EXTENSION = "cfg.json";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, FileHeaderContext context) {
    return FileUtil.matchesExtension(file, FILE_EXTENSION);
  }

  @Override
  protected String sanitizeComment(String line) {
    return StringUtils.replace(StringUtils.replace(line, "/*", "/+"), "*/", "+/");
  }

  @Override
  protected String getCommentBlockStart() {
    return "/*\n";
  }

  @Override
  protected String getCommentBlockEnd() {
    return "*/\n";
  }

  @Override
  public FileHeaderContext extract(FileContext file) {
    return extractFileHeaderBetweenBlockStartEnd(file);
  }

}
