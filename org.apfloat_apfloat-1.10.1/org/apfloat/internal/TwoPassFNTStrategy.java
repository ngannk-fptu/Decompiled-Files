/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.AbstractStepFNTStrategy;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.Util;

public class TwoPassFNTStrategy
extends AbstractStepFNTStrategy {
    @Override
    protected void transform(DataStorage dataStorage, int n1, int n2, long length, int modulus) throws ApfloatRuntimeException {
        ArrayAccess arrayAccess;
        int i;
        assert (n2 >= n1);
        int maxBlockSize = this.getMaxMemoryBlockSize(length);
        if (n1 > maxBlockSize || n2 > maxBlockSize) {
            throw new ApfloatInternalException("Not enough memory available to fit one row or column of matrix to memory; n1=" + n1 + ", n2=" + n2 + ", available=" + maxBlockSize);
        }
        int b = maxBlockSize / n1;
        for (i = 0; i < n2; i += b) {
            arrayAccess = this.getColumns(dataStorage, i, b, n1);
            try {
                this.transformColumns(arrayAccess, n1, b, false, modulus);
                continue;
            }
            finally {
                if (arrayAccess != null) {
                    arrayAccess.close();
                }
            }
        }
        b = maxBlockSize / n2;
        for (i = 0; i < n1; i += b) {
            arrayAccess = this.getRows(dataStorage, i, b, n2);
            try {
                this.multiplyElements(arrayAccess, i, 0, b, n2, length, 1L, false, modulus);
                this.transformRows(arrayAccess, n2, b, false, modulus);
                continue;
            }
            finally {
                if (arrayAccess != null) {
                    arrayAccess.close();
                }
            }
        }
    }

    @Override
    protected void inverseTransform(DataStorage dataStorage, int n1, int n2, long length, long totalTransformLength, int modulus) throws ApfloatRuntimeException {
        ArrayAccess arrayAccess;
        int i;
        assert (n2 >= n1);
        int maxBlockSize = this.getMaxMemoryBlockSize(length);
        if (n1 > maxBlockSize || n2 > maxBlockSize) {
            throw new ApfloatInternalException("Not enough memory available to fit one row or column of matrix to memory; n1=" + n1 + ", n2=" + n2 + ", available=" + maxBlockSize);
        }
        int b = maxBlockSize / n2;
        for (i = 0; i < n1; i += b) {
            arrayAccess = this.getRows(dataStorage, i, b, n2);
            try {
                this.transformRows(arrayAccess, n2, b, true, modulus);
                this.multiplyElements(arrayAccess, i, 0, b, n2, length, totalTransformLength, true, modulus);
                continue;
            }
            finally {
                if (arrayAccess != null) {
                    arrayAccess.close();
                }
            }
        }
        b = maxBlockSize / n1;
        for (i = 0; i < n2; i += b) {
            arrayAccess = this.getColumns(dataStorage, i, b, n1);
            try {
                this.transformColumns(arrayAccess, n1, b, true, modulus);
                continue;
            }
            finally {
                if (arrayAccess != null) {
                    arrayAccess.close();
                }
            }
        }
    }

    protected ArrayAccess getColumns(DataStorage dataStorage, int startColumn, int columns, int rows) {
        return dataStorage.getTransposedArray(3, startColumn, columns, rows);
    }

    protected ArrayAccess getRows(DataStorage dataStorage, int startRow, int rows, int columns) {
        return dataStorage.getArray(3, startRow * columns, rows * columns);
    }

    protected void multiplyElements(ArrayAccess arrayAccess, int startRow, int startColumn, int rows, int columns, long length, long totalTransformLength, boolean isInverse, int modulus) {
        this.stepStrategy.multiplyElements(arrayAccess, startRow, startColumn, rows, columns, length, totalTransformLength, isInverse, modulus);
    }

    protected void transformColumns(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus) {
        this.stepStrategy.transformRows(arrayAccess, length, count, isInverse, true, modulus);
    }

    protected void transformRows(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus) {
        this.stepStrategy.transformRows(arrayAccess, length, count, isInverse, false, modulus);
    }

    private int getMaxMemoryBlockSize(long length) {
        ApfloatContext ctx = ApfloatContext.getContext();
        long maxMemoryBlockSize = Util.round2down(Math.min(ctx.getMaxMemoryBlockSize(), Integer.MAX_VALUE)) / (long)ctx.getBuilderFactory().getElementSize();
        int maxBlockSize = (int)Math.min(length, maxMemoryBlockSize);
        return maxBlockSize;
    }
}

