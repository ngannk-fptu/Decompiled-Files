/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.lang.reflect.Array;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.MessagePasser;
import org.apfloat.internal.ParallelRunnable;
import org.apfloat.internal.ParallelRunner;
import org.apfloat.internal.Parallelizable;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.CarryCRTStepStrategy;
import org.apfloat.spi.CarryCRTStrategy;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;

public class StepCarryCRTStrategy
implements CarryCRTStrategy,
Parallelizable {
    private int radix;

    public StepCarryCRTStrategy(int radix) {
        this.radix = radix;
    }

    @Override
    public DataStorage carryCRT(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, long resultSize) throws ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        Class<?> elementArrayType = builderFactory.getElementArrayType();
        return this.doCarryCRT(elementArrayType, resultMod0, resultMod1, resultMod2, resultSize);
    }

    private <T> DataStorage doCarryCRT(Class<T> elementArrayType, DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, long resultSize) throws ApfloatRuntimeException {
        long size = Math.min(resultSize + 2L, resultMod0.getSize());
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        DataStorageBuilder dataStorageBuilder = builderFactory.getDataStorageBuilder();
        DataStorage dataStorage = dataStorageBuilder.createDataStorage(resultSize * (long)builderFactory.getElementSize());
        dataStorage.setSize(resultSize);
        ParallelRunnable parallelRunnable = this.createCarryCRTParallelRunnable(elementArrayType, resultMod0, resultMod1, resultMod2, dataStorage, size, resultSize);
        if (size <= Integer.MAX_VALUE && resultMod0.isCached() && resultMod1.isCached() && resultMod2.isCached() && dataStorage.isCached()) {
            ParallelRunner.runParallel(parallelRunnable);
        } else {
            parallelRunnable.getRunnable(0L, size).run();
        }
        return dataStorage;
    }

    protected <T> ParallelRunnable createCarryCRTParallelRunnable(Class<T> elementArrayType, final DataStorage resultMod0, final DataStorage resultMod1, final DataStorage resultMod2, final DataStorage dataStorage, final long size, final long resultSize) {
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        final MessagePasser messagePasser = new MessagePasser();
        final CarryCRTStepStrategy<T> stepStrategy = builderFactory.getCarryCRTBuilder(elementArrayType).createCarryCRTSteps(this.radix);
        ParallelRunnable parallelRunnable = new ParallelRunnable(size){

            @Override
            public Runnable getRunnable(long offset, long length) {
                return new CarryCRTRunnable(resultMod0, resultMod1, resultMod2, dataStorage, size, resultSize, offset, length, messagePasser, stepStrategy);
            }
        };
        return parallelRunnable;
    }

    private class CarryCRTRunnable<T>
    implements Runnable {
        private DataStorage resultMod0;
        private DataStorage resultMod1;
        private DataStorage resultMod2;
        private DataStorage dataStorage;
        private long size;
        private long resultSize;
        private long offset;
        private long length;
        private MessagePasser<Long, T> messagePasser;
        private CarryCRTStepStrategy<T> stepStrategy;

        public CarryCRTRunnable(DataStorage resultMod0, DataStorage resultMod1, DataStorage resultMod2, DataStorage dataStorage, long size, long resultSize, long offset, long length, MessagePasser<Long, T> messagePasser, CarryCRTStepStrategy<T> stepStrategy) {
            this.resultMod0 = resultMod0;
            this.resultMod1 = resultMod1;
            this.resultMod2 = resultMod2;
            this.dataStorage = dataStorage;
            this.size = size;
            this.resultSize = resultSize;
            this.offset = offset;
            this.length = length;
            this.messagePasser = messagePasser;
            this.stepStrategy = stepStrategy;
        }

        @Override
        public void run() {
            T results = this.stepStrategy.crt(this.resultMod0, this.resultMod1, this.resultMod2, this.dataStorage, this.size, this.resultSize, this.offset, this.length);
            if (this.offset > 0L) {
                T previousResults = this.messagePasser.receiveMessage(this.offset);
                results = this.stepStrategy.carry(this.dataStorage, this.size, this.resultSize, this.offset, this.length, results, previousResults);
            }
            this.messagePasser.sendMessage(this.offset + this.length, results);
            if (this.offset + this.length == this.size) {
                assert (results != null);
                assert (Array.getLength(results) == 2);
                assert (((Number)Array.get(results, 0)).longValue() == 0L);
                assert (((Number)Array.get(results, 1)).longValue() == 0L);
            }
        }
    }
}

