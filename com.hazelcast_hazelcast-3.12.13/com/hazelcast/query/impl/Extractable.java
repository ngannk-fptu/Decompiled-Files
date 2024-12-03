/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.QueryException;

public interface Extractable {
    public Object getAttributeValue(String var1) throws QueryException;
}

