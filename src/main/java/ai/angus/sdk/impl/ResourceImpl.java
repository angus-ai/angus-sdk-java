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
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import ai.angus.sdk.Configuration;
import ai.angus.sdk.Resource;

public class ResourceImpl implements JSONAware, Resource {

    private JSONObject representation;
    protected Configuration conf;
    private URL parent;
    private String name;
    protected URL endpoint;

    public ResourceImpl(URL parent, String name, Configuration conf) {
        this(parent, name, null, conf);
    }

    public ResourceImpl(URL parent, String name, JSONObject content,
            Configuration conf) {
        representation = content;
        this.conf = conf;
        this.parent = parent;
        this.name = name;

        try {
            URL base;
            if (!parent.toString().endsWith("/")) {
                base = new URL(parent.toString() + "/");
            } else {
                base = parent;
            }
            this.endpoint = new URL(base, name);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getRepresentation() {
        return representation;
    }

    public void fetch() throws IOException {
        JSONObject result = conf.getHTTPClient().get(endpoint);
        representation = result;
    }

    public int getStatus() {
        if (representation.containsKey("status")) {
            int status = ((Long) representation.get("status")).intValue();
            return status;
        } else {
            return -1;
        }

    }

    @Override
    public String toJSONString() {
        return "\"" + endpoint + "\"";
    }

    public URL getURL() {
        return endpoint;
    }
}
