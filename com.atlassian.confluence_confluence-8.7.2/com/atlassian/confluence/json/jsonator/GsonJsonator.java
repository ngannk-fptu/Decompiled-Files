/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.jsonator.Gsonable;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.google.gson.Gson;

public class GsonJsonator
implements Jsonator<Gsonable> {
    private final Gson gson = new Gson();

    @Override
    public Json convert(Gsonable object) {
        return () -> this.gson.toJson((Object)object);
    }
}

