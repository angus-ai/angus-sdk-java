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

import ai.angus.sdk.Collection;
import ai.angus.sdk.Configuration;
import ai.angus.sdk.Job;
import ai.angus.sdk.ProcessException;
import ai.angus.sdk.ResultCallBack;
import ai.angus.sdk.Service;
import ai.angus.sdk.Session;

public class ServiceImpl extends ResourceImpl implements Service {

    private Collection<Job> jobs;
    private Session         defaultSession;

    public ServiceImpl(URL parent, String name, Configuration conf) {
        super(parent, name, null, conf);
        jobs = conf.getFactoryRepository().getJobsFactory()
                .create(this.endpoint, "jobs", new JSONObject(), conf);
    }

    public ServiceImpl(URL parent, String name, JSONObject content,
            Configuration conf) {
        super(parent, name, content, conf);
        jobs = conf.getFactoryRepository().getJobsFactory()
                .create(this.endpoint, "jobs", new JSONObject(), conf);
    }

    @Override
    public JobImpl process(JSONObject params) throws ProcessException {
        return process(params, false, null, null);
    }

    @Override
    public JobImpl process(JSONObject params, boolean async)
            throws ProcessException {
        return process(params, async, null, null);
    }

    @Override
    public JobImpl process(JSONObject params, ResultCallBack callback)
            throws ProcessException {
        return process(params, false, null, callback);
    }

    @Override
    public JobImpl process(JSONObject params, boolean async,
            ResultCallBack callback) throws ProcessException {
        return process(params, false, null, callback);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JobImpl process(JSONObject params, boolean async, Session session,
            ResultCallBack callback) throws ProcessException {

        if (!params.containsKey("image")) {
            throw new ProcessException("Bad parameters, no image");
        }
        params = new JSONObject(params);
        params.put("async", async);

        if (session == null) {
            session = this.defaultSession;
        }

        if (session != null) {
            params.put("state", session.getState());
        }

        JobImpl job = (JobImpl) jobs.create(params, conf.getFactoryRepository()
                .getJobFactory());

        return job;
    }

    @Override
    public Session createSession() {
        Session session = new SessionImpl(this);
        return session;
    }

    @Override
    public void enableSession() {
        if (this.defaultSession == null) {
            this.defaultSession = this.createSession();
        }

    }

    @Override
    public void disableSession() {
        this.defaultSession = null;
    }

}
