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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.wcm.devops.conga.generator.spi.handlebars.EscapingStrategyPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

public class OsgiConfigEscapingStrategyTest {

  private EscapingStrategyPlugin underTest;

  @Before
  public void setUp() {
    underTest = new PluginManagerImpl().get(OsgiConfigEscapingStrategy.NAME, EscapingStrategyPlugin.class);
  }

  @Test
  public void testValid() {
    assertTrue(underTest.accepts("config"));
  }

  @Test
  public void testInvalid() {
    assertFalse(underTest.accepts("txt"));
  }

  @Test
  public void testEscape() {
    assertEquals("\\ \\\"\\\\", underTest.escape(" \"\\"));
    assertEquals("äöüß€/", underTest.escape("äöüß€/"));
    assertEquals("aa\\=bb", underTest.escape("aa=bb"));
    assertEquals("\\b\\t\\n\\f\\r", underTest.escape("\b\t\n\f\r"));
    assertEquals("ab\\u0005c\\u0006", underTest.escape("ab\u0005c\u0006"));
  }

}
