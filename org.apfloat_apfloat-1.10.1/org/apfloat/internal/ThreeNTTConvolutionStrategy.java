/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.CarryCRTStrategy;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.NTTConvolutionStepStrategy;
import org.apfloat.spi.NTTStrategy;

public class ThreeNTTConvolutionStrategy
implements ConvolutionStrategy {
    protected NTTStrategy nttStrategy;
    protected CarryCRTStrategy carryCRTStrategy;
    protected NTTConvolutionStepStrategy stepStrategy;

    public ThreeNTTConvolutionStrategy(int radix, NTTStrategy nttStrategy) {
        ApfloatContext ctx = ApfloatContext.getContext();
        BuilderFactory builderFactory = ctx.getBuilderFactory();
        this.nttStrategy = nttStrategy;
        this.carryCRTStrategy = builderFactory.getCarryCRTBuilder(builderFactory.getElementArrayType()).createCarryCRT(radix);
        this.stepStrategy = builderFactory.getNTTBuilder().createNTTConvolutionSteps();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DataStorage convolute(DataStorage x, DataStorage y, long resultSize) throws ApfloatRuntimeException {
        DataStorage result;
        if (x == y) {
            return this.autoConvolute(x, resultSize);
        }
        long length = this.nttStrategy.getTransformLength(x.getSize() + y.getSize());
        this.lock(length);
        try {
            DataStorage resultMod0 = this.convoluteOne(x, y, length, 0, false);
            DataStorage resultMod1 = this.convoluteOne(x, y, length, 1, false);
            DataStorage resultMod2 = this.convoluteOne(x, y, length, 2, true);
            result = this.carryCRTStrategy.carryCRT(resultMod0, resultMod1, resultMod2, resultSize);
        }
        finally {
            this.unlock();
        }
        return result;
    }

    protected DataStorage convoluteOne(DataStorage x, DataStorage y, long length, int modulus, boolean cached) throws ApfloatRuntimeException {
        DataStorage tmpY = this.createCachedDataStorage(length);
        tmpY.copyFrom(y, length);
        this.nttStrategy.transform(tmpY, modulus);
        tmpY = this.createDataStorage(tmpY);
        DataStorage tmpX = this.createCachedDataStorage(length);
        tmpX.copyFrom(x, length);
        this.nttStrategy.transform(tmpX, modulus);
        this.stepStrategy.multiplyInPlace(tmpX, tmpY, modulus);
        this.nttStrategy.inverseTransform(tmpX, modulus, length);
        tmpX = cached ? tmpX : this.createDataStorage(tmpX);
        return tmpX;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DataStorage autoConvolute(DataStorage x, long resultSize) throws ApfloatRuntimeException {
        DataStorage result;
        long length = this.nttStrategy.getTransformLength(x.getSize() * 2L);
        this.lock(length);
        try {
            DataStorage resultMod0 = this.autoConvoluteOne(x, length, 0, false);
            DataStorage resultMod1 = this.autoConvoluteOne(x, length, 1, false);
            DataStorage resultMod2 = this.autoConvoluteOne(x, length, 2, true);
            result = this.carryCRTStrategy.carryCRT(resultMod0, resultMod1, resultMod2, resultSize);
        }
        finally {
            this.unlock();
        }
        return result;
    }

    protected DataStorage autoConvoluteOne(DataStorage x, long length, int modulus, boolean cached) throws ApfloatRuntimeException {
        DataStorage tmp = this.createCachedDataStorage(length);
        tmp.copyFrom(x, length);
        this.nttStrategy.transform(tmp, modulus);
        this.stepStrategy.squareInPlace(tmp, modulus);
        this.nttStrategy.inverseTransform(tmp, modulus, length);
        tmp = cached ? tmp : this.createDataStorage(tmp);
        return tmp;
    }

    protected void lock(long length) {
    }

    protected void unlock() {
    }

    protected DataStorage createCachedDataStorage(long size) throws ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        return dataStorageBuilder.createCachedDataStorage(size * (long)ctx.getBuilderFactory().getElementSize());
    }

    protected DataStorage createDataStorage(DataStorage dataStorage) throws ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        return dataStorageBuilder.createDataStorage(dataStorage);
    }
}

