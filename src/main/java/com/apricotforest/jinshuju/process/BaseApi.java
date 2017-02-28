package com.apricotforest.jinshuju.process;

import com.apricotforest.jinshuju.Oauth;
import com.apricotforest.jinshuju.http.HttpClient;

public abstract class BaseApi {
    protected HttpClient client;

    protected BaseApi(Oauth oauth) {
        client = new HttpClient(oauth);
    }

}
