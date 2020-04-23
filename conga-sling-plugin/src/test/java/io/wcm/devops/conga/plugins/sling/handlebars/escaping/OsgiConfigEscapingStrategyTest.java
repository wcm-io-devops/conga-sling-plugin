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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class OsgiConfigEscapingStrategyTest {

  private EscapingStrategyPlugin underTest;

  @BeforeEach
  void setUp() {
    underTest = new PluginManagerImpl().get(OsgiConfigEscapingStrategy.NAME, EscapingStrategyPlugin.class);
  }

  @Test
  void testValid() {
    assertTrue(underTest.accepts("config", null));
  }

  @Test
  void testInvalid() {
    assertFalse(underTest.accepts("txt", null));
  }

  @Test
  void testEscape() {
    assertEquals("\\ \\\"\\\\", underTest.escape(" \"\\", null));
    assertEquals("äöüß€/", underTest.escape("äöüß€/", null));
    assertEquals("aa\\=bb", underTest.escape("aa=bb", null));
    assertEquals("\\b\\t\\n\\f\\r", underTest.escape("\b\t\n\f\r", null));
    assertEquals("ab\\u0005c\\u0006", underTest.escape("ab\u0005c\u0006", null));
  }

}
