/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.DoubleMatrixStrategy;
import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.MatrixStrategy;

public class DoubleMatrixBuilder
implements MatrixBuilder {
    private static MatrixStrategy matrixStrategy = new DoubleMatrixStrategy();

    @Override
    public MatrixStrategy createMatrix() {
        return matrixStrategy;
    }
}

