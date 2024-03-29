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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;

import org.apache.commons.io.FileUtils;
import org.apache.felix.cm.json.io.Configurations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
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
  void setUp(TestInfo testInfo) throws IOException {
    underTest = new PluginManagerImpl().get(ProvisioningOsgiConfigPostProcessor.NAME, PostProcessorPlugin.class);

    // prepare target directory
    targetDir = new File("target/ProvisioningOsgiConfigPostProcessorTest_" + testInfo.getDisplayName());
    if (targetDir.exists()) {
      FileUtils.deleteDirectory(targetDir);
    }
  }

  @Test
  void testProvisioningExample() throws Exception {

    // post process example valid provisioning file
    File provisioningFile = new File(targetDir, "provisioningExample.txt");
    FileUtils.copyFile(new File(getClass().getResource("/provisioning/validProvisioning.txt").toURI()), provisioningFile);
    postProcess(provisioningFile);

    // validate generated configs
    Dictionary<?, ?> config = readConfig("my.pid.cfg.json");
    assertEquals("value1", config.get("stringProperty"));
    assertArrayEquals(new String[] {
        "v1", "v2", "v3"
    }, (String[])config.get("stringArrayProperty"));
    assertEquals(true, config.get("booleanProperty"));
    assertEquals(999999999999L, config.get("longProperty"));

    assertExists("my.factory-my.pid.cfg.json");
    assertExists("mode1/my.factory-my.pid2.cfg.json");
    assertExists("mode2/my.pid2.cfg.json");
    assertExists("publish.prod/my.pid2.cfg.json");

    // validate repoinit statements
    config = readConfig("org.apache.sling.jcr.repoinit.RepositoryInitializer-test.cfg.json");
    assertArrayEquals(new String[] {"create path /repoinit/test1\n" +
        "create path /repoinit/test2\n" }, (String[])config.get("scripts"));

    config = readConfig("mode1/org.apache.sling.jcr.repoinit.RepositoryInitializer-test-mode1.cfg.json");
    assertArrayEquals(new String[] { "create service user mode1\n" }, (String[])config.get("scripts"));

    config = readConfig("mode1.mode2/org.apache.sling.jcr.repoinit.RepositoryInitializer-test-mode1-mode2.cfg.json");
    assertArrayEquals(new String[] { "create service user mode1_mode2" }, (String[])config.get("scripts"));
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
    Dictionary<?, ?> config = readConfig("com.example.ServiceConfiguration.cfg.json");
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
    Dictionary<?, ?> config = readConfig("com.example.ServiceConfiguration.cfg.json");
    assertNull(config.get("bar"));
    assertEquals("foo", config.get("foo"));
  }

  @Test
  void testEscapedVariable() throws Exception {

    // post process example valid provisioning file
    File provisioningFile = new File(targetDir, "provisioningExample.txt");
    FileUtils.copyFile(new File(getClass().getResource("/provisioning/validProvisioningEscapedVariable.txt").toURI()), provisioningFile);
    postProcess(provisioningFile);

    // validate generated configs
    Dictionary<?, ?> config = readConfig("my.pid.cfg.json");
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
    try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
      return Configurations.buildReader().build(reader).readConfiguration();
    }
  }

  private void assertExists(String fileName) throws IOException {
    File file = new File(targetDir, fileName);
    assertTrue(file.exists(), "Config file found: " + file.getCanonicalPath());
  }

}
