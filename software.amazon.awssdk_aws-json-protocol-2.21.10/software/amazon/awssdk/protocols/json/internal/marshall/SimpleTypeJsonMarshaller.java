/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.document.Document
 *  software.amazon.awssdk.core.document.VoidDocumentVisitor
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructMap
 *  software.amazon.awssdk.utils.DateUtils
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.VoidDocumentVisitor;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.json.internal.marshall.DocumentTypeJsonMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerContext;
import software.amazon.awssdk.utils.DateUtils;

@SdkInternalApi
public final class SimpleTypeJsonMarshaller {
    public static final JsonMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        if (Objects.nonNull(sdkField) && sdkField.containsTrait(RequiredTrait.class)) {
            throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", Optional.ofNullable(paramName).orElseGet(() -> "paramName null")));
        }
        if (paramName == null) {
            context.jsonGenerator().writeNull();
        }
    };
    public static final JsonMarshaller<String> STRING = new BaseJsonMarshaller<String>(){

        @Override
        public void marshall(String val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Integer> INTEGER = new BaseJsonMarshaller<Integer>(){

        @Override
        public void marshall(Integer val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Long> LONG = new BaseJsonMarshaller<Long>(){

        @Override
        public void marshall(Long val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Short> SHORT = new BaseJsonMarshaller<Short>(){

        @Override
        public void marshall(Short val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Float> FLOAT = new BaseJsonMarshaller<Float>(){

        @Override
        public void marshall(Float val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val.floatValue());
        }
    };
    public static final JsonMarshaller<BigDecimal> BIG_DECIMAL = new BaseJsonMarshaller<BigDecimal>(){

        @Override
        public void marshall(BigDecimal val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Double> DOUBLE = new BaseJsonMarshaller<Double>(){

        @Override
        public void marshall(Double val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Boolean> BOOLEAN = new BaseJsonMarshaller<Boolean>(){

        @Override
        public void marshall(Boolean val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val);
        }
    };
    public static final JsonMarshaller<Instant> INSTANT = (val, context, paramName, sdkField) -> {
        block7: {
            StructuredJsonGenerator jsonGenerator;
            block6: {
                TimestampFormatTrait trait;
                jsonGenerator = context.jsonGenerator();
                if (paramName != null) {
                    jsonGenerator.writeFieldName(paramName);
                }
                TimestampFormatTrait timestampFormatTrait = trait = sdkField != null ? (TimestampFormatTrait)sdkField.getTrait(TimestampFormatTrait.class) : null;
                if (trait == null) break block6;
                switch (trait.format()) {
                    case UNIX_TIMESTAMP: {
                        jsonGenerator.writeNumber(DateUtils.formatUnixTimestampInstant((Instant)val));
                        break block7;
                    }
                    case RFC_822: {
                        jsonGenerator.writeValue(DateUtils.formatRfc822Date((Instant)val));
                        break block7;
                    }
                    case ISO_8601: {
                        jsonGenerator.writeValue(DateUtils.formatIso8601Date((Instant)val));
                        break block7;
                    }
                    default: {
                        throw SdkClientException.create((String)("Unrecognized timestamp format - " + trait.format()));
                    }
                }
            }
            jsonGenerator.writeValue((Instant)val);
        }
    };
    public static final JsonMarshaller<SdkBytes> SDK_BYTES = new BaseJsonMarshaller<SdkBytes>(){

        @Override
        public void marshall(SdkBytes val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeValue(val.asByteBuffer());
        }
    };
    public static final JsonMarshaller<SdkPojo> SDK_POJO = new BaseJsonMarshaller<SdkPojo>(){

        @Override
        public void marshall(SdkPojo val, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeStartObject();
            context.protocolHandler().doMarshall(val);
            jsonGenerator.writeEndObject();
        }
    };
    public static final JsonMarshaller<List<?>> LIST = new BaseJsonMarshaller<List<?>>(){

        @Override
        public void marshall(List<?> list, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeStartArray();
            for (Object listValue : list) {
                context.marshall(MarshallLocation.PAYLOAD, listValue);
            }
            jsonGenerator.writeEndArray();
        }

        @Override
        protected boolean shouldEmit(List list) {
            return !list.isEmpty() || !(list instanceof SdkAutoConstructList);
        }
    };
    public static final JsonMarshaller<Map<String, ?>> MAP = new BaseJsonMarshaller<Map<String, ?>>(){

        @Override
        public void marshall(Map<String, ?> map, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            jsonGenerator.writeStartObject();
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                if (entry.getValue() == null) continue;
                Object value = entry.getValue();
                jsonGenerator.writeFieldName(entry.getKey());
                context.marshall(MarshallLocation.PAYLOAD, value);
            }
            jsonGenerator.writeEndObject();
        }

        @Override
        protected boolean shouldEmit(Map<String, ?> map) {
            return !map.isEmpty() || !(map instanceof SdkAutoConstructMap);
        }
    };
    public static final JsonMarshaller<Document> DOCUMENT = new BaseJsonMarshaller<Document>(){

        @Override
        public void marshall(Document document, StructuredJsonGenerator jsonGenerator, JsonMarshallerContext context) {
            document.accept((VoidDocumentVisitor)new DocumentTypeJsonMarshaller(jsonGenerator));
        }
    };

    private SimpleTypeJsonMarshaller() {
    }

    private static abstract class BaseJsonMarshaller<T>
    implements JsonMarshaller<T> {
        private BaseJsonMarshaller() {
        }

        @Override
        public final void marshall(T val, JsonMarshallerContext context, String paramName, SdkField<T> sdkField) {
            if (!this.shouldEmit(val)) {
                return;
            }
            if (paramName != null) {
                context.jsonGenerator().writeFieldName(paramName);
            }
            this.marshall(val, context.jsonGenerator(), context);
        }

        public abstract void marshall(T var1, StructuredJsonGenerator var2, JsonMarshallerContext var3);

        protected boolean shouldEmit(T val) {
            return true;
        }
    }
}

