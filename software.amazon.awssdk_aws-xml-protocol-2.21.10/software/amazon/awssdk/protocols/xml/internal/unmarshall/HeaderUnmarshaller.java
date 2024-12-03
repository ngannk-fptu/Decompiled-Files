/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.protocols.core.StringToValueConverter
 *  software.amazon.awssdk.protocols.core.StringToValueConverter$StringToValue
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlProtocolUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshallerContext;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class HeaderUnmarshaller {
    public static final XmlUnmarshaller<String> STRING = new SimpleHeaderUnmarshaller<String>((StringToValueConverter.StringToValue)StringToValueConverter.TO_STRING);
    public static final XmlUnmarshaller<Integer> INTEGER = new SimpleHeaderUnmarshaller<Integer>((StringToValueConverter.StringToValue)StringToValueConverter.TO_INTEGER);
    public static final XmlUnmarshaller<Long> LONG = new SimpleHeaderUnmarshaller<Long>((StringToValueConverter.StringToValue)StringToValueConverter.TO_LONG);
    public static final XmlUnmarshaller<Short> SHORT = new SimpleHeaderUnmarshaller<Short>((StringToValueConverter.StringToValue)StringToValueConverter.TO_SHORT);
    public static final XmlUnmarshaller<Float> FLOAT = new SimpleHeaderUnmarshaller<Float>((StringToValueConverter.StringToValue)StringToValueConverter.TO_FLOAT);
    public static final XmlUnmarshaller<Double> DOUBLE = new SimpleHeaderUnmarshaller<Double>((StringToValueConverter.StringToValue)StringToValueConverter.TO_DOUBLE);
    public static final XmlUnmarshaller<Boolean> BOOLEAN = new SimpleHeaderUnmarshaller<Boolean>((StringToValueConverter.StringToValue)StringToValueConverter.TO_BOOLEAN);
    public static final XmlUnmarshaller<Instant> INSTANT = new SimpleHeaderUnmarshaller<Instant>(XmlProtocolUnmarshaller.INSTANT_STRING_TO_VALUE);
    public static final XmlUnmarshaller<Map<String, ?>> MAP = (context, content, field) -> {
        HashMap result = new HashMap();
        context.response().forEachHeader((name, value) -> {
            if (StringUtils.startsWithIgnoreCase((String)name, (String)field.locationName())) {
                result.put(StringUtils.replacePrefixIgnoreCase((String)name, (String)field.locationName(), (String)""), String.join((CharSequence)",", value));
            }
        });
        return result;
    };
    public static final XmlUnmarshaller<List<?>> LIST = (context, content, field) -> context.response().matchingHeaders(field.locationName());

    private HeaderUnmarshaller() {
    }

    private static class SimpleHeaderUnmarshaller<T>
    implements XmlUnmarshaller<T> {
        private final StringToValueConverter.StringToValue<T> stringToValue;

        private SimpleHeaderUnmarshaller(StringToValueConverter.StringToValue<T> stringToValue) {
            this.stringToValue = stringToValue;
        }

        @Override
        public T unmarshall(XmlUnmarshallerContext context, List<XmlElement> content, SdkField<T> field) {
            return context.response().firstMatchingHeader(field.locationName()).map(s -> this.stringToValue.convert(s, field)).orElse(null);
        }
    }
}

