/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.JSONUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public final class JSONArrayUtils {
    public static JSONArray parse(String s) throws ParseException {
        Object o = JSONUtils.parseJSON(s);
        if (o instanceof JSONArray) {
            return (JSONArray)o;
        }
        throw new ParseException("The JSON entity is not an array");
    }

    public static List<String> toStringList(JSONArray jsonArray) {
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Collections.emptyList();
        }
        ArrayList<String> stringList = new ArrayList<String>(jsonArray.size());
        for (Object o : jsonArray) {
            if (o == null) continue;
            stringList.add(o.toString());
        }
        return stringList;
    }

    public static List<URI> toURIList(JSONArray jsonArray) throws ParseException {
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Collections.emptyList();
        }
        ArrayList<URI> uriList = new ArrayList<URI>(jsonArray.size());
        for (Object o : jsonArray) {
            if (o == null) continue;
            try {
                uriList.add(new URI(o.toString()));
            }
            catch (URISyntaxException e) {
                throw new ParseException("Illegal URI: " + e.getMessage(), e);
            }
        }
        return uriList;
    }

    public static List<JSONObject> toJSONObjectList(JSONArray jsonArray) throws ParseException {
        if (CollectionUtils.isEmpty(jsonArray)) {
            return Collections.emptyList();
        }
        ArrayList<JSONObject> objectList = new ArrayList<JSONObject>(jsonArray.size());
        for (Object o : jsonArray) {
            if (o == null) continue;
            try {
                objectList.add((JSONObject)o);
            }
            catch (Exception e) {
                throw new ParseException("Invalid JSON object: " + e.getMessage());
            }
        }
        return objectList;
    }

    private JSONArrayUtils() {
    }
}

