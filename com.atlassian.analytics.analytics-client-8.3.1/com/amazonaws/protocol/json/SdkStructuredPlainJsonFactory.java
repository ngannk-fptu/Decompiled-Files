/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.annotation.SdkTestInternalApi;
import com.amazonaws.protocol.json.SdkJsonGenerator;
import com.amazonaws.protocol.json.SdkStructuredJsonFactory;
import com.amazonaws.protocol.json.SdkStructuredJsonFactoryImpl;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.transform.JsonUnmarshallerContext;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers;
import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.ImmutableMapParameter;
import com.fasterxml.jackson.core.JsonFactory;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Map;

@SdkProtectedApi
public class SdkStructuredPlainJsonFactory {
    public static final JsonFactory JSON_FACTORY = new JsonFactory();
    @SdkTestInternalApi
    public static final Map<Class<?>, Unmarshaller<?, JsonUnmarshallerContext>> JSON_SCALAR_UNMARSHALLERS = new ImmutableMapParameter.Builder<Class<String>, SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller>().put(String.class, SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller.getInstance()).put(Double.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.DoubleJsonUnmarshaller.getInstance())).put(Integer.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.IntegerJsonUnmarshaller.getInstance())).put(BigInteger.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.BigIntegerJsonUnmarshaller.getInstance())).put(BigDecimal.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.BigDecimalJsonUnmarshaller.getInstance())).put(Boolean.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.BooleanJsonUnmarshaller.getInstance())).put(Float.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.FloatJsonUnmarshaller.getInstance())).put(Long.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.LongJsonUnmarshaller.getInstance())).put(Byte.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.ByteJsonUnmarshaller.getInstance())).put(Date.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.DateJsonUnmarshaller.getInstance())).put(ByteBuffer.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.ByteBufferJsonUnmarshaller.getInstance())).put(Character.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.CharacterJsonUnmarshaller.getInstance())).put(Short.class, (SimpleTypeJsonUnmarshallers.StringJsonUnmarshaller)((Object)SimpleTypeJsonUnmarshallers.ShortJsonUnmarshaller.getInstance())).build();
    @SdkTestInternalApi
    public static final Map<JsonUnmarshallerContext.UnmarshallerType, Unmarshaller<?, JsonUnmarshallerContext>> JSON_CUSTOM_TYPE_UNMARSHALLERS = new ImmutableMapParameter.Builder<JsonUnmarshallerContext.UnmarshallerType, SimpleTypeJsonUnmarshallers.JsonValueStringUnmarshaller>().put(JsonUnmarshallerContext.UnmarshallerType.JSON_VALUE, SimpleTypeJsonUnmarshallers.JsonValueStringUnmarshaller.getInstance()).build();
    public static final SdkStructuredJsonFactory SDK_JSON_FACTORY = new SdkStructuredJsonFactoryImpl(JSON_FACTORY, (Map)JSON_SCALAR_UNMARSHALLERS, (Map)JSON_CUSTOM_TYPE_UNMARSHALLERS){

        @Override
        protected StructuredJsonGenerator createWriter(JsonFactory jsonFactory, String contentType) {
            return new SdkJsonGenerator(jsonFactory, contentType);
        }
    };
}

