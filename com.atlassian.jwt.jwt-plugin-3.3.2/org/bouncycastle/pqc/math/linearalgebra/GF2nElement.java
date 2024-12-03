/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.linearalgebra;

import org.bouncycastle.pqc.math.linearalgebra.GF2nField;
import org.bouncycastle.pqc.math.linearalgebra.GFElement;

public abstract class GF2nElement
implements GFElement {
    protected GF2nField mField;
    protected int mDegree;

    public abstract Object clone();

    abstract void assignZero();

    abstract void assignOne();

    public abstract boolean testRightmostBit();

    abstract boolean testBit(int var1);

    public final GF2nField getField() {
        return this.mField;
    }

    public abstract GF2nElement increase();

    public abstract void increaseThis();

    public final GFElement subtract(GFElement gFElement) {
        return this.add(gFElement);
    }

    public final void subtractFromThis(GFElement gFElement) {
        this.addToThis(gFElement);
    }

    public abstract GF2nElement square();

    public abstract void squareThis();

    public abstract GF2nElement squareRoot();

    public abstract void squareRootThis();

    public final GF2nElement convert(GF2nField gF2nField) {
        return this.mField.convert(this, gF2nField);
    }

    public abstract int trace();

    public abstract GF2nElement solveQuadraticEquation() throws RuntimeException;
}

