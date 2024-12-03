/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.json.json;

import com.atlassian.confluence.json.json.Json;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonArray
implements Json {
    private final List<Json> jsonList = new ArrayList<Json>();

    public JsonArray() {
    }

    public JsonArray(List<Json> jsonList) {
        this.jsonList.addAll(jsonList);
    }

    @Override
    public String serialize() {
        ArrayList<String> jsonCollectionStrings = new ArrayList<String>(this.jsonList.size());
        for (Json json : this.jsonList) {
            jsonCollectionStrings.add(json.serialize());
        }
        return "[" + StringUtils.join(jsonCollectionStrings, (char)',') + "]";
    }

    public void add(Json json) {
        this.jsonList.add(json);
    }
}

