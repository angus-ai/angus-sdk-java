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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ai.angus.sdk.Configuration;
import ai.angus.sdk.FactoryRepository;
import ai.angus.sdk.Root;
import ai.angus.sdk.http.HTTPClient;
import ai.angus.sdk.http.impl.HTTPClientImpl;

public class ConfigurationImpl implements Configuration {

    private static FactoryRepository REPOSITORY = new FactoryRepositoryImpl();

    private String accessToken;
    private String defaultRoot;
    private String caPath;
    private String clientId;

    private HTTPClient client;

    public ConfigurationImpl() {
        this(new File(System.getProperty("user.home"), ".angusdk/config.json"));
    }

    public ConfigurationImpl(String accessToken, String defaultRoot,
            String caPath, String clientId) {
        this.accessToken = accessToken;
        this.defaultRoot = defaultRoot;
        this.caPath = caPath;
        this.clientId = clientId;
        this.client = new HTTPClientImpl(this);
    }

    public ConfigurationImpl(File configurationFile) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject conf = (JSONObject) parser.parse(new FileReader(
                    configurationFile));
            accessToken = (String) conf.get("access_token");
            defaultRoot = (String) conf.get("default_root");
            caPath = (String) conf.get("ca_path");
            clientId = (String) conf.get("client_id");
            this.client = new HTTPClientImpl(this);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FactoryRepository getFactoryRepository() {
        return REPOSITORY;
    }

    @Override
    public Root connect() throws IOException {
        return getFactoryRepository().getRootFactory().create(
                new URL(this.defaultRoot), "", this);
    }

    @Override
    public Root connect(URL url) {
        return getFactoryRepository().getRootFactory().create(url, "", this);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDefaultRoot() {
        return defaultRoot;
    }

    public void setDefaultRoot(String defaultRoot) {
        this.defaultRoot = defaultRoot;
    }

    public String getCaPath() {
        return caPath;
    }

    public void setCaPath(String caPath) {
        this.caPath = caPath;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public HTTPClient getHTTPClient() {
        return client;
    }

    public void setClient(HTTPClient client) {
        this.client = client;
    }

}
