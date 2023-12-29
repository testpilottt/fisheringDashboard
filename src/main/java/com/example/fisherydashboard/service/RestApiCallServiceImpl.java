package com.example.fisherydashboard.service;


import com.squareup.okhttp.*;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Objects;

public class RestApiCallServiceImpl implements RestApiCallService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    public JSONObject sendPostRequest(String url, JSONObject jsonObject) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toJSONString());

        Request postRequest = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        JSONObject returnJsonObject = new JSONObject();

        try {
            Response response = client.newCall(postRequest).execute();
            returnJsonObject.put("code", response.code());
            returnJsonObject.put("body", response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnJsonObject;
    }

    @Override
    public JSONObject sendGetRequest(String url, JSONObject jsonObject) {
        OkHttpClient client = new OkHttpClient();

        String param = Objects.nonNull(jsonObject) ? String.valueOf(jsonObject.get("value")) : "";

        Request getRequest = new Request.Builder()
                .url(url + param)
                .get()
                .build();
        JSONObject returnJsonObject = new JSONObject();

        try {
            Response response = client.newCall(getRequest).execute();
            returnJsonObject.put("code", response.code());
            returnJsonObject.put("body", response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnJsonObject;
    }
}
