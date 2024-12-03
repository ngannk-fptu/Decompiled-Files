/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.FloatMatrixStrategy;
import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.MatrixStrategy;

public class FloatMatrixBuilder
implements MatrixBuilder {
    private static MatrixStrategy matrixStrategy = new FloatMatrixStrategy();

    @Override
    public MatrixStrategy createMatrix() {
        return matrixStrategy;
    }
}

