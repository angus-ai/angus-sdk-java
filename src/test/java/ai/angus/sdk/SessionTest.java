/*
 * Copyright 2015 Angus.ai (http://www.angus.ai).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Aurélien Moreau
 */

package ai.angus.sdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import ai.angus.sdk.impl.ConfigurationImpl;
import ai.angus.sdk.impl.File;

public class SessionTest {

    private static Root root;
    private static Service service;
    private static CheckResult callback1, callback3, callback43;
    private static String IMG_1 = "data/Angus-6.jpg";

    @BeforeClass
    public static void setUp() {
        try {
            Configuration conf = new ConfigurationImpl();
            root = conf.connect();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(root);
        try {
            service = root.getServices().getService("face_detection", 1);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    private static class CheckResult implements ResultCallBack {
        public CheckResult() {
        }

        @Override
        public void callback(Job result) {
            SessionTest.checkResult(result);
        }
    }

    private static void checkResult(Job result) {
        assertEquals(result.getStatus(), Resource.CREATED);
        assertEquals(result.getRepresentation(), result.getResult());
        assertTrue(result.getRepresentation().containsKey("faces"));


        int size = ((JSONArray) result.getRepresentation().get("faces")).size();
        assertTrue(size >= 0);
        assertTrue(size <= 1);
    }

    private static void checkResultEventually(Job result) {
        if (result.getStatus() == Resource.ACCEPTED) {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                result.fetch();
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }
        }
        checkResult(result);
    }

    @Test
    public void connect() {
        Root root;
        try {
            Configuration conf = new ConfigurationImpl();
            root = conf.connect();
            Service service = root.getServices()
                    .getService("face_detection", 1);
            assertNotNull(service);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededSync() throws Exception {
        service.enableSession();
        FakeCamera camera = new FakeCamera("data/video1");
        while (camera.hasNext()) {
            JSONObject parameters = new JSONObject();
            parameters.put("image", new File(camera.next()));
            Job job = service.process(parameters, false, callback1);
            checkResultEventually(job);
        }
        service.disableSession();
    }
}
