/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.json;

import com.atlassian.confluence.json.json.Json;

public class JsonNumber
implements Json {
    private final Number num;

    public JsonNumber(Number num) {
        this.num = num;
    }

    @Override
    public String serialize() {
        return String.valueOf(this.num);
    }
}

