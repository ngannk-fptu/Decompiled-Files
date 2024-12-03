/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.PredicateConfig;
import com.hazelcast.query.Predicate;

class PredicateConfigReadOnly
extends PredicateConfig {
    public PredicateConfigReadOnly(PredicateConfig config) {
        super(config);
    }

    @Override
    public PredicateConfig setClassName(String className) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public PredicateConfig setImplementation(Predicate implementation) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public void setSql(String sql) {
        throw new UnsupportedOperationException("This config is read-only");
    }

    @Override
    public String toString() {
        return "PredicateConfigReadOnly{} " + super.toString();
    }
}

