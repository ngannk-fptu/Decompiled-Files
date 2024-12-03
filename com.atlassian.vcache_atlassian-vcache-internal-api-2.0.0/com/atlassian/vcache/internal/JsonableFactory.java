/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 */
package com.atlassian.vcache.internal;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.vcache.internal.RequestMetrics;
import java.util.function.Function;

public interface JsonableFactory
extends Function<RequestMetrics, Jsonable> {
    @Override
    public Jsonable apply(RequestMetrics var1);
}

