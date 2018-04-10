package org.jboss.pnc.servermock.servlet;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class CallbackProcessor {

    private static Logger log = LoggerFactory.getLogger(CallbackProcessor.class);


    private static Map<String, CallbackProcessor> instance = new ConcurrentHashMap<>();

    ScheduledExecutorService executorService;

    private CallbackProcessor(int threadPoolSize) {
        executorService = Executors.newScheduledThreadPool(threadPoolSize);
    }

    public static void configure(int threadPoolSize) {
        CallbackProcessor.instance(threadPoolSize);
    }

    public static CallbackProcessor instance() {
        return instance(20);
    }

    public static CallbackProcessor instance(int threadPoolSize) {
        return instance.computeIfAbsent("instance", (k) -> new CallbackProcessor(threadPoolSize));
    }

    public void process(String callbackUrl, String callbackData, String callbackDelayMillis) throws Exception {
        long delay = 0;
        if (callbackDelayMillis != null) {
            delay = Long.parseLong(callbackDelayMillis);
        }
        instance().executorService.schedule(() -> request(callbackUrl, callbackData), delay, TimeUnit.MILLISECONDS);
    }

    private void request(String callbackUrl, String callbackData) throws RuntimeException {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String responseBody;
            if (callbackData != null) {
                HttpPost httpPost = new HttpPost(callbackUrl);
                HttpEntity entity = new ByteArrayEntity(callbackData.getBytes());
                httpPost.setEntity(entity);
                responseBody = httpclient.execute(httpPost, getStringResponseHandler(callbackUrl));
            } else {
                HttpGet httpGet = new HttpGet(callbackUrl);
                responseBody = httpclient.execute(httpGet, getStringResponseHandler(callbackUrl));
            }
            log.info("Callback response for " + callbackUrl + " received: " + responseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private ResponseHandler<String> getStringResponseHandler(String callbackUrl) {
        return response -> {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response from " + callbackUrl + " status: " + status);
            }
        };
    }

}
