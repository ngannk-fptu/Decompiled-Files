/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.math;

import com.jhlabs.math.Function1D;

public class CompositeFunction1D
implements Function1D {
    private Function1D f1;
    private Function1D f2;

    public CompositeFunction1D(Function1D f1, Function1D f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    public float evaluate(float v) {
        return this.f1.evaluate(this.f2.evaluate(v));
    }
}

