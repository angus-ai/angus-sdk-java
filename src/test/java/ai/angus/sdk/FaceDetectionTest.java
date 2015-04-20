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
import static org.junit.Assert.assertFalse;
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

public class FaceDetectionTest {

    private static Root root;
    private static Service service;
    private static CheckResult callback1, callback3, callback43;
    private static Resource image;
    private static String IMG_1 = "data/Angus-6.jpg";
    private static String IMG_3 = "data/Angus-24.jpg";
    private static String IMG_LARGE = "data/large.jpg";

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
        callback1 = new CheckResult(1);
        callback3 = new CheckResult(3);
        callback43 = new CheckResult(43);

        image = root.getBlobs().create(new File(IMG_1));
    }

    private static class CheckResult implements ResultCallBack {
        private int howmany;

        public CheckResult(int howmany) {
            this.howmany = howmany;
        }

        @Override
        public void callback(Job result) {
            FaceDetectionTest.checkResult(result, howmany);
        }
    }

    private static void checkResult(Job result, int howmany) {
        assertEquals(result.getStatus(), Resource.CREATED);
        assertEquals(result.getRepresentation(), result.getResult());
        assertTrue(result.getRepresentation().containsKey("faces"));

        double min = Math.ceil(0.5 * howmany);
        double max = Math.floor(1.5 * howmany);

        int size = ((JSONArray) result.getRepresentation().get("faces")).size();
        assertTrue(size >= min);
        assertTrue(size <= max);

    }

    private static void checkResultEventually(Job result, int howmany) {
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
        checkResult(result, howmany);
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
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_1));
        Job job = service.process(parameters, false, callback1);
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededDefault() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_1));
        Job job = service.process(parameters, callback1);
        checkResultEventually(job, 1);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededSync3() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_3));
        Job job = service.process(parameters, false, callback3);
        checkResultEventually(job, 3);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void hrefSync() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", image);
        Job job = service.process(parameters, false, callback1);
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededAsync() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_1));
        Job job = service.process(parameters, true, callback1);
        assertEquals(job.getStatus(), Resource.ACCEPTED);
        assertFalse(job.getRepresentation().containsKey("faces"));
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededAsyncLarge() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_LARGE));
        Job job = service.process(parameters, true, callback43);
        assertEquals(job.getStatus(), Resource.ACCEPTED);
        assertFalse(job.getRepresentation().containsKey("faces"));
        checkResultEventually(job, 43);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void hrefAsync() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", image);
        Job job = service.process(parameters, true, callback1);
        assertEquals(job.getStatus(), Resource.ACCEPTED);
        assertFalse(job.getRepresentation().containsKey("faces"));
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void localUploadFile() throws Exception {
        if (root.getURL().getHost().equals("localhost")) {
            JSONObject parameters = new JSONObject();
            parameters.put("image",
                    "file://" + (new java.io.File(IMG_1)).getAbsolutePath());
            Job job = service.process(parameters, false, callback1);
            checkResultEventually(job, 1);
        }
    }
}
