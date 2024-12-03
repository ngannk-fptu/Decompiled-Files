/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongModConstants;
import org.apfloat.internal.LongModMath;
import org.apfloat.internal.ParallelRunnable;
import org.apfloat.internal.ParallelRunner;
import org.apfloat.internal.Parallelizable;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.Factor3NTTStepStrategy;

public class LongFactor3NTTStepStrategy
extends LongModMath
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
        return 0x1800000000000L;
    }

    protected ParallelRunnable createColumnTransformParallelRunnable(final DataStorage dataStorage0, final DataStorage dataStorage1, final DataStorage dataStorage2, final long startColumn, long columns, long power2length, long length, final boolean isInverse, int modulus) {
        this.setModulus(LongModConstants.MODULUS[modulus]);
        final long w = isInverse ? this.getInverseNthRoot(LongModConstants.PRIMITIVE_ROOT[modulus], length) : this.getForwardNthRoot(LongModConstants.PRIMITIVE_ROOT[modulus], length);
        long w3 = this.modPow(w, power2length);
        final long ww = this.modMultiply(w, w);
        final long w1 = this.negate(this.modDivide(3L, 2L));
        final long w2 = this.modAdd(w3, this.modDivide(1L, 2L));
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
        private long w;
        private long ww;
        private long w1;
        private long w2;
        private boolean isInverse;

        public ColumnTransformRunnable(DataStorage dataStorage0, DataStorage dataStorage1, DataStorage dataStorage2, long startColumn, long columns, long w, long ww, long w1, long w2, boolean isInverse) {
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
            long tmp1 = LongFactor3NTTStepStrategy.this.modPow(this.w, this.startColumn);
            long tmp2 = LongFactor3NTTStepStrategy.this.modPow(this.ww, this.startColumn);
            DataStorage.Iterator iterator0 = this.dataStorage0.iterator(3, this.startColumn, this.startColumn + this.columns);
            DataStorage.Iterator iterator1 = this.dataStorage1.iterator(3, this.startColumn, this.startColumn + this.columns);
            DataStorage.Iterator iterator2 = this.dataStorage2.iterator(3, this.startColumn, this.startColumn + this.columns);
            for (long i = 0L; i < this.columns; ++i) {
                long x0 = iterator0.getLong();
                long x1 = iterator1.getLong();
                long x2 = iterator2.getLong();
                if (this.isInverse) {
                    x1 = LongFactor3NTTStepStrategy.this.modMultiply(x1, tmp1);
                    x2 = LongFactor3NTTStepStrategy.this.modMultiply(x2, tmp2);
                }
                long t = LongFactor3NTTStepStrategy.this.modAdd(x1, x2);
                x2 = LongFactor3NTTStepStrategy.this.modSubtract(x1, x2);
                x0 = LongFactor3NTTStepStrategy.this.modAdd(x0, t);
                t = LongFactor3NTTStepStrategy.this.modMultiply(t, this.w1);
                x2 = LongFactor3NTTStepStrategy.this.modMultiply(x2, this.w2);
                t = LongFactor3NTTStepStrategy.this.modAdd(t, x0);
                x1 = LongFactor3NTTStepStrategy.this.modAdd(t, x2);
                x2 = LongFactor3NTTStepStrategy.this.modSubtract(t, x2);
                if (!this.isInverse) {
                    x1 = LongFactor3NTTStepStrategy.this.modMultiply(x1, tmp1);
                    x2 = LongFactor3NTTStepStrategy.this.modMultiply(x2, tmp2);
                }
                iterator0.setLong(x0);
                iterator1.setLong(x1);
                iterator2.setLong(x2);
                iterator0.next();
                iterator1.next();
                iterator2.next();
                tmp1 = LongFactor3NTTStepStrategy.this.modMultiply(tmp1, this.w);
                tmp2 = LongFactor3NTTStepStrategy.this.modMultiply(tmp2, this.ww);
            }
        }
    }
}

