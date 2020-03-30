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

public class ProvisioningEscapingStrategyTest {

  private EscapingStrategyPlugin underTest;

  @BeforeEach
  public void setUp() {
    underTest = new PluginManagerImpl().get(ProvisioningEscapingStrategy.NAME, EscapingStrategyPlugin.class);
  }

  @Test
  public void testValid() {
    assertTrue(underTest.accepts("provisioning", null));
    assertEquals("\\ \\\"\\\\", underTest.escape(" \"\\", null));
    assertEquals("äöüß€/", underTest.escape("äöüß€/", null));
  }

  @Test
  public void testEscapeVariables() {
    assertEquals("\\${var1}\\ and\\ \\${var2}", underTest.escape("${var1} and ${var2}", null));
  }

  @Test
  public void testInvalid() {
    assertFalse(underTest.accepts("txt", null));
  }

}
