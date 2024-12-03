/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.impl;

import com.hazelcast.internal.metrics.ProbeFunction;

class ProbeInstance<S> {
    final String name;
    volatile ProbeFunction function;
    volatile S source;

    ProbeInstance(String name, S source, ProbeFunction function) {
        this.name = name;
        this.function = function;
        this.source = source;
    }
}

