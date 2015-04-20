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
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

import ai.angus.sdk.Collection;
import ai.angus.sdk.Configuration;
import ai.angus.sdk.Resource;
import ai.angus.sdk.ResourceFactory;

public class CollectionImpl<T extends Resource> extends ResourceImpl implements
        Collection<T> {

    public CollectionImpl(URL endpoint, String name, Configuration conf) {
        super(endpoint, name, conf);
    }

    public CollectionImpl(URL endpoint, String name, JSONObject representation,
            Configuration conf) {
        super(endpoint, name, representation, conf);
    }

    @Override
    public T create(JSONObject parameters, ResourceFactory<T> constructor) {
        URL url;

        HashMap<String, java.io.File> files = new HashMap<String, java.io.File>();

        // TODO: only root level object are converted
        for (Iterator i = parameters.values().iterator(); i.hasNext();) {
            Object o = i.next();
            if (o instanceof File) {
                java.io.File file = ((File) o).getFile();
                files.put(file.getName(), file);
            }
        }

        try {
            JSONObject result = conf.getHTTPClient().post(endpoint,
                    parameters.toJSONString().getBytes(), files);
            return constructor.create(endpoint, (String) result.get("url"),
                    result, conf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject list(Map<String, String> filters) throws IOException {
        this.fetch();
        JSONObject result = this.getRepresentation();
        return result;
    }

}
