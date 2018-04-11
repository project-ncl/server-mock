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

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class GetWhatYouWant extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger(GetWhatYouWant.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("GetWhatYouWant servlet requested.");

        try {
            processRequest(request, response);
        } catch (Exception e) {
            log.error("Hey, you need to fix something.", e);
            response.sendError(500, e.getMessage());
        }

    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String callbackUrl = request.getParameter("callbackUrl");
        String callbackData = request.getParameter("callbackData");
        String callbackDelayMillis = request.getParameter("callbackDelayMillis");

        String responseData = request.getParameter("responseData");
        String responseDelayMillis = request.getParameter("responseDelayMillis");

        if (callbackUrl != null) {
            long delayMillis = 0;
            if (callbackDelayMillis != null) {
                delayMillis = Long.parseLong(callbackDelayMillis);
            }
            CallbackProcessor.instance().process(callbackUrl, callbackData, delayMillis);
        }

        if (responseDelayMillis != null) {
            Thread.sleep(Integer.parseInt(responseDelayMillis));
        }

        response.setStatus(200);
        if (responseData != null) {
            response.getWriter().print(responseData);
        }
    }


}
