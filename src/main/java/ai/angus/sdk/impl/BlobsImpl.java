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

import java.net.URL;

import org.json.simple.JSONObject;

import ai.angus.sdk.Blobs;
import ai.angus.sdk.Configuration;
import ai.angus.sdk.Resource;

public class BlobsImpl extends CollectionImpl<Resource> implements Blobs {

    public BlobsImpl(URL endpoint, String name, Configuration conf) {
        super(endpoint, name, conf);
    }

    public BlobsImpl(URL endpoint, String name, JSONObject representation,
            Configuration conf) {
        super(endpoint, name, representation, conf);
    }

    @SuppressWarnings("unchecked")
    public Resource create(File file) {
        JSONObject parameters = new JSONObject();
        parameters.put("content", file);
        return super.create(parameters, conf.getFactoryRepository()
                .getResourceFactory());
    }
}
