/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.traits.JsonValueTrait
 *  software.amazon.awssdk.protocols.core.StringToValueConverter
 *  software.amazon.awssdk.protocols.core.StringToValueConverter$StringToValue
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 *  software.amazon.awssdk.utils.BinaryUtils
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.traits.JsonValueTrait;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshaller;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshallerContext;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.utils.BinaryUtils;

@SdkInternalApi
final class HeaderUnmarshaller {
    public static final JsonUnmarshaller<String> STRING = new SimpleHeaderUnmarshaller<String>(HeaderUnmarshaller::unmarshallStringHeader);
    public static final JsonUnmarshaller<Integer> INTEGER = new SimpleHeaderUnmarshaller<Integer>((StringToValueConverter.StringToValue)StringToValueConverter.TO_INTEGER);
    public static final JsonUnmarshaller<Long> LONG = new SimpleHeaderUnmarshaller<Long>((StringToValueConverter.StringToValue)StringToValueConverter.TO_LONG);
    public static final JsonUnmarshaller<Short> SHORT = new SimpleHeaderUnmarshaller<Short>((StringToValueConverter.StringToValue)StringToValueConverter.TO_SHORT);
    public static final JsonUnmarshaller<Double> DOUBLE = new SimpleHeaderUnmarshaller<Double>((StringToValueConverter.StringToValue)StringToValueConverter.TO_DOUBLE);
    public static final JsonUnmarshaller<Boolean> BOOLEAN = new SimpleHeaderUnmarshaller<Boolean>((StringToValueConverter.StringToValue)StringToValueConverter.TO_BOOLEAN);
    public static final JsonUnmarshaller<Float> FLOAT = new SimpleHeaderUnmarshaller<Float>((StringToValueConverter.StringToValue)StringToValueConverter.TO_FLOAT);
    public static final JsonUnmarshaller<List<?>> LIST = (context, jsonContent, field) -> context.response().matchingHeaders(field.locationName());

    private HeaderUnmarshaller() {
    }

    private static String unmarshallStringHeader(String value, SdkField<String> field) {
        return field.containsTrait(JsonValueTrait.class) ? new String(BinaryUtils.fromBase64((String)value), StandardCharsets.UTF_8) : value;
    }

    public static JsonUnmarshaller<Instant> createInstantHeaderUnmarshaller(StringToValueConverter.StringToValue<Instant> instantStringToValue) {
        return new SimpleHeaderUnmarshaller<Instant>(instantStringToValue);
    }

    private static class SimpleHeaderUnmarshaller<T>
    implements JsonUnmarshaller<T> {
        private final StringToValueConverter.StringToValue<T> stringToValue;

        private SimpleHeaderUnmarshaller(StringToValueConverter.StringToValue<T> stringToValue) {
            this.stringToValue = stringToValue;
        }

        @Override
        public T unmarshall(JsonUnmarshallerContext context, JsonNode jsonContent, SdkField<T> field) {
            return context.response().firstMatchingHeader(field.locationName()).map(s -> this.stringToValue.convert(s, field)).orElse(null);
        }
    }
}

