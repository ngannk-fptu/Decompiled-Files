/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.json;

import com.atlassian.confluence.json.json.Json;

public class JsonBoolean
implements Json {
    private final Boolean bool;

    public JsonBoolean(Boolean bool) {
        this.bool = bool;
    }

    @Override
    public String serialize() {
        return String.valueOf(this.bool);
    }
}

