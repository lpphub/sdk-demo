package com.apricotforest.jinshuju.http;

import com.apricotforest.jinshuju.Oauth;
import com.apricotforest.jinshuju.comm.JinshujuException;
import com.apricotforest.jinshuju.utils.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * qa环境无法忽略证书, 改用httpclient
 */
public final class Client {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    private final static int SOCKET_TIMEOUT = 5000;
    private final static int CONNECTION_TIMEOUT = 5000;
    private Oauth oauth;

    public Client(Oauth oauth) {
        this.oauth = oauth;
    }

    public Response get(String url) throws JinshujuException {
        return send(Request.Get(url), null);
    }

    public Response post(String url, Map<String, String> parameters) throws JinshujuException {
        return post(url, parameters, null);
    }

    public Response post(String url, Map<String, String> parameters, Map<String, String> headers) throws JinshujuException {
        Request request = Request.Post(url).bodyForm(buildForm(parameters));
        return send(request, headers);
    }

    public Response put(String url, Map<String, String> parameters) throws JinshujuException {
        return send(Request.Put(url).bodyForm(buildForm(parameters)), null);
    }

    public Response delete(String url) throws JinshujuException {
        return send(Request.Delete(url), null);
    }

    private Response send(Request request, Map<String, String> headers) throws JinshujuException {
        if (StringUtils.isBlank(oauth.getAccessToken())) {
            throw new JinshujuException("no access_token");
        }
        request.addHeader("Authorization", "bearer " + oauth.getAccessToken());
        request.socketTimeout(SOCKET_TIMEOUT).connectTimeout(CONNECTION_TIMEOUT);
        if (null != headers) {
            headers.forEach(request::addHeader);
        }
        try {
            HttpResponse response = request.execute().returnResponse();
            if (401 == response.getStatusLine().getStatusCode()) {
                // access_token过期, 刷新重试一次
                request.setHeader("Authorization", "bearer " + oauth.refreshToken());
                LOG.info("access_token过期, 刷新重试 -> new_access_token: {}", oauth.getAccessToken());
                response = request.execute().returnResponse();
            }
            return Response.create(response);
        } catch (IOException e) {
            throw new JinshujuException(e);
        }
    }

    private List<NameValuePair> buildForm(Map<String, String> parameters) {
        if (null != parameters && parameters.size() > 0) {
            Form form = Form.form();
            parameters.forEach(form::add);
            return form.build();
        }
        return Collections.emptyList();
    }

    public void setOauth(Oauth oauth) {
        this.oauth = oauth;
    }
}
