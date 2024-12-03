/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.http.CompositeErrorCodeParser;
import com.amazonaws.internal.http.ErrorCodeParser;
import com.amazonaws.internal.http.IonErrorCodeParser;
import com.amazonaws.internal.http.JsonErrorCodeParser;
import com.amazonaws.protocol.json.IonFactory;
import com.amazonaws.protocol.json.SdkIonGenerator;
import com.amazonaws.protocol.json.SdkStructuredJsonFactoryImpl;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeIonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.ImmutableMapParameter;
import com.fasterxml.jackson.core.JsonFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import software.amazon.ion.IonSystem;
import software.amazon.ion.system.IonBinaryWriterBuilder;
import software.amazon.ion.system.IonSystemBuilder;
import software.amazon.ion.system.IonTextWriterBuilder;
import software.amazon.ion.system.IonWriterBuilder;

@SdkInternalApi
class SdkStructuredIonFactory
extends SdkStructuredJsonFactoryImpl {
    private static final IonSystem ION_SYSTEM = IonSystemBuilder.standard().build();
    private static final JsonFactory JSON_FACTORY = new IonFactory(ION_SYSTEM);
    private static final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> UNMARSHALLERS = new ImmutableMapParameter.Builder<Class<BigDecimal>, SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller>().put(BigDecimal.class, SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller.getInstance()).put(BigInteger.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.BigIntegerIonUnmarshaller.getInstance())).put(Boolean.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.BooleanIonUnmarshaller.getInstance())).put(ByteBuffer.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.ByteBufferIonUnmarshaller.getInstance())).put(Byte.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.ByteIonUnmarshaller.getInstance())).put(Date.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.DateIonUnmarshaller.getInstance())).put(Double.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.DoubleIonUnmarshaller.getInstance())).put(Float.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.FloatIonUnmarshaller.getInstance())).put(Integer.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.IntegerIonUnmarshaller.getInstance())).put(Long.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.LongIonUnmarshaller.getInstance())).put(Short.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.ShortIonUnmarshaller.getInstance())).put(String.class, (SimpleTypeIonUnmarshallers.BigDecimalIonUnmarshaller)((Object)SimpleTypeIonUnmarshallers.StringIonUnmarshaller.getInstance())).build();
    private static final IonBinaryWriterBuilder BINARY_WRITER_BUILDER = IonBinaryWriterBuilder.standard().immutable();
    private static final IonTextWriterBuilder TEXT_WRITER_BUILDER = IonTextWriterBuilder.standard().immutable();
    public static final SdkStructuredIonFactory SDK_ION_BINARY_FACTORY = new SdkStructuredIonFactory(BINARY_WRITER_BUILDER);
    public static final SdkStructuredIonFactory SDK_ION_TEXT_FACTORY = new SdkStructuredIonFactory(TEXT_WRITER_BUILDER);
    private final IonWriterBuilder builder;

    private SdkStructuredIonFactory(IonWriterBuilder builder) {
        super(JSON_FACTORY, UNMARSHALLERS, Collections.emptyMap());
        this.builder = builder;
    }

    @Override
    protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory, String contentType) {
        return SdkIonGenerator.create(this.builder, contentType);
    }

    @Override
    protected ErrorCodeParser getErrorCodeParser(String customErrorCodeFieldName) {
        return new CompositeErrorCodeParser(new IonErrorCodeParser(ION_SYSTEM), new JsonErrorCodeParser(customErrorCodeFieldName));
    }
}

