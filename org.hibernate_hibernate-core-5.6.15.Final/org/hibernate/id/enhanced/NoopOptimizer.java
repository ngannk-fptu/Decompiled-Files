/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id.enhanced;

import java.io.Serializable;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.id.enhanced.AbstractOptimizer;
import org.hibernate.id.enhanced.AccessCallback;

public final class NoopOptimizer
extends AbstractOptimizer {
    private IntegralDataTypeHolder lastSourceValue;

    public NoopOptimizer(Class returnClass, int incrementSize) {
        super(returnClass, incrementSize);
    }

    @Override
    public Serializable generate(AccessCallback callback) {
        IntegralDataTypeHolder value;
        this.lastSourceValue = value = callback.getNextValue();
        return value.makeValue();
    }

    @Override
    public IntegralDataTypeHolder getLastSourceValue() {
        return this.lastSourceValue;
    }

    @Override
    public boolean applyIncrementSizeToSourceValues() {
        return this.getIncrementSize() != 0 && this.getIncrementSize() != 1;
    }
}

