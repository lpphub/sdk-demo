package com.apricotforest.jinshuju.process;

import com.alibaba.fastjson.JSONObject;
import com.apricotforest.jinshuju.Oauth;
import com.apricotforest.jinshuju.comm.Constants;
import com.apricotforest.jinshuju.comm.JinshujuException;
import com.apricotforest.jinshuju.http.Response;
import com.apricotforest.jinshuju.model.FormInfo;
import com.apricotforest.jinshuju.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 当access_token为个人时, openid可为空
 * <p>
 * Scope: forms, read_entries
 */
public class FormApi extends BaseApi {
    private String API = Constants.API_DOMAIN + "forms/";

    public FormApi(Oauth oauth) {
        super(oauth);
    }

    public FormApi(Oauth oauth, String apiDomain) {
        super(oauth);
        this.API = apiDomain + "forms/";
    }

    public List<FormInfo> getFormsByOpenId(String openId) throws JinshujuException {
        StringBuilder url = new StringBuilder(API);
        if (StringUtils.isNotBlank(openId)) {
            url.append("?openid=").append(openId);
        }
        return JSONObject.parseArray(client.get(url.toString()).asString(), FormInfo.class);
    }

    public Response getFormsByPageUrl(String url) throws JinshujuException {
        return client.get(url);
    }

    public String getOneForm(String formToken) throws JinshujuException {
        return client.get(API + formToken).asString();
    }

    public String copy(String formToken, String openId) throws JinshujuException {
        return copy(formToken, openId, null);
    }

    public String copy(String formToken, String openId, String name) throws JinshujuException {
        Map<String, String> parameters = new HashMap<>();
        if (StringUtils.isNotBlank(openId)) {
            parameters.put("openid", openId);
        }
        if (StringUtils.isNotBlank(name)) {
            parameters.put("name", name);
        }
        return client.post(API + formToken + "/copy", parameters).asString();
    }

    public void delete(String formToken) throws JinshujuException {
        client.delete(API + formToken);
    }

    public String status(String formToken) throws JinshujuException {
        return client.get(API + formToken + "/status").asString();
    }

    public String getSetting(String formToken) throws JinshujuException {
        return client.get(API + formToken + "/setting").asString();
    }

    public void updateSetting(String formToken, String redirectUrl, String redirectFields, String pushUrl) throws JinshujuException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("success_redirect_url", redirectUrl);
        parameters.put("success_redirect_fields", redirectFields);
        parameters.put("push_url", pushUrl);
        client.put(API + formToken + "/setting", parameters);
    }

    public void addCooperator(String formToken, String openId, String role) throws JinshujuException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("openid", openId);
        parameters.put("role", role);
        client.post(API + formToken + "/cooperators", parameters);
    }

    public void changeCooperator(String formToken, String openId, String role) throws JinshujuException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("role", role);
        client.put(API + formToken + "/cooperators/" + openId, parameters);
    }

    public void deleteCooperator(String formToken, String openId) throws JinshujuException {
        client.delete(API + formToken + "/cooperators/" + openId);
    }

    public String getOneEntry(String formToken, String serialNumber) throws JinshujuException {
        return client.get(API + formToken + "/entries/" + serialNumber).asString();
    }

    public String getEntries(String formToken) throws JinshujuException {
        return client.get(API + formToken + "/entries").asString();
    }
}
