/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.ambari.logfeeder.mapper;

import org.apache.ambari.logfeeder.conf.LogFeederProps;
import org.apache.ambari.logfeeder.plugin.filter.mapper.Mapper;
import org.apache.ambari.logfeeder.util.LogFeederUtil;
import org.apache.ambari.logsearch.config.api.model.inputconfig.MapFieldDescriptor;
import org.apache.ambari.logsearch.config.api.model.inputconfig.MapFieldValueDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Overrides the value for the field
 * <pre>
 *   "post_map_values": {
 *         "Result": [
 *           {
 *               'map_field_value': {
 *                 'pre_value': 'true',
 *                 'post_value': '1'
 *               }
 *           }
 *       }
 * </pre>
 */
public class MapperFieldValue extends Mapper<LogFeederProps> {
  private static final Logger logger = LogManager.getLogger(MapperFieldValue.class);
  
  private String prevValue = null;
  private String newValue = null;

  @Override
  public boolean init(LogFeederProps logFeederProps, String inputDesc, String fieldName, String mapClassCode, MapFieldDescriptor mapFieldDescriptor) {
    init(inputDesc, fieldName, mapClassCode);
    
    prevValue = ((MapFieldValueDescriptor)mapFieldDescriptor).getPreValue();
    newValue = ((MapFieldValueDescriptor)mapFieldDescriptor).getPostValue();;
    if (StringUtils.isEmpty(newValue)) {
      logger.fatal("Map field value is empty.");
      return false;
    }
    return true;
  }

  @Override
  public Object apply(Map<String, Object> jsonObj, Object value) {
    if (newValue != null && prevValue != null) {
      if (prevValue.equalsIgnoreCase(value.toString())) {
        value = newValue;
        jsonObj.put(getFieldName(), value);
      }
    } else {
      LogFeederUtil.logErrorMessageByInterval(this.getClass().getSimpleName() + ":apply",
          "New value is null, so transformation is not applied. " + this.toString(), null, logger, Level.ERROR);
    }
    return value;
  }
}
