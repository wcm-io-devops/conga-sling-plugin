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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

class JsonOsgiConfigUtilTest {

  @Test
  @SuppressWarnings("unchecked")
  void testReadToMap() throws IOException {
    Map<String, Object> content = JsonOsgiConfigUtil.readToMap(new File("src/test/resources/osgi-config-json/sample.osgiconfig.json"));
    assertArrayEquals(new Object[] { "create service user mode1" }, (Object[])content.get("repoinit:mode1"));

    // assert JSON arrays are represented as array in map instead of list
    Map<String, Object> configurations = (Map<String, Object>)content.get("configurations");
    Map<String, Object> my_pid = (Map<String, Object>)configurations.get("my.pid");
    Object stringArrayProperty = my_pid.get("stringArrayProperty");
    assertTrue(stringArrayProperty.getClass().isArray());
    assertArrayEquals(new Object[] { "v1", "v2", "v3" }, (Object[])stringArrayProperty);
  }

}
