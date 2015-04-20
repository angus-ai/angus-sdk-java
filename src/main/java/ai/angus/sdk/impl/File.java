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

import org.json.simple.JSONAware;

public class File implements JSONAware {

    private java.io.File inner;

    public File(java.io.File inner) {
        this.inner = inner;
    }

    public File(String path) {
        this.inner = new java.io.File(path);
    }

    @Override
    public String toJSONString() {
        String name = inner.getName();
        return "\"attachment://" + name + "\"";
        // return "attachment://" + name;
    }

    public java.io.File getFile() {
        return inner;
    }
}
