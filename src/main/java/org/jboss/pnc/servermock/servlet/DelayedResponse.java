package org.jboss.pnc.servermock.servlet;

import org.jboss.pnc.servermock.Configuration;
import org.jboss.pnc.servermock.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class DelayedResponse extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(DelayedResponse.class);

    private String responseDelayMillis;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        responseDelayMillis = Configuration.get("responseDelayMillis");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("DelayedResponse servlet requested: {}", Utils.getRequestString(request));
        Utils.logAllParameters(request, log);

        if (responseDelayMillis != null) {
            try {
                Thread.sleep(Integer.parseInt(responseDelayMillis));
            } catch (Exception e) {
                log.error("Hey, you need to fix something.", e);
                response.sendError(500, e.getMessage());
            }
        }
        response.setStatus(200);
    }
}
