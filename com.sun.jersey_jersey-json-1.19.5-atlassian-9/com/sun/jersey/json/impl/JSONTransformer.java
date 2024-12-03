/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jettison.json.JSONArray
 *  org.codehaus.jettison.json.JSONException
 *  org.codehaus.jettison.json.JSONObject
 */
package com.sun.jersey.json.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

final class JSONTransformer {
    JSONTransformer() {
    }

    static <T> Map<String, T> asMap(String jsonObjectVal) throws JSONException {
        if (null == jsonObjectVal) {
            return null;
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        JSONObject sourceMap = new JSONObject(jsonObjectVal);
        Iterator keyIterator = sourceMap.keys();
        while (keyIterator.hasNext()) {
            String key = (String)keyIterator.next();
            result.put(key, sourceMap.get(key));
        }
        return result;
    }

    static <T> Collection<T> asCollection(String jsonArrayVal) throws JSONException {
        if (null == jsonArrayVal) {
            return null;
        }
        LinkedList<Object> result = new LinkedList<Object>();
        JSONArray arrayVal = new JSONArray(jsonArrayVal);
        for (int i = 0; i < arrayVal.length(); ++i) {
            result.add(arrayVal.get(i));
        }
        return result;
    }

    static String asJsonArray(Collection<? extends Object> collection) throws JSONException {
        return null == collection ? "[]" : new JSONArray(collection).toString();
    }

    static String asJsonObject(Map map) throws JSONException {
        return null == map ? "{}" : new JSONObject(map).toString();
    }
}

