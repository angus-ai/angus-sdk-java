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
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import ai.angus.sdk.Configuration;
import ai.angus.sdk.Service;
import ai.angus.sdk.Services;

public class ServicesImpl extends CollectionImpl<Service> implements Services {

    public ServicesImpl(URL endpoint, String name, Configuration conf) {
        super(endpoint, name, conf);
    }

    public ServicesImpl(URL endpoint, String name, JSONObject representation,
            Configuration conf) {
        super(endpoint, name, representation, conf);
    }

    public Service getService(String name, int version) throws IOException {
        Map<String, String> filters = new HashMap<String, String>();
        filters.put("name", name);
        JSONObject result = this.list(filters);
        result = (JSONObject) ((JSONObject) result.get("services")).get(name);

        String url = (String) result.get("url");

        GenericService gService = new GenericService(this.endpoint, url,
                this.conf);

        Service service = gService.getService(version);
        return service;
    }

}
