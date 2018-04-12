package org.jboss.pnc.servermock;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Utils {

    public static String getRequestString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURL() + (queryString != null ? "?" + queryString : "");
    }
}
