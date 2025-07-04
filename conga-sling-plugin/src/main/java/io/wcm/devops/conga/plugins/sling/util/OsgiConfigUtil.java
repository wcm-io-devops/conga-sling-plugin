/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2019 wcm.io
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;

import org.apache.felix.cm.json.io.Configurations;

/**
 * Helper class for writing OSGi configurations in JSON format (.cfg.json files).
 */
public final class OsgiConfigUtil {

  private OsgiConfigUtil() {
    // static methods only
  }

  /**
   * Writes the configuration data from the <code>Dictionary</code> to the given <code>OutputStream</code>.
   *
   * <p>
   * This method writes at the current location in the stream and does not close the outputstream.
   * </p>
   * @param out The <code>OutputStream</code> to write the configuration data to.
   * @param properties The <code>Dictionary</code> to write.
   * @throws IOException If an error occurs writing to the output stream.
   */
  public static void write(OutputStream out, Dictionary<String, Object> properties) throws IOException {
    try (Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
      Configurations.buildWriter().build(writer).writeConfiguration(properties);
    }
  }

}
