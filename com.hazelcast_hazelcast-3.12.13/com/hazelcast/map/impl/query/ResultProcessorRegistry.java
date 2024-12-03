/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.Result;
import com.hazelcast.map.impl.query.ResultProcessor;
import java.util.HashMap;
import java.util.Map;

public class ResultProcessorRegistry {
    private final Map<Class<? extends Result>, ResultProcessor> processors = new HashMap<Class<? extends Result>, ResultProcessor>();

    public void registerProcessor(Class<? extends Result> clazz, ResultProcessor processor) {
        this.processors.put(clazz, processor);
    }

    public ResultProcessor get(Class<? extends Result> clazz) {
        return this.processors.get(clazz);
    }
}

