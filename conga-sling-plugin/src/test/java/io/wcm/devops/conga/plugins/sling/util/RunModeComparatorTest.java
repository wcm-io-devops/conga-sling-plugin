/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2023 wcm.io
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class RunModeComparatorTest {

  @Test
  void testCompare() {
    assertEquals(List.of("mode1", "mode2"), comparedList("mode2", "mode1"));
    assertEquals(List.of("publish", "mode1", "mode2", "prod"), comparedList("mode2", "mode1", "publish", "prod"));
    assertEquals(List.of("author", "publish", "mode1", "mode2", "prod"), comparedList("mode2", "mode1", "publish", "prod", "author"));
  }

  private static List<String> comparedList(String... runmodes) {
    SortedSet<String> set = new TreeSet<>(new RunModeComparator());
    set.addAll(Arrays.asList(runmodes));
    return set.stream().collect(Collectors.toList());
  }

}
