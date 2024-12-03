/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.MapTrait
 *  software.amazon.awssdk.protocols.core.StringToValueConverter
 *  software.amazon.awssdk.protocols.core.StringToValueConverter$StringToValue
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlProtocolUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshallerContext;

@SdkInternalApi
public final class XmlPayloadUnmarshaller {
    public static final XmlUnmarshaller<String> STRING = new SimpleTypePayloadUnmarshaller<String>((StringToValueConverter.StringToValue)StringToValueConverter.TO_STRING);
    public static final XmlUnmarshaller<Integer> INTEGER = new SimpleTypePayloadUnmarshaller<Integer>((StringToValueConverter.StringToValue)StringToValueConverter.TO_INTEGER);
    public static final XmlUnmarshaller<Long> LONG = new SimpleTypePayloadUnmarshaller<Long>((StringToValueConverter.StringToValue)StringToValueConverter.TO_LONG);
    public static final XmlUnmarshaller<Short> SHORT = new SimpleTypePayloadUnmarshaller<Short>((StringToValueConverter.StringToValue)StringToValueConverter.TO_SHORT);
    public static final XmlUnmarshaller<Float> FLOAT = new SimpleTypePayloadUnmarshaller<Float>((StringToValueConverter.StringToValue)StringToValueConverter.TO_FLOAT);
    public static final XmlUnmarshaller<Double> DOUBLE = new SimpleTypePayloadUnmarshaller<Double>((StringToValueConverter.StringToValue)StringToValueConverter.TO_DOUBLE);
    public static final XmlUnmarshaller<BigDecimal> BIG_DECIMAL = new SimpleTypePayloadUnmarshaller<BigDecimal>((StringToValueConverter.StringToValue)StringToValueConverter.TO_BIG_DECIMAL);
    public static final XmlUnmarshaller<Boolean> BOOLEAN = new SimpleTypePayloadUnmarshaller<Boolean>((StringToValueConverter.StringToValue)StringToValueConverter.TO_BOOLEAN);
    public static final XmlUnmarshaller<Instant> INSTANT = new SimpleTypePayloadUnmarshaller<Instant>(XmlProtocolUnmarshaller.INSTANT_STRING_TO_VALUE);
    public static final XmlUnmarshaller<SdkBytes> SDK_BYTES = new SimpleTypePayloadUnmarshaller<SdkBytes>((StringToValueConverter.StringToValue)StringToValueConverter.TO_SDK_BYTES);

    private XmlPayloadUnmarshaller() {
    }

    public static SdkPojo unmarshallSdkPojo(XmlUnmarshallerContext context, List<XmlElement> content, SdkField<SdkPojo> field) {
        return context.protocolUnmarshaller().unmarshall(context, (SdkPojo)field.constructor().get(), content.get(0));
    }

    public static List<?> unmarshallList(XmlUnmarshallerContext context, List<XmlElement> content, SdkField<List<?>> field) {
        ListTrait listTrait = (ListTrait)field.getTrait(ListTrait.class);
        ArrayList list = new ArrayList();
        XmlPayloadUnmarshaller.getMembers(content, listTrait).forEach(member -> {
            XmlUnmarshaller<Object> unmarshaller = context.getUnmarshaller(listTrait.memberFieldInfo().location(), listTrait.memberFieldInfo().marshallingType());
            list.add(unmarshaller.unmarshall(context, Collections.singletonList(member), (SdkField<Object>)listTrait.memberFieldInfo()));
        });
        return list;
    }

    private static List<XmlElement> getMembers(List<XmlElement> content, ListTrait listTrait) {
        String memberLocation = listTrait.memberLocationName() != null ? listTrait.memberLocationName() : listTrait.memberFieldInfo().locationName();
        return listTrait.isFlattened() ? content : content.get(0).getElementsByName(memberLocation);
    }

    public static Map<String, ?> unmarshallMap(XmlUnmarshallerContext context, List<XmlElement> content, SdkField<Map<String, ?>> field) {
        HashMap map = new HashMap();
        MapTrait mapTrait = (MapTrait)field.getTrait(MapTrait.class);
        SdkField mapValueSdkField = mapTrait.valueFieldInfo();
        XmlPayloadUnmarshaller.getEntries(content, mapTrait).forEach(entry -> {
            XmlElement key = entry.getElementByName(mapTrait.keyLocationName());
            XmlElement value = entry.getElementByName(mapTrait.valueLocationName());
            XmlUnmarshaller<Object> unmarshaller = context.getUnmarshaller(mapValueSdkField.location(), mapValueSdkField.marshallingType());
            map.put(key.textContent(), unmarshaller.unmarshall(context, Collections.singletonList(value), (SdkField<Object>)mapValueSdkField));
        });
        return map;
    }

    private static List<XmlElement> getEntries(List<XmlElement> content, MapTrait mapTrait) {
        return mapTrait.isFlattened() ? content : content.get(0).getElementsByName("entry");
    }

    private static class SimpleTypePayloadUnmarshaller<T>
    implements XmlUnmarshaller<T> {
        private final StringToValueConverter.StringToValue<T> converter;

        private SimpleTypePayloadUnmarshaller(StringToValueConverter.StringToValue<T> converter) {
            this.converter = converter;
        }

        @Override
        public T unmarshall(XmlUnmarshallerContext context, List<XmlElement> content, SdkField<T> field) {
            if (content == null) {
                return null;
            }
            return (T)this.converter.convert(content.get(0).textContent(), field);
        }
    }
}

