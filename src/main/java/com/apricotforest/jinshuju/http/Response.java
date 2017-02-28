package com.apricotforest.jinshuju.http;

import com.apricotforest.jinshuju.utils.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Response {
    public int statusCode;
    public String error;
    private byte[] body;
    private HttpResponse response;
    private Map<String, String> headers;

    public Response(HttpResponse response, int statusCode, String error, byte[] body) {
        this(response, statusCode, error, body, null);
    }

    public Response(HttpResponse response, int statusCode, String error, byte[] body, Map<String, String> headers) {
        this.response = response;
        this.statusCode = statusCode;
        this.error = error;
        this.body = body;
        this.headers = headers;
    }

    public static Response create(HttpResponse response) {
        int code = -1;
        String err = null;
        byte[] body = null;
        Map<String, String> headerMap = new HashMap<>();
        try {
            code = response.getStatusLine().getStatusCode();
            body = EntityUtils.toByteArray(response.getEntity());
            for (Header header : response.getAllHeaders()) {
                headerMap.put(header.getName(), header.getValue());
            }
        } catch (IOException e) {
            err = e.getMessage();
        }
        return new Response(response, code, err, body, headerMap);
    }

    public String asString() {
        try {
            return new String(this.body, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        return null;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public boolean isOK() {
        return statusCode == 200 && StringUtils.isBlank(error);
    }

}
