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
package ai.angus.sdk;

import java.io.IOException;
import java.net.URL;

import ai.angus.sdk.http.HTTPClient;

public interface Configuration {
    public FactoryRepository getFactoryRepository();

    public Root connect() throws IOException;

    public Root connect(URL url);

    public String getAccessToken();

    public void setAccessToken(String accessToken);

    public URL getDefaultRoot();

    public void setDefaultRoot(URL defaultRoot);

    public String getCaPath();

    public void setCaPath(String caPath);

    public String getClientId();

    public void setClientId(String clientId);

    public HTTPClient getHTTPClient();
}
