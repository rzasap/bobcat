package com.cognifide.qa.bb.logging.entries;

/*-
 * #%L
 * Bobcat Parent
 * %%
 * Copyright (C) 2016 Cognifide Ltd.
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

/**
 * This entry class represents a soft assertion that failed.
 */
public class SoftAssertionFailedEntry extends LogEntry {

  private String message;

  /**
   * Constructs the SoftAssertionFailedEntry instance.
   *
   * @param message soft assertion message
   */
  public SoftAssertionFailedEntry(String message) {
    super();
    this.message = message;
  }

  /**
   * @return Failure message
   */
  public String getMessage() {
    return message;
  }
}