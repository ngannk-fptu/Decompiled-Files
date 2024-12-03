/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.vcache.internal.JsonableFactory
 *  com.atlassian.vcache.internal.RequestMetrics
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 */
package com.atlassian.vcache.internal.core.metrics;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.vcache.internal.JsonableFactory;
import com.atlassian.vcache.internal.RequestMetrics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonJsonableFactory
implements JsonableFactory {
    private static final Gson GSON = new GsonBuilder().create();

    public Jsonable apply(RequestMetrics requestMetrics) {
        return writer -> writer.write(GSON.toJson((Object)requestMetrics));
    }
}

