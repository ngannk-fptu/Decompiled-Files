/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 */
package com.atlassian.confluence.content.render.xhtml;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonUtils {
    private static final JsonParser parser = new JsonParser();

    public static boolean isJsonFormat(String value) {
        try {
            JsonElement element = parser.parse(value);
            return !element.isJsonPrimitive() && !element.isJsonNull();
        }
        catch (JsonSyntaxException e) {
            return false;
        }
    }
}

