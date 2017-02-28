package com.apricotforest.jinshuju;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.apricotforest.jinshuju.comm.Constants;
import com.apricotforest.jinshuju.comm.JinshujuException;
import com.apricotforest.jinshuju.utils.HttpUtils;
import com.apricotforest.jinshuju.storage.TokenStorage;
import com.apricotforest.jinshuju.storage.impl.RedisTokenStorage;
import com.apricotforest.jinshuju.utils.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 金数据 企业级oauth认证 ==>(important!!!! 目前使用该类时只能封装成单例)
 * <p>
 * 默认从redis中取access_token与refresh_token, 然后利用refresh来不断刷新token, 不再从金数据授权
 */
public class Oauth {
    private static final Logger LOG = LoggerFactory.getLogger(Oauth.class);

    private String accessToken;
    private String refreshToken;
    private String tokenUrl = Constants.ORG_TOKEN_URL;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String scopes;

    private TokenStorage tokenStorage;

    public Oauth(String tokenUrl, String clientId, String clientSecret, String redirectUrl, TokenStorage tokenStorage) {
        if (StringUtils.isNotBlank(tokenUrl)) {
            this.tokenUrl = tokenUrl;
        }
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.tokenStorage = tokenStorage;
        this.accessToken = tokenStorage.getAccessToken();
        this.refreshToken = tokenStorage.getRefreshToken();
    }

    public String authorize(String url, String scope) {
        return new StringBuilder(url).append("?client_id=").append(clientId)
                .append("&redirect_uri=").append(redirectUrl).append("&response_type=code")
                .append("&state=1").append("&scope=").append(scope).toString();
    }

    public String accessToken(String code) throws JinshujuException {
        try {
            HttpResponse response = send(tokenUrl, Form.form().add("code", code).add("grant_type", "authorization_code")
                    .add("client_id", clientId).add("client_secret", clientSecret)
                    .add("redirect_uri", redirectUrl).build());
            parseJson(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            throw new JinshujuException("获取token异常", e);
        }
        return accessToken;
    }

    public String refreshToken() throws JinshujuException {
        return refreshToken(this.refreshToken);
    }

    public String refreshToken(String refresh) throws JinshujuException {
        if (StringUtils.isBlank(refresh)) {
            throw new JinshujuException("no refresh_token");
        }
        try {
            HttpResponse response = send(tokenUrl, Form.form().add("grant_type", "refresh_token").add("client_id", clientId)
                    .add("client_secret", clientSecret).add("refresh_token", refresh).build());
            if (401 == response.getStatusLine().getStatusCode()) {
                LOG.info("refresh token from storage...");
                updateTokenFromStorage();
            } else {
                parseJson(EntityUtils.toString(response.getEntity()));
                LOG.info("refresh token from jinshuju -> access_token:{}, refresh_token:{}", accessToken, refreshToken);
                tokenStorage.store(this.accessToken, this.refreshToken);
            }
        } catch (IOException e) {
            throw new JinshujuException("刷新token异常", e);
        }
        return accessToken;
    }

    public void updateTokenFromStorage() {
        this.accessToken = tokenStorage.getAccessToken();
        this.refreshToken = tokenStorage.getRefreshToken();
    }

    private CloseableHttpResponse send(String url, List<NameValuePair> forms) throws JinshujuException {
        if (StringUtils.isBlank(url)) {
            throw new JinshujuException("no token_url");
        }
        HttpPost post = new HttpPost(url);
        CloseableHttpClient client = HttpUtils.getClientWithSSL();
        post.setConfig(RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000).build());
        try {
            post.setEntity(new UrlEncodedFormEntity(forms, "UTF-8"));
            return client.execute(post);
        } catch (Exception e) {
            throw new JinshujuException(e);
        }
    }

    private void parseJson(String json) {
        Map<String, String> data = JSONObject.parseObject(json, new TypeReference<Map<String, String>>() {
        });
        this.accessToken = data.get("access_token");
        this.scopes = data.get("scope");
        this.refreshToken = data.get("refresh_token");
    }

    public String getScopes() {
        return this.scopes;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static Builder custom() {
        return new Builder();
    }

    public static class Builder {
        private String tokenUrl;
        private String clientId;
        private String clientSecret;
        private String redirectUrl;
        private TokenStorage storage;

        public Builder tokenUrl(String tokenUrl) {
            this.tokenUrl = tokenUrl;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder redirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
            return this;
        }

        public Builder storage(TokenStorage storage) {
            this.storage = storage;
            return this;
        }

        public Builder redis(String host, int port) {
            this.storage = new RedisTokenStorage(host, port);
            return this;
        }

        public Oauth build() throws JinshujuException {
            return new Oauth(this.tokenUrl, this.clientId, this.clientSecret, this.redirectUrl, this.storage);
        }
    }
}
