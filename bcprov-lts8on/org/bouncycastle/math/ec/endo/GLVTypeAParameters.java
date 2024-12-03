/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.endo.ScalarSplitParameters;

public class GLVTypeAParameters {
    protected final BigInteger i;
    protected final BigInteger lambda;
    protected final ScalarSplitParameters splitParams;

    public GLVTypeAParameters(BigInteger i, BigInteger lambda, ScalarSplitParameters splitParams) {
        this.i = i;
        this.lambda = lambda;
        this.splitParams = splitParams;
    }

    public BigInteger getI() {
        return this.i;
    }

    public BigInteger getLambda() {
        return this.lambda;
    }

    public ScalarSplitParameters getSplitParams() {
        return this.splitParams;
    }
}

