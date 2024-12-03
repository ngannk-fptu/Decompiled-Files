/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.query.impl.getters.MultiResult;
import java.util.List;

public final class ImmutableMultiResult<T>
extends MultiResult<T> {
    private final MultiResult<T> multiResult;

    public ImmutableMultiResult(MultiResult<T> multiResult) {
        this.multiResult = multiResult;
    }

    @Override
    public void add(T result) {
        throw new UnsupportedOperationException("Can't modify an immutable MultiResult");
    }

    @Override
    public void addNullOrEmptyTarget() {
        throw new UnsupportedOperationException("Can't modify an immutable MultiResult");
    }

    @Override
    public List<T> getResults() {
        return this.multiResult.getResults();
    }

    @Override
    public boolean isEmpty() {
        return this.multiResult.isEmpty();
    }

    @Override
    public boolean isNullEmptyTarget() {
        return this.multiResult.isNullEmptyTarget();
    }

    @Override
    public void setNullOrEmptyTarget(boolean nullOrEmptyTarget) {
        throw new UnsupportedOperationException("Can't modify an immutable MultiResult");
    }
}

