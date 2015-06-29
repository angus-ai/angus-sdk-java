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

import ai.angus.sdk.Blobs;
import ai.angus.sdk.Configuration;
import ai.angus.sdk.Resource;
import ai.angus.sdk.Root;
import ai.angus.sdk.Services;

public class RootImpl extends CollectionImpl<Resource> implements Root {

    private Services services;
    private Blobs blobs;

    public RootImpl(Configuration conf) {
        this(conf.getDefaultRoot(), conf);
    }

    public RootImpl(URL endpoint, Configuration conf) {
        super(null, endpoint.toString(), conf);

        conf.setDefaultRoot(endpoint);

        services = conf.getFactoryRepository().getServicesFactory()
                .create(this.endpoint, "services", conf);

        blobs = conf.getFactoryRepository().getBlobsFactory()
                .create(this.endpoint, "blobs", conf);

    }

    @Override
    public Services getServices() {
        return services;
    }

    @Override
    public Blobs getBlobs() {
        return blobs;
    }

}
