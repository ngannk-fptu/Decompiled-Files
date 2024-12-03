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
import org.apfloat.spi.MatrixStrategy;

public class SixStepFNTStrategy
extends AbstractStepFNTStrategy {
    protected MatrixStrategy matrixStrategy;

    public SixStepFNTStrategy() {
        ApfloatContext ctx = ApfloatContext.getContext();
        this.matrixStrategy = ctx.getBuilderFactory().getMatrixBuilder().createMatrix();
    }

    @Override
    protected void transform(DataStorage dataStorage, int n1, int n2, long length, int modulus) throws ApfloatRuntimeException {
        if (length > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }
        assert (n2 >= n1);
        try (ArrayAccess arrayAccess = dataStorage.getArray(3, 0L, (int)length);){
            this.preTransform(arrayAccess);
            this.transposeInitial(arrayAccess, n1, n2, false);
            this.transformFirst(arrayAccess, n1, n2, false, modulus);
            this.transposeMiddle(arrayAccess, n2, n1, false);
            this.multiplyElements(arrayAccess, n1, n2, length, 1L, false, modulus);
            this.transformSecond(arrayAccess, n2, n1, false, modulus);
            this.transposeFinal(arrayAccess, n1, n2, false);
            this.postTransform(arrayAccess);
        }
    }

    @Override
    protected void inverseTransform(DataStorage dataStorage, int n1, int n2, long length, long totalTransformLength, int modulus) throws ApfloatRuntimeException {
        if (length > Integer.MAX_VALUE) {
            throw new ApfloatInternalException("Maximum array length exceeded: " + length);
        }
        assert (n2 >= n1);
        try (ArrayAccess arrayAccess = dataStorage.getArray(3, 0L, (int)length);){
            this.preTransform(arrayAccess);
            this.transposeFinal(arrayAccess, n2, n1, true);
            this.transformSecond(arrayAccess, n2, n1, true, modulus);
            this.multiplyElements(arrayAccess, n1, n2, length, totalTransformLength, true, modulus);
            this.transposeMiddle(arrayAccess, n1, n2, true);
            this.transformFirst(arrayAccess, n1, n2, true, modulus);
            this.transposeInitial(arrayAccess, n2, n1, true);
            this.postTransform(arrayAccess);
        }
    }

    protected void preTransform(ArrayAccess arrayAccess) {
    }

    protected void transposeInitial(ArrayAccess arrayAccess, int n1, int n2, boolean isInverse) {
        this.matrixStrategy.transpose(arrayAccess, n1, n2);
    }

    protected void transposeMiddle(ArrayAccess arrayAccess, int n1, int n2, boolean isInverse) {
        this.matrixStrategy.transpose(arrayAccess, n1, n2);
    }

    protected void transposeFinal(ArrayAccess arrayAccess, int n1, int n2, boolean isInverse) {
    }

    protected void transformFirst(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus) {
        this.stepStrategy.transformRows(arrayAccess, length, count, isInverse, true, modulus);
    }

    protected void transformSecond(ArrayAccess arrayAccess, int length, int count, boolean isInverse, int modulus) {
        this.stepStrategy.transformRows(arrayAccess, length, count, isInverse, false, modulus);
    }

    protected void multiplyElements(ArrayAccess arrayAccess, int rows, int columns, long length, long totalTransformLength, boolean isInverse, int modulus) {
        this.stepStrategy.multiplyElements(arrayAccess, 0, 0, rows, columns, length, totalTransformLength, isInverse, modulus);
    }

    protected void postTransform(ArrayAccess arrayAccess) {
    }
}

