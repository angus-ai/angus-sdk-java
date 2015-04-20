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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MultiPartBody {
    private static String CRLF = "\r\n";
    private static String TWOHIPHENS = "--";
    private String boundary = null;

    private static class Part {
        public String name;

        Part(String name) {
            this.name = name;
        }
    }

    private class RawPart extends Part {
        public byte[] body;
        public boolean isFile;

        RawPart(String name, byte[] body, boolean isFile) {
            super(name);
            this.body = body;
            this.isFile = isFile;
        }
    }

    private class FilePart extends Part {
        public File file;

        FilePart(String name, File file) {
            super("attachment://" + name);
            this.file = file;
        }
    }

    private Map<String, Part> parts;

    public MultiPartBody(String boundary) {
        this.boundary = boundary;
        this.parts = new HashMap<String, MultiPartBody.Part>();
    }

    public void addPart(String name, File file) {
        parts.put(name, new FilePart(name, file));
    }

    public void addPart(String name, byte[] body, boolean isFile) {
        parts.put(name, new RawPart(name, body, isFile));
    }

    public void writeBodies(OutputStream output) throws IOException {
        Writer out = new OutputStreamWriter(output);
        for (Iterator<Part> ip = parts.values().iterator(); ip.hasNext();) {
            Part part = ip.next();
            out.write(TWOHIPHENS + boundary + CRLF);
            out.write("Content-Disposition: form-data; name=\"" + part.name
                    + "\";");
            if (part instanceof RawPart) {
                RawPart rPart = (RawPart) part;
                if (rPart.isFile) {
                    out.write(" filename=\"" + part.name + "\";");
                }
                out.write(CRLF);
                out.write(CRLF);
                out.flush(); // Important for no interleav with output
                output.write(rPart.body);
            } else if (part instanceof FilePart) {
                FilePart fPart = (FilePart) part;
                out.write(" filename=\"" + part.name + "\";");
                out.write(CRLF);
                out.write(CRLF);
                out.flush(); // Important for no interleav with output
                Files.copy(fPart.file.toPath(), output);
            }
            out.write(CRLF);
        }
        out.write((TWOHIPHENS + boundary + TWOHIPHENS + CRLF));
        out.flush();
    }
}
