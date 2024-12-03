/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.internal.SdkInternalList;
import com.amazonaws.internal.SdkInternalMap;
import com.amazonaws.protocol.MarshallLocation;
import com.amazonaws.protocol.MarshallingInfo;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.protocol.json.StructuredJsonMarshaller;
import com.amazonaws.protocol.json.internal.JsonMarshaller;
import com.amazonaws.protocol.json.internal.JsonMarshallerContext;
import com.amazonaws.util.TimestampFormat;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SdkInternalApi
public class SimpleTypeJsonMarshallers {
    public static final JsonMarshaller<Void> NULL = new JsonMarshaller<Void>(){

        @Override
        public void marshall(Void val, JsonMarshallerContext context, MarshallingInfo<Void> marshallingInfo) {
            if (marshallingInfo == null) {
                context.jsonGenerator().writeNull();
            } else if (marshallingInfo.isExplicitPayloadMember()) {
                if (marshallingInfo.marshallLocationName() != null) {
                    throw new IllegalStateException("Expected marshalling location name to be null if explicit member is null");
                }
                context.emptyBodyJsonMarshaller().marshall(context.jsonGenerator());
            }
        }
    };
    public static final JsonMarshaller<String> STRING = new BaseJsonMarshaller<String>(){

        @Override
        public void marshall(String val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<String> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Integer> INTEGER = new BaseJsonMarshaller<Integer>(){

        @Override
        public void marshall(Integer val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Integer> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Long> LONG = new BaseJsonMarshaller<Long>(){

        @Override
        public void marshall(Long val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Long> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Short> SHORT = new BaseJsonMarshaller<Short>(){

        @Override
        public void marshall(Short val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Short> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Float> FLOAT = new BaseJsonMarshaller<Float>(){

        @Override
        public void marshall(Float val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Float> marshallingInfo) {
            jsonGenerator.writeValue(val.floatValue());
        }
    };
    public static final JsonMarshaller<BigDecimal> BIG_DECIMAL = new BaseJsonMarshaller<BigDecimal>(){

        @Override
        public void marshall(BigDecimal val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<BigDecimal> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Double> DOUBLE = new BaseJsonMarshaller<Double>(){

        @Override
        public void marshall(Double val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Double> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Boolean> BOOLEAN = new BaseJsonMarshaller<Boolean>(){

        @Override
        public void marshall(Boolean val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Boolean> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Date> DATE = new BaseJsonMarshaller<Date>(){

        @Override
        public void marshall(Date val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Date> marshallingInfo) {
            TimestampFormat timestampFormat = TimestampFormat.UNIX_TIMESTAMP;
            if (marshallingInfo != null && marshallingInfo.timestampFormat() != null) {
                timestampFormat = marshallingInfo.timestampFormat();
            }
            jsonGenerator.writeValue(val, timestampFormat);
        }
    };
    public static final JsonMarshaller<ByteBuffer> BYTE_BUFFER = new BaseJsonMarshaller<ByteBuffer>(){

        @Override
        public void marshall(ByteBuffer val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<ByteBuffer> marshallingInfo) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<StructuredPojo> STRUCTURED = new BaseJsonMarshaller<StructuredPojo>(){

        @Override
        public void marshall(StructuredPojo val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<StructuredPojo> marshallingInfo) {
            jsonGenerator.writeStartObject();
            val.marshall(context.protocolHandler());
            jsonGenerator.writeEndObject();
        }
    };
    public static final JsonMarshaller<List> LIST = new BaseJsonMarshaller<List>(){

        @Override
        public void marshall(List list, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<List> marshallingInfo) {
            jsonGenerator.writeStartArray();
            for (Object listValue : list) {
                context.marshall(MarshallLocation.PAYLOAD, listValue);
            }
            jsonGenerator.writeEndArray();
        }

        @Override
        protected boolean shouldEmit(List list) {
            return !list.isEmpty() || !(list instanceof SdkInternalList) || !((SdkInternalList)list).isAutoConstruct();
        }
    };
    public static final JsonMarshaller<Map> MAP = new BaseJsonMarshaller<Map>(){

        @Override
        public void marshall(Map map, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<Map> mapMarshallingInfo) {
            jsonGenerator.writeStartObject();
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() == null) continue;
                Object value = entry.getValue();
                jsonGenerator.writeFieldName((String)entry.getKey());
                context.marshall(MarshallLocation.PAYLOAD, value);
            }
            jsonGenerator.writeEndObject();
        }

        @Override
        protected boolean shouldEmit(Map map) {
            return !map.isEmpty() || !(map instanceof SdkInternalMap) || !((SdkInternalMap)map).isAutoConstruct();
        }
    };

    public static <T> JsonMarshaller<T> adapt(final StructuredJsonMarshaller<T> toAdapt) {
        return new BaseJsonMarshaller<T>(){

            @Override
            public void marshall(T val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context, MarshallingInfo<T> marshallingInfo) {
                toAdapt.marshall(val, jsonGenerator);
            }
        };
    }

    private static abstract class BaseJsonMarshaller<T>
    implements JsonMarshaller<T> {
        private BaseJsonMarshaller() {
        }

        @Override
        public final void marshall(T val, JsonMarshallerContext context, MarshallingInfo<T> marshallingInfo) {
            if (!this.shouldEmit(val)) {
                return;
            }
            if (marshallingInfo != null && marshallingInfo.marshallLocationName() != null) {
                context.jsonGenerator().writeFieldName(marshallingInfo.marshallLocationName());
            }
            this.marshall(val, context.jsonGenerator(), context, marshallingInfo);
        }

        public abstract void marshall(T var1, StructuredJsonGenerator var2, JsonMarshallerContext var3, MarshallingInfo<T> var4);

        protected boolean shouldEmit(T val) {
            return true;
        }
    }
}

