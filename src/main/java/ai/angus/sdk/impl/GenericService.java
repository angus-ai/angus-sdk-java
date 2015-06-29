/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ai.angus.sdk.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import ai.angus.sdk.Configuration;
import ai.angus.sdk.Service;

public class GenericService extends CollectionImpl {

    public GenericService(URL endpoint, String name, Configuration conf) {
        super(endpoint, name, conf);
    }

    public Service getService(int version) throws IOException {
        Map<String, String> filters = new HashMap<String, String>();
        filters.put("version", Integer.toString(version));
        JSONObject result = this.list(filters);

        JSONObject versions = ((JSONObject) result.get("versions"));

        JSONObject description;

        if(version < 0) {
            List<String> toSort = new ArrayList<String>(versions.keySet());
            java.util.Collections.sort(toSort);
            version = Integer.parseInt(toSort.get(toSort.size() - 1));
        }

        description = (JSONObject) versions.get(Integer.toString(version));

        return this.conf.getFactoryRepository().getServiceFactory()
                .create(this.endpoint, (String) description.get("url"), conf);
    }
}
