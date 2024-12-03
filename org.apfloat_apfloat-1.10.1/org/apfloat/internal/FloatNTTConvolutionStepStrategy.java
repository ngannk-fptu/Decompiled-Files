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
import org.apfloat.spi.NTTConvolutionStepStrategy;

public class FloatNTTConvolutionStepStrategy
extends FloatModMath
implements NTTConvolutionStepStrategy,
Parallelizable {
    @Override
    public void multiplyInPlace(DataStorage sourceAndDestination, DataStorage source, int modulus) throws ApfloatRuntimeException {
        assert (sourceAndDestination != source);
        long size = sourceAndDestination.getSize();
        ParallelRunnable parallelRunnable = this.createMultiplyInPlaceParallelRunnable(sourceAndDestination, source, modulus);
        if (size <= Integer.MAX_VALUE && sourceAndDestination.isCached() && source.isCached()) {
            ParallelRunner.runParallel(parallelRunnable);
        } else {
            parallelRunnable.run();
        }
    }

    @Override
    public void squareInPlace(DataStorage sourceAndDestination, int modulus) throws ApfloatRuntimeException {
        long size = sourceAndDestination.getSize();
        ParallelRunnable parallelRunnable = this.createSquareInPlaceParallelRunnable(sourceAndDestination, modulus);
        if (size <= Integer.MAX_VALUE && sourceAndDestination.isCached()) {
            ParallelRunner.runParallel(parallelRunnable);
        } else {
            parallelRunnable.run();
        }
    }

    protected ParallelRunnable createMultiplyInPlaceParallelRunnable(final DataStorage sourceAndDestination, final DataStorage source, int modulus) {
        long size = sourceAndDestination.getSize();
        this.setModulus(FloatModConstants.MODULUS[modulus]);
        ParallelRunnable parallelRunnable = new ParallelRunnable(size){

            @Override
            public Runnable getRunnable(long offset, long length) {
                return new MultiplyInPlaceRunnable(sourceAndDestination, source, offset, length);
            }
        };
        return parallelRunnable;
    }

    protected ParallelRunnable createSquareInPlaceParallelRunnable(final DataStorage sourceAndDestination, int modulus) {
        long size = sourceAndDestination.getSize();
        this.setModulus(FloatModConstants.MODULUS[modulus]);
        ParallelRunnable parallelRunnable = new ParallelRunnable(size){

            @Override
            public Runnable getRunnable(long offset, long length) {
                return new SquareInPlaceRunnable(sourceAndDestination, offset, length);
            }
        };
        return parallelRunnable;
    }

    private class SquareInPlaceRunnable
    implements Runnable {
        private DataStorage sourceAndDestination;
        private long offset;
        private long length;

        public SquareInPlaceRunnable(DataStorage sourceAndDestination, long offset, long length) {
            this.sourceAndDestination = sourceAndDestination;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public void run() {
            DataStorage.Iterator iterator = this.sourceAndDestination.iterator(3, this.offset, this.offset + this.length);
            while (this.length > 0L) {
                float value = iterator.getFloat();
                iterator.setFloat(FloatNTTConvolutionStepStrategy.this.modMultiply(value, value));
                iterator.next();
                --this.length;
            }
        }
    }

    private class MultiplyInPlaceRunnable
    implements Runnable {
        private DataStorage sourceAndDestination;
        private DataStorage source;
        private long offset;
        private long length;

        public MultiplyInPlaceRunnable(DataStorage sourceAndDestination, DataStorage source, long offset, long length) {
            this.sourceAndDestination = sourceAndDestination;
            this.source = source;
            this.offset = offset;
            this.length = length;
        }

        @Override
        public void run() {
            DataStorage.Iterator dest = this.sourceAndDestination.iterator(3, this.offset, this.offset + this.length);
            DataStorage.Iterator src = this.source.iterator(1, this.offset, this.offset + this.length);
            while (this.length > 0L) {
                dest.setFloat(FloatNTTConvolutionStepStrategy.this.modMultiply(dest.getFloat(), src.getFloat()));
                dest.next();
                src.next();
                --this.length;
            }
        }
    }
}

