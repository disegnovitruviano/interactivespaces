/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.liveactivity.runtime.activity.wrapper;

import interactivespaces.activity.execution.ActivityExecutionContext;
import interactivespaces.activity.execution.BaseActivityExecutionContext;

/**
 * Support class for instances of {@link ActivityWrapper}.
 *
 * @author Keith M. Hughes
 */
public abstract class BaseActivityWrapper implements ActivityWrapper {

  @Override
  public void done() {
    // Default is do nothing.
  }

  @Override
  public ActivityExecutionContext newExecutionContext() {
    return new BaseActivityExecutionContext();
  }
}
