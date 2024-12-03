/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.jwk.JWKSet
 *  com.nimbusds.jwt.JWTClaimsSet
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public final class JSONObjectUtils {
    public static boolean containsKey(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.containsKey((Object)key);
    }

    public static JSONObject parse(String s) throws ParseException {
        Object o = JSONUtils.parseJSON(s);
        if (o instanceof JSONObject) {
            return (JSONObject)o;
        }
        throw new ParseException("The JSON entity is not an object");
    }

    public static LinkedHashMap<String, Object> parseKeepingOrder(String s) throws ParseException {
        Object o = JSONUtils.parseJSONKeepingOrder(s);
        if (o instanceof LinkedHashMap) {
            return (LinkedHashMap)o;
        }
        throw new ParseException("The JSON entity is not an object");
    }

    @Deprecated
    public static JSONObject parseJSONObject(String s) throws ParseException {
        return JSONObjectUtils.parse(s);
    }

    public static <T> T getGeneric(JSONObject o, String key, Class<T> clazz) throws ParseException {
        if (!o.containsKey((Object)key)) {
            throw new ParseException("Missing JSON object member with key " + key + "");
        }
        Object value = o.get((Object)key);
        if (value == null) {
            throw new ParseException("JSON object member with key " + key + " has null value");
        }
        try {
            return JSONUtils.to(value, clazz);
        }
        catch (ParseException e) {
            throw new ParseException("Unexpected type of JSON object member with key " + key + "", e);
        }
    }

    public static boolean getBoolean(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, Boolean.class);
    }

    public static boolean getBoolean(JSONObject o, String key, boolean def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getBoolean(o, key);
        }
        return def;
    }

    public static int getInt(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, Number.class).intValue();
    }

    public static int getInt(JSONObject o, String key, int def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getInt(o, key);
        }
        return def;
    }

    public static long getLong(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, Number.class).longValue();
    }

    public static long getLong(JSONObject o, String key, long def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getLong(o, key);
        }
        return def;
    }

    public static float getFloat(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, Number.class).floatValue();
    }

    public static float getFloat(JSONObject o, String key, float def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getFloat(o, key);
        }
        return def;
    }

    public static double getDouble(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, Number.class).doubleValue();
    }

    public static double getDouble(JSONObject o, String key, double def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getDouble(o, key);
        }
        return def;
    }

    public static Number getNumber(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, Number.class);
    }

    public static Number getNumber(JSONObject o, String key, Number def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getNumber(o, key);
        }
        return def;
    }

    public static String getString(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, String.class);
    }

    public static String getString(JSONObject o, String key, String def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getString(o, key);
        }
        return def;
    }

    public static <T extends Enum<T>> T getEnum(JSONObject o, String key, Class<T> enumClass) throws ParseException {
        String value = JSONObjectUtils.getString(o, key);
        for (Enum en : (Enum[])enumClass.getEnumConstants()) {
            if (!en.toString().equalsIgnoreCase(value)) continue;
            return (T)en;
        }
        throw new ParseException("Unexpected value of JSON object member with key " + key + "");
    }

    public static <T extends Enum<T>> T getEnum(JSONObject o, String key, Class<T> enumClass, T def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getEnum(o, key, enumClass);
        }
        return def;
    }

    public static URI getURI(JSONObject o, String key) throws ParseException {
        try {
            return new URI(JSONObjectUtils.getGeneric(o, key, String.class));
        }
        catch (URISyntaxException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static URI getURI(JSONObject o, String key, URI def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getURI(o, key);
        }
        return def;
    }

    public static URL getURL(JSONObject o, String key) throws ParseException {
        try {
            return new URL(JSONObjectUtils.getGeneric(o, key, String.class));
        }
        catch (MalformedURLException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public static JSONArray getJSONArray(JSONObject o, String key) throws ParseException {
        List list = JSONObjectUtils.getGeneric(o, key, List.class);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll((Collection)list);
        return jsonArray;
    }

    public static JSONArray getJSONArray(JSONObject o, String key, JSONArray def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getJSONArray(o, key);
        }
        return def;
    }

    public static List<Object> getList(JSONObject o, String key) throws ParseException {
        return JSONObjectUtils.getGeneric(o, key, List.class);
    }

    public static List<Object> getList(JSONObject o, String key, List<Object> def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getList(o, key);
        }
        return def;
    }

    public static String[] getStringArray(JSONObject o, String key) throws ParseException {
        List<Object> list = JSONObjectUtils.getList(o, key);
        try {
            return list.toArray(new String[0]);
        }
        catch (ArrayStoreException e) {
            throw new ParseException("JSON object member with key " + key + " is not an array of strings");
        }
    }

    public static String[] getStringArray(JSONObject o, String key, String[] def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getStringArray(o, key);
        }
        return def;
    }

    public static List<String> getStringList(JSONObject o, String key) throws ParseException {
        return Arrays.asList(JSONObjectUtils.getStringArray(o, key));
    }

    public static List<String> getStringList(JSONObject o, String key, List<String> def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getStringList(o, key);
        }
        return def;
    }

    public static Set<String> getStringSet(JSONObject o, String key) throws ParseException {
        List<Object> list = JSONObjectUtils.getList(o, key);
        HashSet<String> set = new HashSet<String>();
        for (Object item : list) {
            try {
                set.add((String)item);
            }
            catch (Exception e) {
                throw new ParseException("JSON object member with key " + key + " is not an array of strings");
            }
        }
        return set;
    }

    public static Set<String> getStringSet(JSONObject o, String key, Set<String> def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getStringSet(o, key);
        }
        return def;
    }

    public static JSONObject getJSONObject(JSONObject o, String key) throws ParseException {
        Map mapObject = JSONObjectUtils.getGeneric(o, key, Map.class);
        return new JSONObject(mapObject);
    }

    public static JSONObject getJSONObject(JSONObject o, String key, JSONObject def) throws ParseException {
        if (o.get((Object)key) != null) {
            return JSONObjectUtils.getJSONObject(o, key);
        }
        return def;
    }

    public static JSONObject toJSONObject(JWTClaimsSet jwtClaimsSet) {
        if (jwtClaimsSet == null) {
            return null;
        }
        if (jwtClaimsSet.getClaims().isEmpty()) {
            return new JSONObject();
        }
        String json = jwtClaimsSet.toString();
        try {
            return JSONObjectUtils.parse(json);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static JSONObject toJSONObject(JWKSet jwkSet) {
        if (jwkSet == null) {
            return null;
        }
        String json = jwkSet.toString(false);
        try {
            return JSONObjectUtils.parse(json);
        }
        catch (ParseException e) {
            return null;
        }
    }

    private JSONObjectUtils() {
    }
}

