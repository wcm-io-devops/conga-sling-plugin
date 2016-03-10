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

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.LookupTranslator;

import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.util.FileUtil;

/**
 * Escapes for JSON files.
 */
public class OsgiConfigEscapingStrategy implements EscapingStrategyPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "osgi-config";

  private static final String FILE_EXTENSION = "config";

  /*
   * Defines translations for strings in Apache Felix OSGi configuration files.
   * The rules are taken from
   * http://svn.apache.org/repos/asf/felix/trunk/configadmin/src/main/java/org/apache/felix/cm/file/ConfigurationHandler.java
   * method 'writeQuoted'.
   */
  static final CharSequenceTranslator ESCAPE_OSGI_CONFIG =
      new AggregateTranslator(
          new LookupTranslator(
              new String[][] {
                  {
                    " ", "\\ "
                  },
                  {
                    "\"", "\\\""
                  },
                  {
                    "\\", "\\\\"
                  },
                  {
                    "=", "\\="
                  },
                  // well known escapes
                  {
                    "\b", "\\b"
                  },
                  {
                    "\t", "\\t"
                  },
                  {
                    "\n", "\\n"
                  },
                  {
                    "\f", "\\f"
                  },
                  {
                    "\r", "\\r"
                  }
              }),
              new CharSequenceTranslator() {
            @Override
            public int translate(CharSequence input, int index, Writer out) throws IOException {
              char c = input.charAt(index);
              if (c < ' ') {
                String t = "000" + Integer.toHexString(c);
                out.write("\\u" + t.substring(t.length() - 4));
                return 1;
              }
              return 0;
            }
          }
          );

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(String fileExtension) {
    return FileUtil.matchesExtension(fileExtension, FILE_EXTENSION);
  }

  @Override
  public String escape(CharSequence value) {
    return value == null ? null : ESCAPE_OSGI_CONFIG.translate(value);
  }

}
