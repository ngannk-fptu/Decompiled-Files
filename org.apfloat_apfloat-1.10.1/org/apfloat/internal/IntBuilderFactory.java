/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.IntAdditionBuilder;
import org.apfloat.internal.IntApfloatBuilder;
import org.apfloat.internal.IntCarryCRTBuilder;
import org.apfloat.internal.IntConvolutionBuilder;
import org.apfloat.internal.IntDataStorageBuilder;
import org.apfloat.internal.IntMatrixBuilder;
import org.apfloat.internal.IntNTTBuilder;
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

public class IntBuilderFactory
implements BuilderFactory {
    private static ApfloatBuilder apfloatBuilder = new IntApfloatBuilder();
    private static DataStorageBuilder dataStorageBuilder = new IntDataStorageBuilder();
    private static AdditionBuilder<Integer> additionBuilder = new IntAdditionBuilder();
    private static ConvolutionBuilder convolutionBuilder = new IntConvolutionBuilder();
    private static NTTBuilder nttBuilder = new IntNTTBuilder();
    private static MatrixBuilder matrixBuilder = new IntMatrixBuilder();
    private static CarryCRTBuilder<int[]> carryCRTBuilder = new IntCarryCRTBuilder();
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
        if (!Integer.TYPE.equals(elementType)) {
            throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
        AdditionBuilder<Integer> additionBuilder = IntBuilderFactory.additionBuilder;
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
        if (!int[].class.equals(elementArrayType)) {
            throw new IllegalArgumentException("Unsupported element array type: " + elementArrayType);
        }
        CarryCRTBuilder<int[]> carryCRTBuilder = IntBuilderFactory.carryCRTBuilder;
        return carryCRTBuilder;
    }

    @Override
    public ExecutionBuilder getExecutionBuilder() {
        return executionBuilder;
    }

    @Override
    public Class<?> getElementType() {
        return Integer.TYPE;
    }

    @Override
    public Class<?> getElementArrayType() {
        return int[].class;
    }

    @Override
    public int getElementSize() {
        return 4;
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

