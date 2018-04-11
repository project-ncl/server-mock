package org.jboss.pnc.servermock.servlet;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class MockExecuteBuild extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(MockExecuteBuild.class);

    private String responseDelayMillis;
    private String callbackDelayMillis;

    private String bpmUsername;
    private String bpmPass;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        responseDelayMillis = System.getProperty("responseDelayMillis");
        callbackDelayMillis = System.getProperty("callbackDelayMillis");

        bpmUsername = System.getProperty("bpmUsername");
        bpmPass = System.getProperty("bpmPass");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("MockExecuteBuild servlet requested.");

        try {
            processRequest(request, response);
        } catch (Exception e) {
            log.error("Hey, you need to fix something.", e);
            response.sendError(500, e.getMessage());
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String callbackUrl = request.getParameter("callbackUrl");

        if (callbackUrl != null) {
            executeBuildCallback(callbackUrl);
        }

        if (responseDelayMillis != null) {
            Thread.sleep(Integer.parseInt(responseDelayMillis));
        }

        response.setStatus(200);
    }

    private String getAuthHeader() {
        byte[] encodedBytes = Base64.encodeBase64((bpmUsername + ":" + bpmPass).getBytes());
        return "Basic " + new String(encodedBytes);
    }

    private void executeBuildCallback(String url) {
        HttpPost request = new HttpPost(url);

        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("event", "{\"error\", \" Response from MOCK.\"}"));

        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(parameters);
        } catch (UnsupportedEncodingException e) {
            log.error("Error occurred preparing callback request.", e);
        }
        request.setEntity(entity);
        request.addHeader("Authorization", getAuthHeader());

        CallbackProcessor.instance().process(request, Long.parseLong(callbackDelayMillis));
    }
}
