/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.ApfloatBuilder;
import org.apfloat.spi.CarryCRTBuilder;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.ExecutionBuilder;
import org.apfloat.spi.MatrixBuilder;
import org.apfloat.spi.NTTBuilder;

public interface BuilderFactory {
    public ApfloatBuilder getApfloatBuilder();

    public DataStorageBuilder getDataStorageBuilder();

    public <T> AdditionBuilder<T> getAdditionBuilder(Class<T> var1) throws IllegalArgumentException;

    public ConvolutionBuilder getConvolutionBuilder();

    public NTTBuilder getNTTBuilder();

    public MatrixBuilder getMatrixBuilder();

    public <T> CarryCRTBuilder<T> getCarryCRTBuilder(Class<T> var1) throws IllegalArgumentException;

    public ExecutionBuilder getExecutionBuilder();

    public Class<?> getElementType();

    public Class<?> getElementArrayType();

    public int getElementSize();

    public void shutdown() throws ApfloatRuntimeException;

    public void gc() throws ApfloatRuntimeException;
}

