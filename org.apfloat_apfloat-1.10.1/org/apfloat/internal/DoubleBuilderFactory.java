/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.DiskDataStorage;
import org.apfloat.internal.DoubleAdditionBuilder;
import org.apfloat.internal.DoubleApfloatBuilder;
import org.apfloat.internal.DoubleCarryCRTBuilder;
import org.apfloat.internal.DoubleConvolutionBuilder;
import org.apfloat.internal.DoubleDataStorageBuilder;
import org.apfloat.internal.DoubleMatrixBuilder;
import org.apfloat.internal.DoubleNTTBuilder;
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

public class DoubleBuilderFactory
implements BuilderFactory {
    private static ApfloatBuilder apfloatBuilder = new DoubleApfloatBuilder();
    private static DataStorageBuilder dataStorageBuilder = new DoubleDataStorageBuilder();
    private static AdditionBuilder<Double> additionBuilder = new DoubleAdditionBuilder();
    private static ConvolutionBuilder convolutionBuilder = new DoubleConvolutionBuilder();
    private static NTTBuilder nttBuilder = new DoubleNTTBuilder();
    private static MatrixBuilder matrixBuilder = new DoubleMatrixBuilder();
    private static CarryCRTBuilder<double[]> carryCRTBuilder = new DoubleCarryCRTBuilder();
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
        if (!Double.TYPE.equals(elementType)) {
            throw new IllegalArgumentException("Unsupported element type: " + elementType);
        }
        AdditionBuilder<Double> additionBuilder = DoubleBuilderFactory.additionBuilder;
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
        if (!double[].class.equals(elementArrayType)) {
            throw new IllegalArgumentException("Unsupported element array type: " + elementArrayType);
        }
        CarryCRTBuilder<double[]> carryCRTBuilder = DoubleBuilderFactory.carryCRTBuilder;
        return carryCRTBuilder;
    }

    @Override
    public ExecutionBuilder getExecutionBuilder() {
        return executionBuilder;
    }

    @Override
    public Class<?> getElementType() {
        return Double.TYPE;
    }

    @Override
    public Class<?> getElementArrayType() {
        return double[].class;
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

