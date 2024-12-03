/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.internal.LongMatrixStrategy;
import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.MatrixStrategy;

public class LongMatrixBuilder
implements MatrixBuilder {
    private static MatrixStrategy matrixStrategy = new LongMatrixStrategy();

    @Override
    public MatrixStrategy createMatrix() {
        return matrixStrategy;
    }
}

