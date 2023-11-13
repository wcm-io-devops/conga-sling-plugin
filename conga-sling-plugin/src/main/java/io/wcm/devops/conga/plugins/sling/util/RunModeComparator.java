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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Comparator sorts run modes alphabetically.
 * However, service run modes (author, publish) are always put at the first position.
 */
public class RunModeComparator implements Comparator<String>, Serializable {
  private static final long serialVersionUID = 1L;

  private static final Set<String> SERVICE_RUNMODES = Set.of("author", "publish");

  @Override
  public int compare(String runmode1, String runmode2) {
    if (isServiceRunmode(runmode1) && !isServiceRunmode(runmode2)) {
      return -1;
    }
    if (!isServiceRunmode(runmode1) && isServiceRunmode(runmode2)) {
      return 1;
    }
    return StringUtils.compare(runmode1, runmode2);
  }

  private static boolean isServiceRunmode(@Nullable String runmode) {
    return runmode != null && SERVICE_RUNMODES.contains(runmode);
  }

}
