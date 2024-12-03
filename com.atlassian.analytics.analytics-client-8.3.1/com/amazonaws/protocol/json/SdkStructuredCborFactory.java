/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.SdkCborGenerator;
import com.amazonaws.protocol.json.SdkStructuredJsonFactory;
import com.amazonaws.protocol.json.SdkStructuredJsonFactoryImpl;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeCborUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.ImmutableMapParameter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

@SdkInternalApi
class SdkStructuredCborFactory {
    private static final JsonFactory CBOR_FACTORY = new CBORFactory();
    private static final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> CBOR_SCALAR_UNMARSHALLERS = new ImmutableMapParameter.Builder<Class<String>, SimpleTypeCborUnmarshallers.StringCborUnmarshaller>().put(String.class, SimpleTypeCborUnmarshallers.StringCborUnmarshaller.getInstance()).put(Double.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.DoubleCborUnmarshaller.getInstance())).put(Integer.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.IntegerCborUnmarshaller.getInstance())).put(BigInteger.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.BigIntegerCborUnmarshaller.getInstance())).put(BigDecimal.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.BigDecimalCborUnmarshaller.getInstance())).put(Boolean.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.BooleanCborUnmarshaller.getInstance())).put(Float.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.FloatCborUnmarshaller.getInstance())).put(Long.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.LongCborUnmarshaller.getInstance())).put(Byte.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.ByteCborUnmarshaller.getInstance())).put(Date.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.DateCborUnmarshaller.getInstance())).put(ByteBuffer.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.ByteBufferCborUnmarshaller.getInstance())).put(Short.class, (SimpleTypeCborUnmarshallers.StringCborUnmarshaller)((Object)SimpleTypeCborUnmarshallers.ShortCborUnmarshaller.getInstance())).build();
    public static final SdkStructuredJsonFactory SDK_CBOR_FACTORY = new SdkStructuredJsonFactoryImpl(CBOR_FACTORY, (Map)CBOR_SCALAR_UNMARSHALLERS, Collections.emptyMap()){

        @Override
        protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory, String contentType) {
            return new SdkCborGenerator(jsonFactory, contentType);
        }
    };

    SdkStructuredCborFactory() {
    }
}

