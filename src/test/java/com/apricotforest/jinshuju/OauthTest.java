package com.apricotforest.jinshuju;

import com.apricotforest.jinshuju.comm.JinshujuException;
import com.apricotforest.jinshuju.storage.TokenStorage;
import com.apricotforest.jinshuju.storage.impl.RedisTokenStorage;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class OauthTest {

    @Ignore
    @Test
    public void test_oauth() throws JinshujuException {
        Oauth oauth = Oauth.custom().clientId("d92a6c8298d06b541cc163210ca3dba9481b16824d638d21c6def6c33d7f1931")
                .clientSecret("6fb32203fd6050e9f455721ecd27f1940b66130fd5d0c6d67713c54dd1d32f0d")
                .tokenUrl("https://account.uat.jinshuju.com/org_oauth/token")
                .redis("192.168.103.2", 6396)
                .build();

        System.out.println(oauth.getAccessToken());
        System.out.println(oauth.getRefreshToken());
        oauth.refreshToken(oauth.getRefreshToken());
        System.out.println("===========================");
        System.out.println(oauth.getAccessToken());
        System.out.println(oauth.getRefreshToken());
        System.out.println("scopes:" + oauth.getScopes());

    }

    @Ignore
    @Test
    public void test_init_token() throws JinshujuException {
        TokenStorage storage = new RedisTokenStorage("192.168.103.2", 6396);
        storage.store("6c16aee5c54b5f10ff9c4878cabddc37e151ef6f0c934151ca75788a9566eb98",
                "f0a35a2cd35dc6fd287e88252ec485d0729cf805b1e72f97a12ea9568b5614e8");
    }

}
