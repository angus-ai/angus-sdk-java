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
package ai.angus.sdk.http.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import ai.angus.sdk.Configuration;
import ai.angus.sdk.http.HTTPClient;

public class HTTPClientImpl implements HTTPClient {
    private SSLSocketFactory sslFactory;
    private String auth64;

    public HTTPClientImpl(Configuration conf) {
        try {
            String auth = conf.getClientId() + ":" + conf.getAccessToken();
            auth64 = DatatypeConverter
                    .printBase64Binary(auth.getBytes("UTF-8"));
            initSSLSocketFactory(conf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSSLSocketFactory(Configuration conf) throws Exception {
        InputStream inStream = null;

        inStream = new FileInputStream(conf.getCaPath());
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // cert = (X509Certificate) cf.generateCertificate(inStream);
        Collection<? extends Certificate> certs = cf
                .generateCertificates(inStream);

        if (inStream != null) {
            inStream.close();
        }

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);

        int c = 0;
        for (Certificate cert : certs) {
            keyStore.setCertificateEntry("cert-" + (c++), cert);
        }

        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, tmf.getTrustManagers(), null);
        sslFactory = ctx.getSocketFactory();
    }

    @Override
    public JSONObject get(URL url, Map<String, String> filters)
            throws IOException {
        // TODO currently no filters
        return this.get(url);
    }

    @Override
    public JSONObject get(URL url) throws IOException {
        HttpURLConnection connection = null;

        if (url.getProtocol().equals("https")) {
            HttpsURLConnection sconnection = (HttpsURLConnection) url
                    .openConnection();
            sconnection.setSSLSocketFactory(sslFactory);
            connection = sconnection;
        } else if (url.getProtocol().equals("http")) {
            connection = (HttpURLConnection) url.openConnection();
        }

        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(false);
        connection.setRequestProperty("Authorization", "Basic " + auth64);

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        JSONObject result = (JSONObject) JSONValue.parse(rd);

        return result;
    }

    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();

    protected String generateBoundary() {
        final StringBuilder buffer = new StringBuilder();
        final Random rand = new Random();
        final int count = rand.nextInt(11) + 30; // a random size from 30 to 40
        for (int i = 0; i < count; i++) {
            buffer.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        return buffer.toString();
    }

    @Override
    public JSONObject post(URL url, byte[] params, Map<String, File> files)
            throws IOException {
        String boundary = generateBoundary();
        HttpURLConnection connection = null;

        if (url.getProtocol().equals("https")) {
            HttpsURLConnection sconnection = (HttpsURLConnection) url
                    .openConnection();
            sconnection.setSSLSocketFactory(sslFactory);
            connection = sconnection;
        } else if (url.getProtocol().equals("http")) {
            connection = (HttpURLConnection) url.openConnection();
        }

        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "Basic " + auth64);
        connection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + boundary);

        OutputStream os = connection.getOutputStream();

        MultiPartBody parts = new MultiPartBody(boundary);
        /* FILE */
        for (Iterator<Entry<String, File>> i = files.entrySet().iterator(); i
                .hasNext();) {
            Entry<String, File> e = i.next();
            parts.addPart(e.getKey(), e.getValue());
        }

        /* META */
        parts.addPart("meta", params, false);

        parts.writeBodies(os);

        os.flush();
        os.close();

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        JSONObject result = (JSONObject) JSONValue.parse(rd);
        return result;
    }
}
