package com.cognifide.qa.bb.aem;

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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.qa.bb.constants.AemConfigKeys;
import com.cognifide.qa.bb.utils.WebElementUtils;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * This class represents a HTML page. <br>
 * Specific pages will inherit from this class to configure URL and title.
 */
public abstract class AbstractPage {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractPage.class);

  private static final long SECONDS_TO_WAIT_BEFORE_NEXT_RETRY = 5;

  @Inject
  private WebDriver webDriver;

  @Inject
  @Named(AemConfigKeys.PAGE_TITLE_TIMEOUT)
  private int pageTitleTimeout;

  @Inject
  @Named(AemConfigKeys.AUTHOR_URL)
  private String authorUrl;

  @Inject
  private WebElementUtils webElementUtils;

  /**
   * @return Url of the page, without domain part.
   */
  public abstract String getContentPath();

  /**
   * @return Title of the page.
   */
  public abstract String getPageTitle();

  /**
   * @return Full URL of the page which means: domain plus "content path".
   */
  public String getFullUrl() {
    return authorUrl + getContentPath();
  }

  /**
   * @return True if the page is displayed, false otherwise. <br>
   *         Assumes that the page is displayed when title of the currently displayed page is the
   *         same as the title stored in this object.
   */
  public boolean isDisplayed() {
    return webElementUtils
        .isConditionMet(ExpectedConditions.titleIs(getPageTitle()), pageTitleTimeout);
  }

  /**
   * Opens the page in a new browser window.
   */
  public void open() {
    webDriver.get(getFullUrl());
  }

  /**
   * Tries opening the page with default {@link AemConfigKeys#PAGE_TITLE_TIMEOUT} and additional
   * refreshing if the page was not opened with the first retry. Each next refresh will take
   * {@link AemConfigKeys#PAGE_TITLE_TIMEOUT} for verification if page was loaded and
   * {@link AbstractPage#SECONDS_TO_WAIT_BEFORE_NEXT_RETRY} for pause before next refresh.
   *
   * @param timeoutForRefreshing additional timeout (in seconds) for page refreshing.
   * @return <code>true</code> if page is loaded
   */
  public boolean openPageWithRefresh(int timeoutForRefreshing) {
    webDriver.get(getFullUrl());
    boolean success = isDisplayed();

    if (!success) {
      success =
          webElementUtils.isConditionMet(driver -> {
            LOG.debug("Error while loading page. Refreshing...");
            webDriver.navigate().refresh();
            return isDisplayed();
          }, timeoutForRefreshing);
    }
    return success;
  }
}