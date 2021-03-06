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
package io.wcm.devops.conga.plugins.sling.util;

import java.io.IOException;
import java.util.Dictionary;

/**
 * Consuming configurations found in provisioning model.
 * @param <R> Result
 */
public interface ConfigConsumer<R> {

  /**
   * Method is called for each configuration found in provisioning model.
   * @param path Path for configuration in filesystem.
   * @param properties OSGi properties
   * @return Result
   * @throws IOException I/O exception
   */
  R accept(String path, Dictionary<String, Object> properties) throws IOException;

}
