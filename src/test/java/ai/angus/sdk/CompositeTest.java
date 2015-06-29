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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import ai.angus.sdk.impl.ConfigurationImpl;
import ai.angus.sdk.impl.File;

public class CompositeTest {

    private static Root root;
    private static Service     services;
    private static Service     selectedServices;
    private static Service     selectedVersionServices;
    private static CheckResult callback1;
    private static Resource image;
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

        services = root.getServices().getServices();

        List<String> list = new ArrayList<String>();
        list.add("face_detection");
        list.add("dummy");
        selectedServices = root.getServices().getServices(list);

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("face_detection", 1);
        map.put("dummy", 1);

        selectedVersionServices = root.getServices().getServices(map);

        callback1 = new CheckResult(1);

        image = root.getBlobs().create(new File(IMG_1));
    }

    private static class CheckResult implements ResultCallBack {
        private int howmany;

        public CheckResult(int howmany) {
            this.howmany = howmany;
        }

        @Override
        public void callback(Job result) {
            CompositeTest.checkResult(result, howmany);
        }
    }

    private static void checkResult(Job result, int howmany) {
        assertEquals(result.getStatus(), 200);
        assertEquals(result.getRepresentation(), result.getResult());
        assertTrue(result.getRepresentation().containsKey("composite"));
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
        Job job = services.process(parameters, false, callback1);
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededDefault() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_1));
        Job job = services.process(parameters, callback1);
        checkResultEventually(job, 1);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void hrefSync() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", image);
        Job job = services.process(parameters, false, callback1);
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void embededAsync() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", new File(IMG_1));
        Job job = services.process(parameters, true, callback1);
        checkResultEventually(job, 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void hrefAsync() throws Exception {
        JSONObject parameters = new JSONObject();
        parameters.put("image", image);
        Job job = services.process(parameters, true, callback1);
        checkResultEventually(job, 1);
    }

}
