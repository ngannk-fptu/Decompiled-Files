/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class JSONObjectUtils {
    public static JSONObject parse(String s) throws java.text.ParseException {
        Object o;
        try {
            o = new JSONParser(640).parse(s);
        }
        catch (ParseException e) {
            throw new java.text.ParseException("Invalid JSON: " + e.getMessage(), 0);
        }
        catch (Exception e) {
            throw new java.text.ParseException("Unexpected exception: " + e.getMessage(), 0);
        }
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new java.text.ParseException("JSON entity is not an object", 0);
    }

    @Deprecated
    public static JSONObject parseJSONObject(String s) throws java.text.ParseException {
        return JSONObjectUtils.parse(s);
    }

    private static <T> T getGeneric(JSONObject o, String key, Class<T> clazz) throws java.text.ParseException {
        if (o.get(key) == null) {
            return null;
        }
        Object value = o.get(key);
        if (!clazz.isAssignableFrom(value.getClass())) {
            throw new java.text.ParseException("Unexpected type of JSON object member with key \"" + key + "\"", 0);
        }
        return (T)value;
    }

    public static boolean getBoolean(JSONObject o, String key) throws java.text.ParseException {
        Boolean value = JSONObjectUtils.getGeneric(o, key, Boolean.class);
        if (value == null) {
            throw new java.text.ParseException("JSON object member with key \"" + key + "\" is missing or null", 0);
        }
        return value;
    }

    public static int getInt(JSONObject o, String key) throws java.text.ParseException {
        Number value = JSONObjectUtils.getGeneric(o, key, Number.class);
        if (value == null) {
            throw new java.text.ParseException("JSON object member with key \"" + key + "\" is missing or null", 0);
        }
        return value.intValue();
    }

    public static long getLong(JSONObject o, String key) throws java.text.ParseException {
        Number value = JSONObjectUtils.getGeneric(o, key, Number.class);
        if (value == null) {
            throw new java.text.ParseException("JSON object member with key \"" + key + "\" is missing or null", 0);
        }
        return value.longValue();
    }

    public static float getFloat(JSONObject o, String key) throws java.text.ParseException {
        Number value = JSONObjectUtils.getGeneric(o, key, Number.class);
        if (value == null) {
            throw new java.text.ParseException("JSON object member with key \"" + key + "\" is missing or null", 0);
        }
        return value.floatValue();
    }

    public static double getDouble(JSONObject o, String key) throws java.text.ParseException {
        Number value = JSONObjectUtils.getGeneric(o, key, Number.class);
        if (value == null) {
            throw new java.text.ParseException("JSON object member with key \"" + key + "\" is missing or null", 0);
        }
        return value.doubleValue();
    }

    public static String getString(JSONObject o, String key) throws java.text.ParseException {
        return JSONObjectUtils.getGeneric(o, key, String.class);
    }

    public static URI getURI(JSONObject o, String key) throws java.text.ParseException {
        String value = JSONObjectUtils.getString(o, key);
        if (value == null) {
            return null;
        }
        try {
            return new URI(value);
        }
        catch (URISyntaxException e) {
            throw new java.text.ParseException(e.getMessage(), 0);
        }
    }

    public static JSONArray getJSONArray(JSONObject o, String key) throws java.text.ParseException {
        return JSONObjectUtils.getGeneric(o, key, JSONArray.class);
    }

    public static String[] getStringArray(JSONObject o, String key) throws java.text.ParseException {
        JSONArray jsonArray = JSONObjectUtils.getJSONArray(o, key);
        if (jsonArray == null) {
            return null;
        }
        try {
            return jsonArray.toArray(new String[0]);
        }
        catch (ArrayStoreException e) {
            throw new java.text.ParseException("JSON object member with key \"" + key + "\" is not an array of strings", 0);
        }
    }

    public static List<String> getStringList(JSONObject o, String key) throws java.text.ParseException {
        String[] array = JSONObjectUtils.getStringArray(o, key);
        if (array == null) {
            return null;
        }
        return Arrays.asList(array);
    }

    public static JSONObject getJSONObject(JSONObject o, String key) throws java.text.ParseException {
        return JSONObjectUtils.getGeneric(o, key, JSONObject.class);
    }

    public static Base64URL getBase64URL(JSONObject o, String key) throws java.text.ParseException {
        String value = JSONObjectUtils.getString(o, key);
        if (value == null) {
            return null;
        }
        return new Base64URL(value);
    }

    private JSONObjectUtils() {
    }
}

