/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.MapTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter$ValueToString
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerContext;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlProtocolMarshaller;

@SdkInternalApi
public final class QueryParamMarshaller {
    public static final XmlMarshaller<String> STRING = new SimpleQueryParamMarshaller<String>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_STRING);
    public static final XmlMarshaller<Integer> INTEGER = new SimpleQueryParamMarshaller<Integer>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_INTEGER);
    public static final XmlMarshaller<Long> LONG = new SimpleQueryParamMarshaller<Long>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_LONG);
    public static final XmlMarshaller<Short> SHORT = new SimpleQueryParamMarshaller<Short>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_SHORT);
    public static final XmlMarshaller<Double> DOUBLE = new SimpleQueryParamMarshaller<Double>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_DOUBLE);
    public static final XmlMarshaller<Float> FLOAT = new SimpleQueryParamMarshaller<Float>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_FLOAT);
    public static final XmlMarshaller<Boolean> BOOLEAN = new SimpleQueryParamMarshaller<Boolean>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_BOOLEAN);
    public static final XmlMarshaller<Instant> INSTANT = new SimpleQueryParamMarshaller<Instant>(XmlProtocolMarshaller.INSTANT_VALUE_TO_STRING);
    public static final XmlMarshaller<List<?>> LIST = (list, context, paramName, sdkField) -> {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Object member : list) {
            context.marshall(MarshallLocation.QUERY_PARAM, member, paramName, null);
        }
    };
    public static final XmlMarshaller<Map<String, ?>> MAP = (map, context, paramName, sdkField) -> {
        if (map == null || map.isEmpty()) {
            return;
        }
        MapTrait mapTrait = (MapTrait)sdkField.getRequiredTrait(MapTrait.class);
        SdkField valueField = mapTrait.valueFieldInfo();
        for (Map.Entry entry : map.entrySet()) {
            if (valueField.containsTrait(ListTrait.class)) {
                ((List)entry.getValue()).forEach(val -> context.marshallerRegistry().getMarshaller(MarshallLocation.QUERY_PARAM, val).marshall(val, context, (String)entry.getKey(), null));
                continue;
            }
            SimpleQueryParamMarshaller valueMarshaller = (SimpleQueryParamMarshaller)context.marshallerRegistry().getMarshaller(MarshallLocation.QUERY_PARAM, entry.getValue());
            context.request().putRawQueryParameter((String)entry.getKey(), valueMarshaller.convert(entry.getValue(), null));
        }
    };
    public static final XmlMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        if (Objects.nonNull(sdkField) && sdkField.containsTrait(RequiredTrait.class)) {
            throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
        }
    };

    private QueryParamMarshaller() {
    }

    private static class SimpleQueryParamMarshaller<T>
    implements XmlMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;

        private SimpleQueryParamMarshaller(ValueToStringConverter.ValueToString<T> converter) {
            this.converter = converter;
        }

        @Override
        public void marshall(T val, XmlMarshallerContext context, String paramName, SdkField<T> sdkField) {
            context.request().appendRawQueryParameter(paramName, this.converter.convert(val, sdkField));
        }

        public String convert(T val, SdkField<T> sdkField) {
            return this.converter.convert(val, sdkField);
        }
    }
}

