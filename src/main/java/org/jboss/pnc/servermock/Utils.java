package org.jboss.pnc.servermock;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Utils {

    public static String getRequestString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURL() + (queryString != null ? "?" + queryString : "");
    }

    public static void logAllParameters(HttpServletRequest request, Logger logger) throws IOException {
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            logger.debug("Parameter: " + entry.getKey() + "->");
            for (String value : entry.getValue()) {
                logger.debug("Value:" + value);
            }
        }

    }
}
