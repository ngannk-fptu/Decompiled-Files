/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.LongAdditionBuilder;
import org.apfloat.internal.LongApfloatBuilder;
import org.apfloat.internal.LongCarryCRTBuilder;
import org.apfloat.internal.LongConvolutionBuilder;
import org.apfloat.internal.LongDataStorageBuilder;
import org.apfloat.internal.LongMatrixBuilder;
import org.apfloat.internal.LongNTTBuilder;
import org.apfloat.internal.ParallelExecutionBuilder;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.ApfloatBuilder;
import org.apfloat.spi.BuilderFactory;
import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.ExecutionBuilder;
import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.NTTBuilder;

public class LongBuilderFactory
implements BuilderFactory {
    private static ApfloatBuilder apfloatBuilder = new LongApfloatBuilder();
    private static DataStorageBuilder dataStorageBuilder = new LongDataStorageBuilder();
    private static AdditionBuilder<Long> additionBuilder = new LongAdditionBuilder();
    private static ConvolutionBuilder convolutionBuilder = new LongConvolutionBuilder();
    private static NTTBuilder nttBuilder = new LongNTTBuilder();
    private static MatrixBuilder matrixBuilder = new LongMatrixBuilder();
    private static CarryCRTBuilder<long[]> carryCRTBuilder = new LongCarryCRTBuilder();
    private static ExecutionBuilder executionBuilder = new ParallelExecutionBuilder();

    @Override
    public ApfloatBuilder getApfloatBuilder() {
        return apfloatBuilder;
    }

    @Override
    public DataStorageBuilder getDataStorageBuilder() {
        return dataStorageBuilder;
    }

    @Override
    public <T> AdditionBuilder<T> getAdditionBuilder(Class<T> elementType) throws IllegalArgumentException {
        if (!Long.TYPE.equals(elementType)) {
            throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
        AdditionBuilder<Long> additionBuilder = LongBuilderFactory.additionBuilder;
        return additionBuilder;
    }

    @Override
    public ConvolutionBuilder getConvolutionBuilder() {
        return convolutionBuilder;
    }

    @Override
    public NTTBuilder getNTTBuilder() {
        return nttBuilder;
    }

    @Override
    public MatrixBuilder getMatrixBuilder() {
        return matrixBuilder;
    }

    @Override
    public <T> CarryCRTBuilder<T> getCarryCRTBuilder(Class<T> elementArrayType) throws IllegalArgumentException {
        if (!long[].class.equals(elementArrayType)) {
            throw new IllegalArgumentException("Unsupported element array type: " + elementArrayType);
        }
        CarryCRTBuilder<long[]> carryCRTBuilder = LongBuilderFactory.carryCRTBuilder;
        return carryCRTBuilder;
    }

    @Override
    public ExecutionBuilder getExecutionBuilder() {
        return executionBuilder;
    }

    @Override
    public Class<?> getElementType() {
        return Long.TYPE;
    }

    @Override
    public Class<?> getElementArrayType() {
        return long[].class;
    }

    @Override
    public int getElementSize() {
        return 8;
    }

    @Override
    public void shutdown() throws ApfloatRuntimeException {
        DiskDataStorage.cleanUp();
    }

    @Override
    public void gc() throws ApfloatRuntimeException {
        System.gc();
        System.gc();
        System.runFinalization();
        DiskDataStorage.gc();
    }
}

