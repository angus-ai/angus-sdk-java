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
import ai.angus.sdk.Collection;
import ai.angus.sdk.Configuration;
import ai.angus.sdk.FactoryRepository;
import ai.angus.sdk.Job;
import ai.angus.sdk.Resource;
import ai.angus.sdk.ResourceFactory;
import ai.angus.sdk.Root;
import ai.angus.sdk.Service;
import ai.angus.sdk.Services;

public class FactoryRepositoryImpl implements FactoryRepository {

    private static ResourceFactory<Collection<Job>> JOBS_FACT;
    private static ResourceFactory<Services> SERVICES_FACT;
    private static ResourceFactory<Blobs> BLOBS_FACT;

    private static ResourceFactory<Job> JOB_FACT;
    private static ResourceFactory<Service> SERVICE_FACT;
    private static ResourceFactory<Resource> RES_FACT;
    private static ResourceFactory<Root> ROOT_FACT;

    public FactoryRepositoryImpl() {
        JOBS_FACT = new CollectionFactoryImpl<Job>();

        JOB_FACT = new ResourceFactory<Job>() {

            @Override
            public Job create(URL parent, String name, JSONObject content,
                    Configuration conf) {
                return new JobImpl(parent, name, content, conf);
            }

            @Override
            public Job create(URL parent, String name, Configuration conf) {
                return new JobImpl(parent, name, conf);
            }

        };

        SERVICE_FACT = new ResourceFactory<Service>() {

            @Override
            public Service create(URL parent, String name, JSONObject content,
                    Configuration conf) {
                return new ServiceImpl(parent, name, content, conf);
            }

            @Override
            public Service create(URL parent, String name, Configuration conf) {
                return new ServiceImpl(parent, name, conf);
            }
        };

        RES_FACT = new ResourceFactory<Resource>() {

            @Override
            public Resource create(URL parent, String name, JSONObject content,
                    Configuration conf) {
                return new ResourceImpl(parent, name, content, conf);
            }

            @Override
            public Resource create(URL parent, String name, Configuration conf) {
                return new ResourceImpl(parent, name, conf);
            }
        };

        SERVICES_FACT = new ResourceFactory<Services>() {

            @Override
            public Services create(URL parent, String name, JSONObject content,
                    Configuration conf) {
                return new ServicesImpl(parent, name, content, conf);
            }

            @Override
            public Services create(URL parent, String name, Configuration conf) {
                return new ServicesImpl(parent, name, conf);
            }
        };

        BLOBS_FACT = new ResourceFactory<Blobs>() {

            @Override
            public Blobs create(URL parent, String name, JSONObject content,
                    Configuration conf) {
                return new BlobsImpl(parent, name, content, conf);
            }

            @Override
            public Blobs create(URL parent, String name, Configuration conf) {
                return new BlobsImpl(parent, name, conf);
            }
        };

        ROOT_FACT = new ResourceFactory<Root>() {

            @Override
            public Root create(URL endpoint, String name, JSONObject content,
                    Configuration conf) {
                return new RootImpl(endpoint, conf);
            }

            @Override
            public Root create(URL endpoint, String name, Configuration conf) {
                return new RootImpl(endpoint, conf);
            }

        };

    }

    @Override
    public ResourceFactory<Collection<Job>> getJobsFactory() {
        return JOBS_FACT;
    }

    @Override
    public ResourceFactory<Job> getJobFactory() {
        return JOB_FACT;
    }

    @Override
    public ResourceFactory<Service> getServiceFactory() {
        return SERVICE_FACT;
    }

    @Override
    public ResourceFactory<Resource> getResourceFactory() {
        return RES_FACT;
    }

    @Override
    public <T extends Resource> ResourceFactory<Collection<T>> getCollectionFactory() {
        return new CollectionFactoryImpl<T>();
    }

    @Override
    public ResourceFactory<Services> getServicesFactory() {
        return SERVICES_FACT;
    }

    @Override
    public ResourceFactory<Blobs> getBlobsFactory() {
        return BLOBS_FACT;
    }

    @Override
    public ResourceFactory<Root> getRootFactory() {
        return ROOT_FACT;
    }
}
