/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.base.internal;

import com.google.template.soy.base.internal.IdGenerator;

public final class FixedIdGenerator
implements IdGenerator {
    private static final int DEFAULT_FIXED_ID = 0;
    private final int fixedId;

    public FixedIdGenerator(int fixedId) {
        this.fixedId = fixedId;
    }

    public FixedIdGenerator() {
        this(0);
    }

    @Override
    public int genId() {
        return this.fixedId;
    }

    @Override
    public FixedIdGenerator clone() {
        return new FixedIdGenerator(this.fixedId);
    }
}

