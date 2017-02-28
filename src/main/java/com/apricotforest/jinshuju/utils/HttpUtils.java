package com.apricotforest.jinshuju.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;

/**
 * 辅助获取忽略https证书client
 */
public class HttpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

    private HttpUtils() {
    }

    public static CloseableHttpClient getClientWithSSL() {
        return HttpClients.custom().setSSLContext(getSSLContext())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
    }

    public static CloseableHttpClient getClientWithSSL(RequestConfig config) {
        return HttpClients.custom().setDefaultRequestConfig(config).setSSLContext(getSSLContext())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
    }

    public static SSLContext getSSLContext() {
        try {
            return SSLContexts.custom().loadTrustMaterial(null, (x509Certificates, s) -> true).build();
        } catch (Exception e) {
            LOG.error("getSSlContext error:", e);
        }
        return null;
    }
}
