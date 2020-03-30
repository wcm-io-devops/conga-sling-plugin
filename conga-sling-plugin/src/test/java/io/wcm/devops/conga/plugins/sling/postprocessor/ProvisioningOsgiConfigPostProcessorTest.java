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
package io.wcm.devops.conga.plugins.sling.postprocessor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PluginContextOptions;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

class ProvisioningOsgiConfigPostProcessorTest {

  private PostProcessorPlugin underTest;

  private File targetDir;

  @BeforeEach
  void setUp() throws IOException {
    underTest = new PluginManagerImpl().get(ProvisioningOsgiConfigPostProcessor.NAME, PostProcessorPlugin.class);

    // prepare target dirctory
    targetDir = new File("target/postprocessor-test_" + UUID.randomUUID().toString());
    if (targetDir.exists()) {
      FileUtils.deleteDirectory(targetDir);
    }
  }

  @AfterEach
  void tearDown() throws IOException {
    FileUtils.deleteDirectory(targetDir);
  }

  @Test
  void testProvisioningExample() throws Exception {

    // post process example valid provisioning file
    File provisioningFile = new File(targetDir, "provisioningExample.txt");
    FileUtils.copyFile(new File(getClass().getResource("/validProvisioning.txt").toURI()), provisioningFile);
    postProcess(provisioningFile);

    // validate generated configs
    Dictionary<?, ?> config = readConfig("my.pid.config");
    assertEquals("value1", config.get("stringProperty"));
    assertArrayEquals(new String[] {
        "v1", "v2", "v3"
    }, (String[])config.get("stringArrayProperty"));
    assertEquals(true, config.get("booleanProperty"));
    assertEquals(999999999999L, config.get("longProperty"));

    assertExists("my.factory-my.pid.config");
    assertExists("mode1/my.factory-my.pid2.config");
    assertExists("mode2/my.pid2.config");
  }

  @Test
  void testSimpleConfig() throws Exception {
    final String PROVISIONING_FILE = "[feature name=test]\n" +
        "[configurations]\n" +
        "com.example.ServiceConfiguration\n"
        + "  bar=\"bar\""
        + "  foo=\"foo\"";

    // post process provisioning example
    File provisioningFile = new File(targetDir, "simpleConfig.txt");
    FileUtils.write(provisioningFile, PROVISIONING_FILE, StandardCharsets.UTF_8);
    postProcess(provisioningFile);

    // validate generated configs
    Dictionary<?, ?> config = readConfig("com.example.ServiceConfiguration.config");
    assertEquals("bar", config.get("bar"));
    assertEquals("foo", config.get("foo"));
  }

  @Test
  void testSimpleConfigWithNewline() throws Exception {
    final String PROVISIONING_FILE = "[feature name=test]\n" +
        "[configurations]\n" +
        "com.example.ServiceConfiguration\n"
        + ""
        + "  foo=\"foo\"";

    // post process provisioning example
    File provisioningFile = new File(targetDir, "simpleConfigWithNewline.txt");
    FileUtils.write(provisioningFile, PROVISIONING_FILE, StandardCharsets.UTF_8);
    postProcess(provisioningFile);

    // validate generated configs
    Dictionary<?, ?> config = readConfig("com.example.ServiceConfiguration.config");
    assertNull(config.get("bar"));
    assertEquals("foo", config.get("foo"));
  }

  @Test
  void testEscapedVariable() throws Exception {

    // post process example valid provisioning file
    File provisioningFile = new File(targetDir, "provisioningExample.txt");
    FileUtils.copyFile(new File(getClass().getResource("/validProvisioningEscapedVariable.txt").toURI()), provisioningFile);
    postProcess(provisioningFile);

    // validate generated configs
    Dictionary<?, ?> config = readConfig("my.pid.config");
    assertEquals("${var1} and ${var2}", config.get("stringProperty"));
  }

  private void postProcess(File provisioningFile) {
    // post-process
    FileContext fileContext = new FileContext()
        .file(provisioningFile)
        .charset(StandardCharsets.UTF_8);
    PluginContextOptions pluginContextOptions = new PluginContextOptions()
        .pluginManager(new PluginManagerImpl())
        .logger(LoggerFactory.getLogger(ProvisioningOsgiConfigPostProcessor.class));
    PostProcessorContext context = new PostProcessorContext()
        .pluginContextOptions(pluginContextOptions);

    assertTrue(underTest.accepts(fileContext, context));
    underTest.apply(fileContext, context);

    // validate
    assertFalse(provisioningFile.exists(), "Provisioning file deleted");
  }

  private Dictionary<?, ?> readConfig(String fileName) throws IOException {
    assertExists(fileName);
    File file = new File(targetDir, fileName);
    try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
      return ConfigurationHandler.read(is);
    }
  }

  private void assertExists(String fileName) throws IOException {
    File file = new File(targetDir, fileName);
    assertTrue(file.exists(), "Config file found: " + file.getCanonicalPath());
  }

}
