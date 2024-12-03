/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter$ValueToString
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerContext;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlProtocolMarshaller;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class HeaderMarshaller {
    public static final XmlMarshaller<String> STRING = new SimpleHeaderMarshaller<String>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_STRING);
    public static final XmlMarshaller<Integer> INTEGER = new SimpleHeaderMarshaller<Integer>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_INTEGER);
    public static final XmlMarshaller<Long> LONG = new SimpleHeaderMarshaller<Long>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_LONG);
    public static final XmlMarshaller<Short> SHORT = new SimpleHeaderMarshaller<Short>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_SHORT);
    public static final XmlMarshaller<Double> DOUBLE = new SimpleHeaderMarshaller<Double>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_DOUBLE);
    public static final XmlMarshaller<Float> FLOAT = new SimpleHeaderMarshaller<Float>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_FLOAT);
    public static final XmlMarshaller<Boolean> BOOLEAN = new SimpleHeaderMarshaller<Boolean>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_BOOLEAN);
    public static final XmlMarshaller<Instant> INSTANT = new SimpleHeaderMarshaller<Instant>(XmlProtocolMarshaller.INSTANT_VALUE_TO_STRING);
    public static final XmlMarshaller<Map<String, ?>> MAP = new SimpleHeaderMarshaller<Map<String, ?>>(null){

        @Override
        public void marshall(Map<String, ?> map, XmlMarshallerContext context, String paramName, SdkField<Map<String, ?>> sdkField) {
            if (!this.shouldEmit(map)) {
                return;
            }
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String key = entry.getKey().startsWith(paramName) ? entry.getKey() : paramName + entry.getKey();
                XmlMarshaller<?> marshaller = context.marshallerRegistry().getMarshaller(MarshallLocation.HEADER, entry.getValue());
                marshaller.marshall(entry.getValue(), context, key, null);
            }
        }

        @Override
        protected boolean shouldEmit(Map map) {
            return !CollectionUtils.isNullOrEmpty((Map)map);
        }
    };
    public static final XmlMarshaller<List<?>> LIST = new SimpleHeaderMarshaller<List<?>>(null){

        @Override
        public void marshall(List<?> list, XmlMarshallerContext context, String paramName, SdkField<List<?>> sdkField) {
            if (!this.shouldEmit(list)) {
                return;
            }
            SdkField memberFieldInfo = ((ListTrait)sdkField.getRequiredTrait(ListTrait.class)).memberFieldInfo();
            for (Object listValue : list) {
                if (this.shouldSkipElement(listValue)) continue;
                XmlMarshaller<?> marshaller = context.marshallerRegistry().getMarshaller(MarshallLocation.HEADER, listValue);
                marshaller.marshall(listValue, context, paramName, memberFieldInfo);
            }
        }

        private boolean shouldSkipElement(Object element) {
            return element instanceof String && StringUtils.isBlank((CharSequence)((String)element));
        }

        @Override
        protected boolean shouldEmit(List list) {
            return !CollectionUtils.isNullOrEmpty((Collection)list);
        }
    };
    public static final XmlMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        if (Objects.nonNull(sdkField) && sdkField.containsTrait(RequiredTrait.class)) {
            throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
        }
    };

    private HeaderMarshaller() {
    }

    private static class SimpleHeaderMarshaller<T>
    implements XmlMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;

        private SimpleHeaderMarshaller(ValueToStringConverter.ValueToString<T> converter) {
            this.converter = converter;
        }

        @Override
        public void marshall(T val, XmlMarshallerContext context, String paramName, SdkField<T> sdkField) {
            if (!this.shouldEmit(val)) {
                return;
            }
            context.request().appendHeader(paramName, this.converter.convert(val, sdkField));
        }

        protected boolean shouldEmit(T val) {
            return val != null;
        }
    }
}

