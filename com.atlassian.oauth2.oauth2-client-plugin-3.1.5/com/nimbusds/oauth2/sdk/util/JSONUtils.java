/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.util;

import com.nimbusds.oauth2.sdk.ParseException;
import java.util.LinkedList;
import java.util.List;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.writer.JsonReader;

public final class JSONUtils {
    public static Object parseJSON(String s) throws ParseException {
        try {
            return new JSONParser(640).parse(s);
        }
        catch (net.minidev.json.parser.ParseException e) {
            throw new ParseException("Invalid JSON: " + e.getMessage(), e);
        }
        catch (NullPointerException e) {
            throw new ParseException("The JSON string must not be null", e);
        }
        catch (Exception e) {
            throw new ParseException("Unexpected exception: " + e.getMessage(), e);
        }
    }

    public static Object parseJSONKeepingOrder(String s) throws ParseException {
        try {
            return new JSONParser(640).parse(s, new JsonReader().DEFAULT_ORDERED);
        }
        catch (net.minidev.json.parser.ParseException e) {
            throw new ParseException("Invalid JSON: " + e.getMessage(), e);
        }
    }

    public static <T> T to(Object o, Class<T> clazz) throws ParseException {
        if (!clazz.isAssignableFrom(o.getClass())) {
            throw new ParseException("Unexpected type: " + o.getClass());
        }
        return (T)o;
    }

    public static boolean toBoolean(Object o) throws ParseException {
        return JSONUtils.to(o, Boolean.class);
    }

    public static Number toNumber(Object o) throws ParseException {
        return JSONUtils.to(o, Number.class);
    }

    public static String toString(Object o) throws ParseException {
        return JSONUtils.to(o, String.class);
    }

    public static List<?> toList(Object o) throws ParseException {
        return JSONUtils.to(o, List.class);
    }

    public static List<String> toStringList(Object o) throws ParseException {
        LinkedList<String> stringList = new LinkedList<String>();
        try {
            for (Object item : JSONUtils.toList(o)) {
                stringList.add((String)item);
            }
        }
        catch (ClassCastException e) {
            throw new ParseException("Item not a string");
        }
        return stringList;
    }

    private JSONUtils() {
    }
}

