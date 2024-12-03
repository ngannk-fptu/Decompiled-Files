/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.FloatModConstants;
import org.apfloat.internal.FloatModMath;
import org.apfloat.internal.ParallelRunnable;
import org.apfloat.internal.ParallelRunner;
import org.apfloat.internal.Parallelizable;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.Factor3NTTStepStrategy;

public class FloatFactor3NTTStepStrategy
extends FloatModMath
implements Factor3NTTStepStrategy,
Parallelizable {
    @Override
    public void transformColumns(DataStorage dataStorage0, DataStorage dataStorage1, DataStorage dataStorage2, long startColumn, long columns, long power2length, long length, boolean isInverse, int modulus) throws ApfloatRuntimeException {
        assert (length == 3L * power2length);
        ParallelRunnable parallelRunnable = this.createColumnTransformParallelRunnable(dataStorage0, dataStorage1, dataStorage2, startColumn, columns, power2length, length, isInverse, modulus);
        if (columns <= Integer.MAX_VALUE && dataStorage0.isCached() && dataStorage1.isCached() && dataStorage2.isCached()) {
            ParallelRunner.runParallel(parallelRunnable);
        } else {
            parallelRunnable.run();
        }
    }

    @Override
    public long getMaxTransformLength() {
        return 393216L;
    }

    protected ParallelRunnable createColumnTransformParallelRunnable(final DataStorage dataStorage0, final DataStorage dataStorage1, final DataStorage dataStorage2, final long startColumn, long columns, long power2length, long length, final boolean isInverse, int modulus) {
        this.setModulus(FloatModConstants.MODULUS[modulus]);
        final float w = isInverse ? this.getInverseNthRoot(FloatModConstants.PRIMITIVE_ROOT[modulus], length) : this.getForwardNthRoot(FloatModConstants.PRIMITIVE_ROOT[modulus], length);
        float w3 = this.modPow(w, power2length);
        final float ww = this.modMultiply(w, w);
        final float w1 = this.negate(this.modDivide(3.0f, 2.0f));
        final float w2 = this.modAdd(w3, this.modDivide(1.0f, 2.0f));
        ParallelRunnable parallelRunnable = new ParallelRunnable(columns){

            @Override
            public Runnable getRunnable(long strideStartColumn, long strideColumns) {
                return new ColumnTransformRunnable(dataStorage0, dataStorage1, dataStorage2, startColumn + strideStartColumn, strideColumns, w, ww, w1, w2, isInverse);
            }
        };
        return parallelRunnable;
    }

    private class ColumnTransformRunnable
    implements Runnable {
        private DataStorage dataStorage0;
        private DataStorage dataStorage1;
        private DataStorage dataStorage2;
        private long startColumn;
        private long columns;
        private float w;
        private float ww;
        private float w1;
        private float w2;
        private boolean isInverse;

        public ColumnTransformRunnable(DataStorage dataStorage0, DataStorage dataStorage1, DataStorage dataStorage2, long startColumn, long columns, float w, float ww, float w1, float w2, boolean isInverse) {
            this.dataStorage0 = dataStorage0;
            this.dataStorage1 = dataStorage1;
            this.dataStorage2 = dataStorage2;
            this.startColumn = startColumn;
            this.columns = columns;
            this.w = w;
            this.ww = ww;
            this.w1 = w1;
            this.w2 = w2;
            this.isInverse = isInverse;
        }

        @Override
        public void run() {
            float tmp1 = FloatFactor3NTTStepStrategy.this.modPow(this.w, this.startColumn);
            float tmp2 = FloatFactor3NTTStepStrategy.this.modPow(this.ww, this.startColumn);
            DataStorage.Iterator iterator0 = this.dataStorage0.iterator(3, this.startColumn, this.startColumn + this.columns);
            DataStorage.Iterator iterator1 = this.dataStorage1.iterator(3, this.startColumn, this.startColumn + this.columns);
            DataStorage.Iterator iterator2 = this.dataStorage2.iterator(3, this.startColumn, this.startColumn + this.columns);
            for (long i = 0L; i < this.columns; ++i) {
                float x0 = iterator0.getFloat();
                float x1 = iterator1.getFloat();
                float x2 = iterator2.getFloat();
                if (this.isInverse) {
                    x1 = FloatFactor3NTTStepStrategy.this.modMultiply(x1, tmp1);
                    x2 = FloatFactor3NTTStepStrategy.this.modMultiply(x2, tmp2);
                }
                float t = FloatFactor3NTTStepStrategy.this.modAdd(x1, x2);
                x2 = FloatFactor3NTTStepStrategy.this.modSubtract(x1, x2);
                x0 = FloatFactor3NTTStepStrategy.this.modAdd(x0, t);
                t = FloatFactor3NTTStepStrategy.this.modMultiply(t, this.w1);
                x2 = FloatFactor3NTTStepStrategy.this.modMultiply(x2, this.w2);
                t = FloatFactor3NTTStepStrategy.this.modAdd(t, x0);
                x1 = FloatFactor3NTTStepStrategy.this.modAdd(t, x2);
                x2 = FloatFactor3NTTStepStrategy.this.modSubtract(t, x2);
                if (!this.isInverse) {
                    x1 = FloatFactor3NTTStepStrategy.this.modMultiply(x1, tmp1);
                    x2 = FloatFactor3NTTStepStrategy.this.modMultiply(x2, tmp2);
                }
                iterator0.setFloat(x0);
                iterator1.setFloat(x1);
                iterator2.setFloat(x2);
                iterator0.next();
                iterator1.next();
                iterator2.next();
                tmp1 = FloatFactor3NTTStepStrategy.this.modMultiply(tmp1, this.w);
                tmp2 = FloatFactor3NTTStepStrategy.this.modMultiply(tmp2, this.ww);
            }
        }
    }
}

