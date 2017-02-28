package com.apricotforest.jinshuju.process;

import com.apricotforest.jinshuju.Oauth;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by shanyingbo on 17/2/22.
 */
@Ignore
public class FormApiTest {


    FormApi formApi = null;

    @Before
    public void setUp() throws Exception {
        Oauth oauth = Oauth.custom().clientId("d92a6c8298d06b541cc163210ca3dba9481b16824d638d21c6def6c33d7f1931")
                .clientSecret("6fb32203fd6050e9f455721ecd27f1940b66130fd5d0c6d67713c54dd1d32f0d")
                .tokenUrl("https://account.uat.jinshuju.com/org_oauth/token")
                .redis("192.168.103.2", 6396)
                .build();
        formApi = new FormApi(oauth, "https://api.uat.jinshuju.com/v4/");
    }

    @Test
    public void test_update_setting() throws Exception {
        formApi.updateSetting("V9gPbH", "www.sina.com.cn", "serial_number x_field_1", "http://qa-caseform.xingshulin.com/form/jinshuju/hook");
    }

    @Test
    public void test_get_one_form() throws Exception {
        System.out.println(formApi.getOneForm("V9gPbH"));
    }
}