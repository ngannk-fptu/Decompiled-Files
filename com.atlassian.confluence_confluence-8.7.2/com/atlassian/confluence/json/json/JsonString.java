/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.json;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonEscapeUtils;

public class JsonString
implements Json {
    private final String str;

    public JsonString(String str) {
        this.str = str;
    }

    @Override
    public String serialize() {
        if (this.str == null) {
            return null;
        }
        return JsonEscapeUtils.quote(this.str);
    }
}

