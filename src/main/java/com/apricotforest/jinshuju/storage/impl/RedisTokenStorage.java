package com.apricotforest.jinshuju.storage.impl;

import com.apricotforest.jinshuju.storage.TokenStorage;
import com.apricotforest.jinshuju.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * redis存储结构:
 * <p>
 *                  | access_token:  SIDKWU82FDS2...
 * Key: jinshuju -> |
 *                  | refresh_token: AL1259WHS45O...
 */
public class RedisTokenStorage implements TokenStorage {
    private static final Logger LOG = LoggerFactory.getLogger(RedisTokenStorage.class);

    private static final String REDIS_OAUTH_TOKEN_KEY = "jinshuju";
    private Executor executor = Executors.newSingleThreadExecutor();
    private Jedis jedis;

    public RedisTokenStorage(String host, int port) {
        LOG.info("Start to init redis-token storage. -> host:{}, port:{}", host, port);
        jedis = new Jedis(host, port);
    }

    public String getAccessToken() {
        return jedis.hget(REDIS_OAUTH_TOKEN_KEY, "access_token");
    }

    public String getRefreshToken() {
        return jedis.hget(REDIS_OAUTH_TOKEN_KEY, "refresh_token");
    }

    public void store(String accessToken, String refreshToken) {
        if (StringUtils.isBlank(accessToken) || StringUtils.isBlank(refreshToken)) {
            return;
        }
        executor.execute(() -> {
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", accessToken);
            tokens.put("refresh_token", refreshToken);
            this.jedis.hmset(REDIS_OAUTH_TOKEN_KEY, tokens);
        });
    }



}
