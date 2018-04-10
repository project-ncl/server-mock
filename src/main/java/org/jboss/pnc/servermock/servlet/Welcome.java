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

package org.jboss.pnc.servermock.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Welcome extends HttpServlet {

    Logger log = LoggerFactory.getLogger(Welcome.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("Welcome servlet requested.");
        PrintWriter out = response.getWriter();
        out.print("Welcome to the Machine!");
        out.print("\n\n You can call /get-what-you-want with this parameters: "
                + "callbackUrl, callbackData, callbackDelayMillis, "
                + "responseData, responseDelayMillis, "
                + "threadPoolSize."
                + "I guess it's clear what they do, otherwise you know where to look ;)."
                + "\n\nExample url: http://localhost:8080/get-what-you-want?responseData=Hi!&callbackUrl=http://localhost:8080/&callbackDelayMillis=5000");
        out.close();
    }
}
