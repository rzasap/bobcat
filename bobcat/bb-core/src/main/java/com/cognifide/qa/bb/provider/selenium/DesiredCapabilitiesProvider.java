package com.cognifide.qa.bb.provider.selenium;

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


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This is a provider for the Capabilities instances.
 */
public class DesiredCapabilitiesProvider implements Provider<Capabilities> {

  private static final String CAPABILITIES_PREFIX = "webdriver.cap.";

  private static final String CAPABILITIES_MAP_PREFIX = "webdriver.map.cap.";

  @Inject
  private Properties properties;

  /**
   * Returns new default DesiredCapabilities object.
   */
  @Override
  public Capabilities get() {
    DesiredCapabilities capabilities = new DesiredCapabilities();
    Map<String, String> mapProperties = new HashMap<String, String>();

    for (String name : properties.stringPropertyNames()) {
      if (name.startsWith(CAPABILITIES_PREFIX)) {
        String capName = StringUtils.removeStart(name, CAPABILITIES_PREFIX);
        capabilities.setCapability(capName, properties.getProperty(name));
      } else if (name.startsWith(CAPABILITIES_MAP_PREFIX)) {
        mapProperties.put(StringUtils.removeStart(name, CAPABILITIES_MAP_PREFIX),
            properties.getProperty(name));
      }
    }
    processMapProperties(capabilities, mapProperties);
    return capabilities;
  }

  private void processMapProperties(DesiredCapabilities capabilities,
      Map<String, String> mapProperties) {
    Map<String, Object> maps = new HashMap<String, Object>();
    for (Map.Entry<String, String> entry : mapProperties.entrySet()) {
      String[] parts = entry.getKey().split("\\.");
      Map<String, Object> map = maps;
      for (int i = 0; i < parts.length - 1; i++) {
        map = getOrCreate(map, parts[i]);
      }
      map.put(parts[parts.length - 1], entry.getValue());
    }

    for (Map.Entry<String, Object> entry : maps.entrySet()) {
      capabilities.setCapability(entry.getKey(), entry.getValue());
    }
  }

  private Map<String, Object> getOrCreate(Map<String, Object> map, String key) {
    Map<String, Object> result = null;
    if (map.get(key) instanceof Map) {
      result = (Map<String, Object>) map.get(key);
    } else {
      result = new HashMap<String, Object>();
      map.put(key, result);
    }
    return result;
  }

}