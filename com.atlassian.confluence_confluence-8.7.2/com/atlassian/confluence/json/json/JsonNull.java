/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.json;

import com.atlassian.confluence.json.json.Json;

public class JsonNull
implements Json {
    @Override
    public String serialize() {
        return "null";
    }
}

