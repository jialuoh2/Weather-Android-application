package edu.uiuc.cs427app;

import static org.junit.Assert.assertEquals;

import static edu.uiuc.cs427app.BuildConfig.WEATHER_API_KEY;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import edu.uiuc.cs427app.api.weather;

/**
 * Test Class for testing api - not used currently will be useful in future milestone requirements
 */
public class ApiTest {

    /**
     * test method for testing a weather api
     * @throws Exception
     */
    @Test
    public void weatherTest() throws Exception {
        weather w = new weather("35.7796", "-78.6382");
        String request = String.format("https://api.weatherbit.io/v2.0/current?lat=%s&lon=%s&key=%s&include=minutely", w.getLat(), w.getLon(), WEATHER_API_KEY);
        HttpResponse<JsonNode> response = Unirest.get(request).asJson();
        JSONArray ja = (JSONArray) response.getBody().getObject().get("data");
        JSONObject jo = (JSONObject) ja.get(0);
        System.out.println(jo.get("sunrise"));
    }
}
