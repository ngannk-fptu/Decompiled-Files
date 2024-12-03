/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.protocol.DefaultMarshallingType;
import com.amazonaws.protocol.StructuredPojo;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SdkProtectedApi
public interface MarshallingType<T> {
    public static final MarshallingType<Void> NULL = new DefaultMarshallingType<Void>(Void.class);
    public static final MarshallingType<String> STRING = new DefaultMarshallingType<String>(String.class);
    public static final MarshallingType<Integer> INTEGER = new DefaultMarshallingType<Integer>(Integer.class);
    public static final MarshallingType<Long> LONG = new DefaultMarshallingType<Long>(Long.class);
    public static final MarshallingType<Short> SHORT = new DefaultMarshallingType<Short>(Short.class);
    public static final MarshallingType<Float> FLOAT = new DefaultMarshallingType<Float>(Float.class);
    public static final MarshallingType<Double> DOUBLE = new DefaultMarshallingType<Double>(Double.class);
    public static final MarshallingType<BigDecimal> BIG_DECIMAL = new DefaultMarshallingType<BigDecimal>(BigDecimal.class);
    public static final MarshallingType<Boolean> BOOLEAN = new DefaultMarshallingType<Boolean>(Boolean.class);
    public static final MarshallingType<Date> DATE = new DefaultMarshallingType<Date>(Date.class);
    public static final MarshallingType<ByteBuffer> BYTE_BUFFER = new DefaultMarshallingType<ByteBuffer>(ByteBuffer.class);
    public static final MarshallingType<InputStream> STREAM = new DefaultMarshallingType<InputStream>(InputStream.class);
    public static final MarshallingType<StructuredPojo> STRUCTURED = new DefaultMarshallingType<StructuredPojo>(StructuredPojo.class);
    public static final MarshallingType<List> LIST = new DefaultMarshallingType<List>(List.class);
    public static final MarshallingType<Map> MAP = new DefaultMarshallingType<Map>(Map.class);
    public static final MarshallingType<String> JSON_VALUE = new MarshallingType<String>(){

        @Override
        public boolean isDefaultMarshallerForType(Class<?> type) {
            return false;
        }
    };

    public boolean isDefaultMarshallerForType(Class<?> var1);
}

