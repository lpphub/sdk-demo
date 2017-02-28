package com.apricotforest.jinshuju.storage;

public interface TokenStorage {

    String getAccessToken();

    String getRefreshToken();

    void store(String accessToken, String refreshToken);
}
