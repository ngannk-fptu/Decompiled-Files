/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import org.json.JSONArray;

public class JSONUtil {
    public static JSONArray toJsonArray(String ... errorMessages) {
        JSONArray array = new JSONArray();
        if (null != errorMessages) {
            for (String errorMessage : errorMessages) {
                if (null == errorMessage) continue;
                array.put((Object)errorMessage);
            }
        }
        return array;
    }

    public static JSONArray toJsonArray(JsonSerializable ... jsonSerializables) {
        JSONArray array = new JSONArray();
        if (null != jsonSerializables && jsonSerializables.length > 0) {
            for (JsonSerializable jsonSerializable : jsonSerializables) {
                if (null == jsonSerializable) continue;
                array.put((Object)jsonSerializable.toJson());
            }
        }
        return array;
    }
}

