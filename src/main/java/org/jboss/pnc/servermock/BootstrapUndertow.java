/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.servermock;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.jboss.pnc.servermock.servlet.DelayedResponse;
import org.jboss.pnc.servermock.servlet.GetWhatYouWant;
import org.jboss.pnc.servermock.servlet.MockExecuteBuild;
import org.jboss.pnc.servermock.servlet.Welcome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.Manifest;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class BootstrapUndertow {

    private final Logger log = LoggerFactory.getLogger(BootstrapUndertow.class);

    private final String host;
    private final int port;

    private Undertow server;

    private static final String SERVLET_PATH = "/";

    public BootstrapUndertow(
            String host,
            int port,
            Consumer<Boolean> completionHandler) throws Exception {
        this.host = host;
        this.port = port;

        bootstrap(completionHandler);
    }

    private void bootstrap(final Consumer<Boolean> completionHandler) throws Exception {
        DeploymentInfo servletBuilder = deployment()
                .setClassLoader(BootstrapUndertow.class.getClassLoader())
                .setContextPath(SERVLET_PATH)
                .setDeploymentName("ROOT.war")
                .addServlets(
                        servlet("WelcomeServlet", Welcome.class)
                                .addMapping("/"),
                        servlet("GetWhatYouWant", GetWhatYouWant.class)
                                .addMapping("/get-what-you-want/*"),
                        servlet("MockExecuteBuild", MockExecuteBuild.class)
                                .addMapping("/pnc-rest/build-tasks/execute-build"),
                        servlet("DelayedResponse", DelayedResponse.class)
                                .addMappings("/pnc-rest/bpm/*", "/pnc-rest/build-tasks/*"));

        DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
        manager.deploy();

        HttpHandler servletHandler = null;
        try {
            servletHandler = manager.start();
        } catch (ServletException e) {
            throw new Exception("Cannot deploy servlets.", e);
        }

        PathHandler pathHandler = Handlers.path()
                .addPrefixPath(SERVLET_PATH, servletHandler);

        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(pathHandler)
                .build();

        server.start();

        completionHandler.accept(true);
    }

    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    private boolean pathMatches(String requestPath, String path) {
        return requestPath.equals(path) || (requestPath + "/").equals(path);
    }

    private String getManifestInformation() {
        String result = "";
        try {
            final Enumeration<URL> resources = Welcome.class.getClassLoader().getResources("META-INF/MANIFEST.MF");

            while (resources.hasMoreElements()) {
                final URL jarUrl = resources.nextElement();

                log.trace("Processing jar resource " + jarUrl);
                if (jarUrl.getFile().contains("build-agent")) {
                    final Manifest manifest = new Manifest(jarUrl.openStream());
                    result = manifest.getMainAttributes().getValue("Implementation-Version");
                    result += " ( SHA: " + manifest.getMainAttributes().getValue("Scm-Revision") + " ) ";
                    break;
                }
            }
        } catch (final IOException e) {
            log.trace( "Error retrieving information from manifest", e);
        }

        return result;
    }
}
