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

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for all types of log entries.
 */
public abstract class LogEntry {

  private static final AtomicInteger ENTRY_INDEX = new AtomicInteger();

  protected final long time;

  private boolean isLast;

  private int index;

  /**
   * Constructs the LogEntry and sets its timestamp to NOW.
   */
  public LogEntry() {
    index = ENTRY_INDEX.getAndIncrement();
    time = System.currentTimeMillis();
  }

  /**
   * @return Timestamp of the entry.
   */
  public Date getTime() {
    return new Date(time);
  }

  /**
   * @return True if the entry is the last one in the entry set of a given test.
   */
  public boolean isLast() {
    return isLast;
  }

  /**
   * Sets the status of the entry.
   *
   * @param isLast True means last entry.
   */
  public void setLast(boolean isLast) {
    this.isLast = isLast;
  }

  int getIndex() {
    return index;
  }

}