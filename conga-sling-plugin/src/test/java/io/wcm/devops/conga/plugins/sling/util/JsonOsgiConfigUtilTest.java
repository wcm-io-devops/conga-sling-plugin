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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JsonOsgiConfigUtilTest {

  @Test
  void testReadToMap() throws IOException {
    Map<String, Object> content = JsonOsgiConfigUtil.readToMap(new File("src/test/resources/osgi-config-json/sample.osgiconfig.json"));
    assertArrayEquals(new String[] { "create service user mode1" }, (String[])content.get("repoinit:mode1"));
  }

  @Test
  void testListToArrayConversion() throws IOException {
    Map<String, Object> content = JsonOsgiConfigUtil.readToMap(new File("src/test/resources/arrayTypes.json"));
    assertArrayEquals(new String[] { "v1", "v2", "v3" }, (String[])content.get("stringArray"));
    assertArrayEquals(new Integer[] { 1, 2, 3 }, (Integer[])content.get("intArray"));
    assertArrayEquals(new Boolean[] { true, false }, (Boolean[])content.get("boolArray"));
    assertArrayEquals(new Boolean[] { null }, (Object[])content.get("nullArray"));
    assertArrayEquals(new Object[] { "v1", 1, true, null }, (Object[])content.get("mixedArray"));
    assertArrayEquals(new Object[0], (Object[])content.get("emptyArray"));
    assertArrayEquals(new String[] { "v1" }, (String[])((Map)content.get("nested")).get("stringArray"));
  }

}
