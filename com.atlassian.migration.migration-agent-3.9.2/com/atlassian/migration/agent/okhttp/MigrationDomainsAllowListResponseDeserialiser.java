/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package com.atlassian.migration.agent.okhttp;

import com.atlassian.migration.agent.service.catalogue.model.MigrationDomainsAllowlistResponse;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class MigrationDomainsAllowListResponseDeserialiser
implements JsonDeserializer<MigrationDomainsAllowlistResponse.Entry> {
    public static final String TYPE = "type";
    private final Gson gson = new Gson();

    public MigrationDomainsAllowlistResponse.Entry deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (!jsonObject.has(TYPE)) {
            throw new IllegalArgumentException("Json does not have 'type' attribute");
        }
        String typeAsString = jsonObject.get(TYPE).getAsString();
        if (typeAsString.equals("default")) {
            return (MigrationDomainsAllowlistResponse.Entry)this.gson.fromJson(json, MigrationDomainsAllowlistResponse.UrlEntry.class);
        }
        return null;
    }
}

