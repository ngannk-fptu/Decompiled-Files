/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.JsonValueTrait;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerContext;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonProtocolMarshaller;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class HeaderMarshaller {
    public static final JsonMarshaller<String> STRING = new SimpleHeaderMarshaller<String>((val, field) -> field.containsTrait(JsonValueTrait.class) ? BinaryUtils.toBase64(val.getBytes(StandardCharsets.UTF_8)) : val);
    public static final JsonMarshaller<Integer> INTEGER = new SimpleHeaderMarshaller<Integer>(ValueToStringConverter.FROM_INTEGER);
    public static final JsonMarshaller<Long> LONG = new SimpleHeaderMarshaller<Long>(ValueToStringConverter.FROM_LONG);
    public static final JsonMarshaller<Short> SHORT = new SimpleHeaderMarshaller<Short>(ValueToStringConverter.FROM_SHORT);
    public static final JsonMarshaller<Double> DOUBLE = new SimpleHeaderMarshaller<Double>(ValueToStringConverter.FROM_DOUBLE);
    public static final JsonMarshaller<Float> FLOAT = new SimpleHeaderMarshaller<Float>(ValueToStringConverter.FROM_FLOAT);
    public static final JsonMarshaller<Boolean> BOOLEAN = new SimpleHeaderMarshaller<Boolean>(ValueToStringConverter.FROM_BOOLEAN);
    public static final JsonMarshaller<Instant> INSTANT = new SimpleHeaderMarshaller<Instant>(JsonProtocolMarshaller.INSTANT_VALUE_TO_STRING);
    public static final JsonMarshaller<List<?>> LIST = (list, context, paramName, sdkField) -> {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        SdkField memberFieldInfo = sdkField.getRequiredTrait(ListTrait.class).memberFieldInfo();
        for (Object listValue : list) {
            if (HeaderMarshaller.shouldSkipElement(listValue)) continue;
            JsonMarshaller marshaller = context.marshallerRegistry().getMarshaller(MarshallLocation.HEADER, listValue);
            marshaller.marshall(listValue, context, paramName, memberFieldInfo);
        }
    };
    public static final JsonMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        if (Objects.nonNull(sdkField) && sdkField.containsTrait(RequiredTrait.class)) {
            throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
        }
    };

    private HeaderMarshaller() {
    }

    private static boolean shouldSkipElement(Object element) {
        return element instanceof String && StringUtils.isBlank((String)element);
    }

    private static class SimpleHeaderMarshaller<T>
    implements JsonMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;

        private SimpleHeaderMarshaller(ValueToStringConverter.ValueToString<T> converter) {
            this.converter = converter;
        }

        @Override
        public void marshall(T val, JsonMarshallerContext context, String paramName, SdkField<T> sdkField) {
            context.request().appendHeader(paramName, this.converter.convert(val, sdkField));
        }
    }
}

