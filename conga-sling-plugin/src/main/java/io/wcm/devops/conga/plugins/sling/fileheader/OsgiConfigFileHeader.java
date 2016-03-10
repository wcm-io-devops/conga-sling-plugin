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

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.plugins.fileheader.AbstractFileHeader;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.FileHeaderContext;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Adds file headers to OSGi .config files.
 * ***
 * WARNING: This file header plugin is disabled.
 * Multiple lines of comment on top of a felix OSGi .conf files
 * seems to be parsed not correctly e.g. by org.apache.sling.installer.core 3.6.6. Removing the comment or converting it
 * to a single line seems to work.
 * So, no file header in OSGi .config files for now.
 * ***
 */
public final class OsgiConfigFileHeader extends AbstractFileHeader {

  /**
   * Plugin name
   */
  public static final String NAME = "osgi-config";

  private static final String FILE_EXTENSION = "config";

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
    if (StringUtils.equals(line, "") || StringUtils.contains(line, "*****")) {
      return null;
    }
    return StringUtils.trim(line);
  }

  @Override
  protected String getCommentLinePrefix() {
    return "";
  }

  @Override
  protected String getCommentBlockStart() {
    return "# ";
  }

  @Override
  protected String getCommentBlockEnd() {
    return "\n";
  }

  @Override
  protected String getLineBreak() {
    // osgi config files only support single line of comment
    return " ";
  }

  @Override
  public FileHeaderContext extract(FileContext file) {
    try {
      String content = FileUtils.readFileToString(file.getFile(), file.getCharset());
      String[] contentLines = StringUtils.split(content, "\n");
      if (contentLines.length > 0 && StringUtils.startsWith(contentLines[0], getCommentLinePrefix())) {
        String fullComment = StringUtils.trim(StringUtils.substringAfter(contentLines[0], getCommentBlockStart()));
        List<String> lines = ImmutableList.of(fullComment);
        return new FileHeaderContext().commentLines(lines);
      }
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable parse add file header from " + FileUtil.getCanonicalPath(file), ex);
    }
    return null;
  }

}
