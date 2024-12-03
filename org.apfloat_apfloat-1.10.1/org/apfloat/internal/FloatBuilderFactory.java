/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.FloatAdditionBuilder;
import org.apfloat.internal.FloatApfloatBuilder;
import org.apfloat.internal.FloatCarryCRTBuilder;
import org.apfloat.internal.FloatConvolutionBuilder;
import org.apfloat.internal.FloatDataStorageBuilder;
import org.apfloat.internal.FloatMatrixBuilder;
import org.apfloat.internal.FloatNTTBuilder;
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

public class FloatBuilderFactory
implements BuilderFactory {
    private static ApfloatBuilder apfloatBuilder = new FloatApfloatBuilder();
    private static DataStorageBuilder dataStorageBuilder = new FloatDataStorageBuilder();
    private static AdditionBuilder<Float> additionBuilder = new FloatAdditionBuilder();
    private static ConvolutionBuilder convolutionBuilder = new FloatConvolutionBuilder();
    private static NTTBuilder nttBuilder = new FloatNTTBuilder();
    private static MatrixBuilder matrixBuilder = new FloatMatrixBuilder();
    private static CarryCRTBuilder<float[]> carryCRTBuilder = new FloatCarryCRTBuilder();
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
        if (!Float.TYPE.equals(elementType)) {
            throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
        AdditionBuilder<Float> additionBuilder = FloatBuilderFactory.additionBuilder;
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
        if (!float[].class.equals(elementArrayType)) {
            throw new IllegalArgumentException("Unsupported element array type: " + elementArrayType);
        }
        CarryCRTBuilder<float[]> carryCRTBuilder = FloatBuilderFactory.carryCRTBuilder;
        return carryCRTBuilder;
    }

    @Override
    public ExecutionBuilder getExecutionBuilder() {
        return executionBuilder;
    }

    @Override
    public Class<?> getElementType() {
        return Float.TYPE;
    }

    @Override
    public Class<?> getElementArrayType() {
        return float[].class;
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

