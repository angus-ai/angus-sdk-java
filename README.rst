Angus Java SDK's documentation
================================

Angus Java SDK is a Java client library for `Angus.ai <http://www.angus.ai>`_ Cloud.

Instalation
-----------

**Automatic installation**:

Angus is in the process of publishing to Maven central repository. 

**Build manually**: 

The Angus SDK source code is `hosted on GitHub <https://github.com/angus-ai/angus-sdk-java>`_.

Requirements: maven.

.. parsed-literal::
  $ git clone https://github.com/angus-ai/angus-sdk-java.git
  $ cd angus-sdk-java
  $ mvn clean compile assembly:single
  
You find the package in `target` repository (`angus-sdk-java-0.0.1-jar-with-dependencies.jar`)

**Initialize your credentials**: Angus SDK request the Angus.ai cloud to provide remote 
artificial intelligence algorithms. Access is restricted and you need some credentials
to be authorized. For a demo purpose you can use the same as the example.
When angus-sdk-java is installed, a new command is available.
This unique help you to configure your environment:

.. parsed-literal::
  $ java -jar target/angus-sdk-java-0.0.1-jar-with-dependencies.jar
  Please copy/paste your client_id: 
  7f5933d2-cd7c-11e4-9fe6-490467a5e114
  Please copy/paste your access_token: 
  db19c01e-18e5-4fc2-8b81-7b3d1f44533b
  $ 

You could explore all options by typing:

.. parsed-literal::
  $ java -jar target/angus-sdk-java-0.0.1-jar-with-dependencies.jar --help

Hello, world
------------

Here is a simple "Hello, world" example for Angus SDK (replace macgyver.jpg by your own image with a face to detect)::

  import java.io.IOException;
  import org.json.simple.JSONObject;
  import ai.angus.sdk.Configuration;
  import ai.angus.sdk.Job;
  import ai.angus.sdk.ProcessException;
  import ai.angus.sdk.Root;
  import ai.angus.sdk.Service;
  import ai.angus.sdk.impl.ConfigurationImpl;
  import ai.angus.sdk.impl.File;

  public class FaceDetect {

    public static void main(String[] args) throws IOException, ProcessException {
      Configuration conf = new ConfigurationImpl();

      Root root = conf.connect();
      Service service = root.getServices().getService("face_detection", 1);

      JSONObject params = new JSONObject();
      params.put("image", new File("./macgyver.jpg"));

      Job job = service.process(params);

      System.out.println(job.getResult().toJSONString());
    }
  }


Go further
----------

- Request your own credentials, currently send us an email at `contact@angus.ai <mailto:contact@angus.ai>`_
- The complete documentation is on the way.
- See "Discussion and support" bellow.


Discussion and support
----------------------

You can discuss Angus SDK on `the Angus SDK developer mailing list <https://groups.google.com/d/forum/angus-sdk-java-dev>`_, and report bugs on the `GitHub issue tracker <https://github.com/angus-ai/angus-sdk-java/issues>`_.

This web site and all documentation is licensed under `Creative
Commons 3.0 <http://creativecommons.org/licenses/by/3.0/>`_.

Angus Python SDK is Angus.ai open source technologies It is available under the `Apache License, Version 2.0. <https://www.apache.org/licenses/LICENSE-2.0.html>`_. Please read LICENSE and NOTICE files for more information.

Copyright 2015, Angus.ai
