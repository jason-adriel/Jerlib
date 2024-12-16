package com.example.jerlib.utils;

import com.example.jerlib.models.Bibjson;
import com.example.jerlib.models.Entry;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class CustomAPIDeserializer implements JsonDeserializer<Entry> {
    @Override
    public Entry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();

            // Check if required properties exist
            if (jsonObject.has("bibjson") && jsonObject.has("created_date") && jsonObject.has("id")) {

                JsonObject bibjsonObject = jsonObject.getAsJsonObject("bibjson");
                if (!bibjsonObject.has("identifier")) {
                    return null;
                }

                if (!bibjsonObject.has("journal")) {
                    return null;
                }

                if (!bibjsonObject.has("keywords") || bibjsonObject.getAsJsonArray("keywords").isEmpty()) {
                    return null;
                }

                if (!bibjsonObject.has("subject")) {
                    return null;
                }

                if (!bibjsonObject.has("link") || bibjsonObject.getAsJsonArray("link").isEmpty()) {
                    return null;
                }

                if (!bibjsonObject.has("abstract")) {
                    return null;
                }

                if (!bibjsonObject.has("title")) {
                    return null;
                }

                Bibjson bibjson = context.deserialize(bibjsonObject, Bibjson.class);
                Entry entry = new Entry();
                entry.setId(jsonObject.get("id").getAsString());
                entry.setCreatedDate(jsonObject.get("created_date").getAsString());
                entry.setBibjson(bibjson);
                return entry;
            }

            return null;
        }
        // If properties are missing or invalid, return null or a default instance
        return null;
    }
}