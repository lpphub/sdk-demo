package com.apricotforest.jinshuju.http;

import com.apricotforest.jinshuju.Oauth;
import com.apricotforest.jinshuju.comm.JinshujuException;
import com.apricotforest.jinshuju.utils.HttpUtils;
import com.apricotforest.jinshuju.utils.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 加入忽略证书验证
 */
public class HttpClient {
    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private Oauth oauth;
    private CloseableHttpClient client;

    public HttpClient(Oauth oauth) {
        this.oauth = oauth;
        RequestConfig config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).build();
//        client = HttpClients.custom().setDefaultRequestConfig(config)
//                .setSSLContext(getSSLContext()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
        client = HttpUtils.getClientWithSSL(config);
    }

    public Response get(String url) throws JinshujuException {
        return get(url, null);
    }

    public Response get(String url, Map<String, String> headers) throws JinshujuException {
        HttpGet get = new HttpGet(url);
        addHeaders(get, headers);
        return send(get);
    }

    public Response post(String url, Map<String, String> parameters) throws JinshujuException {
        return post(url, parameters, null);
    }

    public Response post(String url, Map<String, String> parameters, Map<String, String> headers) throws JinshujuException {
        HttpPost post = new HttpPost(url);
        addHeaders(post, headers);
        bodyForm(post, parameters);
        return send(post);
    }

    public Response put(String url, Map<String, String> parameters) throws JinshujuException {
        HttpPut put = new HttpPut(url);
        bodyForm(put, parameters);
        return send(put);
    }

    public Response delete(String url) throws JinshujuException {
        return send(new HttpDelete(url));
    }

    private Response send(HttpUriRequest request) throws JinshujuException {
        try {
            if (StringUtils.isBlank(oauth.getAccessToken())) {
                throw new JinshujuException("no access_token");
            }
            request.addHeader("Authorization", "bearer " + oauth.getAccessToken());
            CloseableHttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if (401 == code) {
                // access_token过期, 刷新重试一次
                LOG.info("access_token expire, refresh token and retry. url:{}", request.getURI().getPath());
                request.setHeader("Authorization", "bearer " + oauth.refreshToken());
                response = client.execute(request);
            }
            return Response.create(response);
        } catch (Exception e) {
            throw new JinshujuException("httpclient execute error", e);
        }
    }

    private void addHeaders(HttpUriRequest request, Map<String, String> headers) {
        if (headers != null && headers.size() > 0) {
            headers.forEach(request::addHeader);
        }
    }

    private void bodyForm(HttpEntityEnclosingRequestBase request, Map<String, String> parameters) throws JinshujuException {
        if (null != parameters && parameters.size() > 0) {
            List<NameValuePair> params = new ArrayList<>();
            parameters.forEach((k, v) -> params.add(new BasicNameValuePair(k, v)));
            try {
                request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new JinshujuException(e);
            }
        }
    }
}
