/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.Predicate;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SkipIndexPredicate
implements Predicate {
    private Predicate target;

    public SkipIndexPredicate(Predicate target) {
        this.target = target;
    }

    public Predicate getTarget() {
        return this.target;
    }

    public boolean apply(Map.Entry mapEntry) {
        return this.target.apply(mapEntry);
    }

    public String toString() {
        return "SkipIndex(" + this.target + ')';
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        throw new UnsupportedOperationException("can't be serialized");
    }
}

