package org.jboss.pnc.servermock;

import org.slf4j.Logger;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Utils {

    public static String getRequestString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return request.getRequestURL() + (queryString != null ? "?" + queryString : "");
    }

    public static void logRawRequest(HttpServletRequest request, Logger logger) throws IOException {
        try (ServletInputStream inputStream = request.getInputStream()) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)){
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String rawRequest = bufferedReader.lines().collect(Collectors.joining("\n"));
                    logger.debug("RAW Request: " + rawRequest);
                }
            }
        }
    }
}
