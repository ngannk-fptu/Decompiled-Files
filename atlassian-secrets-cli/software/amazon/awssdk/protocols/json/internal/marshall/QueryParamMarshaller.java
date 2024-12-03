/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerContext;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonProtocolMarshaller;

@SdkInternalApi
public final class QueryParamMarshaller {
    public static final JsonMarshaller<String> STRING = new SimpleQueryParamMarshaller<String>(ValueToStringConverter.FROM_STRING);
    public static final JsonMarshaller<Integer> INTEGER = new SimpleQueryParamMarshaller<Integer>(ValueToStringConverter.FROM_INTEGER);
    public static final JsonMarshaller<Long> LONG = new SimpleQueryParamMarshaller<Long>(ValueToStringConverter.FROM_LONG);
    public static final JsonMarshaller<Short> SHORT = new SimpleQueryParamMarshaller<Short>(ValueToStringConverter.FROM_SHORT);
    public static final JsonMarshaller<Double> DOUBLE = new SimpleQueryParamMarshaller<Double>(ValueToStringConverter.FROM_DOUBLE);
    public static final JsonMarshaller<Float> FLOAT = new SimpleQueryParamMarshaller<Float>(ValueToStringConverter.FROM_FLOAT);
    public static final JsonMarshaller<Boolean> BOOLEAN = new SimpleQueryParamMarshaller<Boolean>(ValueToStringConverter.FROM_BOOLEAN);
    public static final JsonMarshaller<Instant> INSTANT = new SimpleQueryParamMarshaller<Instant>(JsonProtocolMarshaller.INSTANT_VALUE_TO_STRING);
    public static final JsonMarshaller<List<?>> LIST = (list, context, paramName, sdkField) -> {
        for (Object listVal : list) {
            context.marshall(MarshallLocation.QUERY_PARAM, listVal, paramName);
        }
    };
    public static final JsonMarshaller<Map<String, ?>> MAP = (val, context, paramName, sdkField) -> {
        for (Map.Entry mapEntry : val.entrySet()) {
            context.marshall(MarshallLocation.QUERY_PARAM, mapEntry.getValue(), (String)mapEntry.getKey());
        }
    };
    public static final JsonMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        if (Objects.nonNull(sdkField) && sdkField.containsTrait(RequiredTrait.class)) {
            throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
        }
    };

    private QueryParamMarshaller() {
    }

    private static class SimpleQueryParamMarshaller<T>
    implements JsonMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;

        private SimpleQueryParamMarshaller(ValueToStringConverter.ValueToString<T> converter) {
            this.converter = converter;
        }

        @Override
        public void marshall(T val, JsonMarshallerContext context, String paramName, SdkField<T> sdkField) {
            context.request().appendRawQueryParameter(paramName, this.converter.convert(val, sdkField));
        }
    }
}

