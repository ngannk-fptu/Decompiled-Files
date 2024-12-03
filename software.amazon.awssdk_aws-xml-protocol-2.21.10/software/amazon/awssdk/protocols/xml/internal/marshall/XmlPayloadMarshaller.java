/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.MapTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.XmlAttributeTrait
 *  software.amazon.awssdk.core.traits.XmlAttributesTrait
 *  software.amazon.awssdk.core.traits.XmlAttributesTrait$AttributeAccessors
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructMap
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter$ValueToString
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.MapTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.XmlAttributeTrait;
import software.amazon.awssdk.core.traits.XmlAttributesTrait;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerContext;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlProtocolMarshaller;

@SdkInternalApi
public class XmlPayloadMarshaller {
    public static final XmlMarshaller<String> STRING = new BasePayloadMarshaller<String>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_STRING);
    public static final XmlMarshaller<Integer> INTEGER = new BasePayloadMarshaller<Integer>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_INTEGER);
    public static final XmlMarshaller<Long> LONG = new BasePayloadMarshaller<Long>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_LONG);
    public static final XmlMarshaller<Short> SHORT = new BasePayloadMarshaller<Short>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_SHORT);
    public static final XmlMarshaller<Float> FLOAT = new BasePayloadMarshaller<Float>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_FLOAT);
    public static final XmlMarshaller<Double> DOUBLE = new BasePayloadMarshaller<Double>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_DOUBLE);
    public static final XmlMarshaller<BigDecimal> BIG_DECIMAL = new BasePayloadMarshaller<BigDecimal>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_BIG_DECIMAL);
    public static final XmlMarshaller<Boolean> BOOLEAN = new BasePayloadMarshaller<Boolean>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_BOOLEAN);
    public static final XmlMarshaller<Instant> INSTANT = new BasePayloadMarshaller<Instant>(XmlProtocolMarshaller.INSTANT_VALUE_TO_STRING);
    public static final XmlMarshaller<SdkBytes> SDK_BYTES = new BasePayloadMarshaller<SdkBytes>((ValueToStringConverter.ValueToString)ValueToStringConverter.FROM_SDK_BYTES);
    public static final XmlMarshaller<SdkPojo> SDK_POJO = new BasePayloadMarshaller<SdkPojo>(null){

        @Override
        public void marshall(SdkPojo val, XmlMarshallerContext context, String paramName, SdkField<SdkPojo> sdkField, ValueToStringConverter.ValueToString<SdkPojo> converter) {
            context.protocolMarshaller().doMarshall(val);
        }
    };
    public static final XmlMarshaller<List<?>> LIST = new BasePayloadMarshaller<List<?>>(null){

        @Override
        public void marshall(List<?> val, XmlMarshallerContext context, String paramName, SdkField<List<?>> sdkField) {
            if (!this.shouldEmit(val, paramName)) {
                return;
            }
            this.marshall(val, context, paramName, sdkField, (ValueToStringConverter.ValueToString<List<?>>)null);
        }

        @Override
        public void marshall(List<?> list, XmlMarshallerContext context, String paramName, SdkField<List<?>> sdkField, ValueToStringConverter.ValueToString<List<?>> converter) {
            ListTrait listTrait = (ListTrait)sdkField.getRequiredTrait(ListTrait.class);
            if (!listTrait.isFlattened()) {
                context.xmlGenerator().startElement(paramName);
            }
            SdkField memberField = listTrait.memberFieldInfo();
            String memberLocationName = this.listMemberLocationName(listTrait, paramName);
            for (Object listMember : list) {
                context.marshall(MarshallLocation.PAYLOAD, listMember, memberLocationName, memberField);
            }
            if (!listTrait.isFlattened()) {
                context.xmlGenerator().endElement();
            }
        }

        private String listMemberLocationName(ListTrait listTrait, String listLocationName) {
            String locationName = listTrait.memberLocationName();
            if (locationName == null) {
                locationName = listTrait.isFlattened() ? listLocationName : "member";
            }
            return locationName;
        }

        @Override
        protected boolean shouldEmit(List list, String paramName) {
            return super.shouldEmit(list, paramName) && (!list.isEmpty() || !(list instanceof SdkAutoConstructList));
        }
    };
    public static final XmlMarshaller<Map<String, ?>> MAP = new BasePayloadMarshaller<Map<String, ?>>(null){

        @Override
        public void marshall(Map<String, ?> map, XmlMarshallerContext context, String paramName, SdkField<Map<String, ?>> sdkField, ValueToStringConverter.ValueToString<Map<String, ?>> converter) {
            MapTrait mapTrait = (MapTrait)sdkField.getRequiredTrait(MapTrait.class);
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                context.xmlGenerator().startElement("entry");
                context.marshall(MarshallLocation.PAYLOAD, entry.getKey(), mapTrait.keyLocationName(), null);
                context.marshall(MarshallLocation.PAYLOAD, entry.getValue(), mapTrait.valueLocationName(), mapTrait.valueFieldInfo());
                context.xmlGenerator().endElement();
            }
        }

        @Override
        protected boolean shouldEmit(Map map, String paramName) {
            return super.shouldEmit(map, paramName) && (!map.isEmpty() || !(map instanceof SdkAutoConstructMap));
        }
    };
    public static final XmlMarshaller<Void> NULL = (val, context, paramName, sdkField) -> {
        if (Objects.nonNull(sdkField) && sdkField.containsTrait(RequiredTrait.class)) {
            throw new IllegalArgumentException(String.format("Parameter '%s' must not be null", paramName));
        }
    };

    private XmlPayloadMarshaller() {
    }

    private static class BasePayloadMarshaller<T>
    implements XmlMarshaller<T> {
        private final ValueToStringConverter.ValueToString<T> converter;

        private BasePayloadMarshaller(ValueToStringConverter.ValueToString<T> converter) {
            this.converter = converter;
        }

        @Override
        public void marshall(T val, XmlMarshallerContext context, String paramName, SdkField<T> sdkField) {
            if (!this.shouldEmit(val, paramName)) {
                return;
            }
            if (this.isXmlAttribute(sdkField)) {
                return;
            }
            if (sdkField != null && sdkField.getOptionalTrait(XmlAttributesTrait.class).isPresent()) {
                XmlAttributesTrait attributeTrait = (XmlAttributesTrait)sdkField.getTrait(XmlAttributesTrait.class);
                Map attributes = attributeTrait.attributes().entrySet().stream().collect(LinkedHashMap::new, (m, e) -> {
                    String cfr_ignored_0 = (String)m.put(e.getKey(), ((XmlAttributesTrait.AttributeAccessors)e.getValue()).attributeGetter().apply(val));
                }, HashMap::putAll);
                context.xmlGenerator().startElement(paramName, attributes);
            } else {
                context.xmlGenerator().startElement(paramName);
            }
            this.marshall(val, context, paramName, sdkField, this.converter);
            context.xmlGenerator().endElement();
        }

        void marshall(T val, XmlMarshallerContext context, String paramName, SdkField<T> sdkField, ValueToStringConverter.ValueToString<T> converter) {
            context.xmlGenerator().xmlWriter().value(converter.convert(val, sdkField));
        }

        protected boolean shouldEmit(T val, String paramName) {
            return val != null && paramName != null;
        }

        private boolean isXmlAttribute(SdkField<T> sdkField) {
            return sdkField != null && sdkField.getOptionalTrait(XmlAttributeTrait.class).isPresent();
        }
    }
}

