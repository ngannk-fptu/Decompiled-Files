/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.map.impl.query.Target;

public interface QueryEngine {
    public <T extends Result> T execute(Query var1, Target var2);
}

