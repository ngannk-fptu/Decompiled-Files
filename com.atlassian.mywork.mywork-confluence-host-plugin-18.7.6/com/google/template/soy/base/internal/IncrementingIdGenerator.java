/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.base.internal;

import com.google.template.soy.base.internal.IdGenerator;

public final class IncrementingIdGenerator
implements IdGenerator {
    private int currId;

    public IncrementingIdGenerator() {
        this.currId = 0;
    }

    protected IncrementingIdGenerator(IncrementingIdGenerator orig) {
        this.currId = orig.currId;
    }

    @Override
    public int genId() {
        return this.currId++;
    }

    @Override
    public IncrementingIdGenerator clone() {
        IncrementingIdGenerator clone = new IncrementingIdGenerator();
        clone.currId = this.currId;
        return clone;
    }
}

