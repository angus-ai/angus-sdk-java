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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AngusMe {

    public static String ANGUS_PATH = ".angusdk";
    public static File FULL_PATH = new File(System.getProperty("user.home"),
            ANGUS_PATH);
    public static File certificates = new File(FULL_PATH, "certificate.pem");
    public static File config = new File(FULL_PATH, "config.json");

    public static String CONFIRMATION = "Your angus configuration directory ("
            + FULL_PATH.getAbsolutePath()
            + ") already exists, do you want regenerate it (y/N) ?";

    public static String SUCCESS = "Configuration directory successfully"
            + " created in (" + FULL_PATH.getAbsolutePath()
            + "), credentials can be modified there directly";

    public static void show() {
        if (!FULL_PATH.exists()) {
            System.out.println("No angus configuration");
            System.exit(-1);
        }
        System.out.println("Java version:");
        System.out.println(System.getProperty("java.version"));

        BufferedReader r;

        String line;
        try {
            r = new BufferedReader(new FileReader(certificates));
            line = r.readLine();
            while (line != null) {
                System.out.println(line);
                line = r.readLine();
            }
            r = new BufferedReader(new FileReader(config));
            line = r.readLine();
            while (line != null) {
                System.out.println(line);
                line = r.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public static void delete() {

        if (FULL_PATH.exists()) {
            deleteDirectory(FULL_PATH);
        }
    }

    public static void update() {
        String clientId = "None";
        String accessToken = "None";
        Scanner scanner = new Scanner(System.in);
        JSONObject conf;

        if (!FULL_PATH.exists()) {
            FULL_PATH.mkdir();
        } else {

            System.out.println(CONFIRMATION);
            String accept = scanner.nextLine();
            if (!accept.startsWith("y") && !accept.startsWith("Y")) {
                show();
                System.exit(0);
            }
            JSONParser parser = new JSONParser();
            try {
                conf = (JSONObject) parser.parse(new FileReader(config));
                clientId = (String) conf.get("client_id");
                accessToken = (String) conf.get("access_token");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        InputStream from = AngusMe.class.getClassLoader().getResourceAsStream(
                "certificate.pem");

        try {
            Files.copy(from, certificates.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line;
        System.out.println("Please copy/paste your client_id (current: "
                + clientId + "): ");

        line = scanner.nextLine();
        if (!"".equals(line)) {
            clientId = line;
        }

        System.out.println("Please copy/paste your access_token (current: "
                + accessToken + "): ");

        line = scanner.nextLine();
        if (!"".equals(line)) {
            accessToken = line;
        }

        scanner.close();

        conf = new JSONObject();
        conf.put("client_id", clientId);
        conf.put("access_token", accessToken);
        conf.put("ca_path", certificates.getAbsolutePath());
        conf.put("default_root", "https://gate.angus.ai");
        try {
            FileWriter writer = new FileWriter(config);
            conf.writeJSONString(writer);
            writer.close();
            System.out.println(SUCCESS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("AngusMe");
        parser.description("Angus SDK configurator");

        parser.addArgument("-s", "--show").action(Arguments.storeTrue())
                .help("display current configuration if exists");

        parser.addArgument("-d", "--delete").action(Arguments.storeTrue())
                .help("remove current configuration if exists");

        try {
            Namespace res = parser.parseArgs(args);
            if (res.getBoolean("show")) {
                show();
            } else if (res.getBoolean("delete")) {
                delete();
            } else {
                update();
            }
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }
    }

}
