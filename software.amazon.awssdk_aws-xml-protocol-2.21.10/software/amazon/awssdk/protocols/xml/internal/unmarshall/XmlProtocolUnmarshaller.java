/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.PayloadTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.core.traits.XmlAttributeTrait
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.protocols.core.StringToInstant
 *  software.amazon.awssdk.protocols.core.StringToValueConverter$StringToValue
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlDomParser
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.builder.Buildable
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.core.traits.XmlAttributeTrait;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.core.StringToInstant;
import software.amazon.awssdk.protocols.core.StringToValueConverter;
import software.amazon.awssdk.protocols.query.unmarshall.XmlDomParser;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.HeaderUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlPayloadUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlResponseParserUtils;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshallerContext;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlUnmarshallerRegistry;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.builder.Buildable;

@SdkInternalApi
public final class XmlProtocolUnmarshaller
implements XmlErrorUnmarshaller {
    public static final StringToValueConverter.StringToValue<Instant> INSTANT_STRING_TO_VALUE = StringToInstant.create(XmlProtocolUnmarshaller.getDefaultTimestampFormats());
    private static final XmlUnmarshallerRegistry REGISTRY = XmlProtocolUnmarshaller.createUnmarshallerRegistry();

    private XmlProtocolUnmarshaller() {
    }

    public static XmlProtocolUnmarshaller create() {
        return new XmlProtocolUnmarshaller();
    }

    public <TypeT extends SdkPojo> TypeT unmarshall(SdkPojo sdkPojo, SdkHttpFullResponse response) {
        XmlElement document = this.hasXmlPayload(sdkPojo, response) ? XmlResponseParserUtils.parse(sdkPojo, response) : null;
        return this.unmarshall(sdkPojo, document, response);
    }

    public <TypeT extends SdkPojo> TypeT unmarshall(SdkPojo sdkPojo, XmlElement resultRoot, SdkHttpFullResponse response) {
        XmlUnmarshallerContext unmarshallerContext = XmlUnmarshallerContext.builder().response(response).registry(REGISTRY).protocolUnmarshaller(this).build();
        return (TypeT)this.unmarshall(unmarshallerContext, sdkPojo, resultRoot);
    }

    SdkPojo unmarshall(XmlUnmarshallerContext context, SdkPojo sdkPojo, XmlElement root) {
        for (SdkField field : sdkPojo.sdkFields()) {
            Object unmarshalled;
            XmlUnmarshaller<Object> unmarshaller = REGISTRY.getUnmarshaller(field.location(), field.marshallingType());
            if (field.location() != MarshallLocation.PAYLOAD) {
                Object unmarshalled2 = unmarshaller.unmarshall(context, null, (SdkField<Object>)field);
                field.set((Object)sdkPojo, unmarshalled2);
                continue;
            }
            if (this.isExplicitPayloadMember(field)) {
                InputStream content = context.response().content().orElse(null);
                if (field.marshallingType() == MarshallingType.SDK_BYTES) {
                    SdkBytes value = content == null ? SdkBytes.fromByteArrayUnsafe((byte[])new byte[0]) : SdkBytes.fromInputStream((InputStream)content);
                    field.set((Object)sdkPojo, (Object)value);
                    continue;
                }
                if (field.marshallingType() == MarshallingType.STRING) {
                    if (content == null) {
                        field.set((Object)sdkPojo, (Object)"");
                        continue;
                    }
                    this.setExplicitStringPayload(unmarshaller, context, sdkPojo, root, field);
                    continue;
                }
                if (root != null && !this.isAttribute(field)) {
                    unmarshalled = unmarshaller.unmarshall(context, Collections.singletonList(root), (SdkField<Object>)field);
                    field.set((Object)sdkPojo, unmarshalled);
                    continue;
                }
            }
            if (root == null) continue;
            if (this.isAttribute(field)) {
                root.getOptionalAttributeByName(field.unmarshallLocationName()).ifPresent(e -> field.set((Object)sdkPojo, e));
                continue;
            }
            List element = root.getElementsByName(field.unmarshallLocationName());
            if (CollectionUtils.isNullOrEmpty((Collection)element)) continue;
            unmarshalled = unmarshaller.unmarshall(context, element, (SdkField<Object>)field);
            field.set((Object)sdkPojo, unmarshalled);
        }
        if (!(sdkPojo instanceof Buildable)) {
            throw new RuntimeException("The sdkPojo passed to the unmarshaller is not buildable (must implement Buildable)");
        }
        return (SdkPojo)((Buildable)sdkPojo).build();
    }

    private void setExplicitStringPayload(XmlUnmarshaller<Object> unmarshaller, XmlUnmarshallerContext context, SdkPojo sdkPojo, XmlElement element, SdkField<?> field) {
        SdkBytes sdkBytes = SdkBytes.fromInputStream((InputStream)((InputStream)context.response().content().get()));
        String stringPayload = sdkBytes.asUtf8String();
        if (this.hasS3XmlEnvelopePrefix(stringPayload)) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(sdkBytes.asByteArray());
            XmlElement document = XmlDomParser.parse((InputStream)inputStream);
            Object unmarshalled = unmarshaller.unmarshall(context, Collections.singletonList(document), field);
            field.set((Object)sdkPojo, unmarshalled);
        } else if (stringPayload.isEmpty()) {
            if (element == null) {
                field.set((Object)sdkPojo, (Object)"");
            } else {
                Object unmarshalled = unmarshaller.unmarshall(context, Collections.singletonList(element), field);
                field.set((Object)sdkPojo, unmarshalled);
            }
        } else {
            field.set((Object)sdkPojo, (Object)stringPayload);
        }
    }

    private boolean hasS3XmlEnvelopePrefix(String payload) {
        String s3XmlEnvelopePrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Policy><![CDATA[";
        return payload.startsWith(s3XmlEnvelopePrefix);
    }

    private boolean isAttribute(SdkField<?> field) {
        return field.containsTrait(XmlAttributeTrait.class);
    }

    private boolean isExplicitPayloadMember(SdkField<?> field) {
        return field.containsTrait(PayloadTrait.class);
    }

    private boolean hasXmlPayload(SdkPojo sdkPojo, SdkHttpFullResponse response) {
        return sdkPojo.sdkFields().stream().anyMatch(f -> this.isPayloadMemberOnUnmarshall((SdkField<?>)f) && !this.isExplicitBlobPayloadMember((SdkField<?>)f) && !this.isExplicitStringPayloadMember((SdkField<?>)f)) && response.content().isPresent();
    }

    private boolean isExplicitBlobPayloadMember(SdkField<?> f) {
        return this.isExplicitPayloadMember(f) && f.marshallingType() == MarshallingType.SDK_BYTES;
    }

    private boolean isExplicitStringPayloadMember(SdkField<?> f) {
        return this.isExplicitPayloadMember(f) && f.marshallingType() == MarshallingType.STRING;
    }

    private boolean isPayloadMemberOnUnmarshall(SdkField<?> f) {
        return f.location() == MarshallLocation.PAYLOAD || XmlProtocolUnmarshaller.isInUri(f.location());
    }

    private static boolean isInUri(MarshallLocation location) {
        switch (location) {
            case PATH: 
            case QUERY_PARAM: {
                return true;
            }
        }
        return false;
    }

    private static Map<MarshallLocation, TimestampFormatTrait.Format> getDefaultTimestampFormats() {
        EnumMap<MarshallLocation, TimestampFormatTrait.Format> formats = new EnumMap<MarshallLocation, TimestampFormatTrait.Format>(MarshallLocation.class);
        formats.put(MarshallLocation.HEADER, TimestampFormatTrait.Format.RFC_822);
        formats.put(MarshallLocation.PAYLOAD, TimestampFormatTrait.Format.ISO_8601);
        return Collections.unmodifiableMap(formats);
    }

    private static XmlUnmarshallerRegistry createUnmarshallerRegistry() {
        return XmlUnmarshallerRegistry.builder().statusCodeUnmarshaller(MarshallingType.INTEGER, (context, content, field) -> context.response().statusCode()).headerUnmarshaller(MarshallingType.STRING, HeaderUnmarshaller.STRING).headerUnmarshaller(MarshallingType.INTEGER, HeaderUnmarshaller.INTEGER).headerUnmarshaller(MarshallingType.LONG, HeaderUnmarshaller.LONG).headerUnmarshaller(MarshallingType.SHORT, HeaderUnmarshaller.SHORT).headerUnmarshaller(MarshallingType.DOUBLE, HeaderUnmarshaller.DOUBLE).headerUnmarshaller(MarshallingType.BOOLEAN, HeaderUnmarshaller.BOOLEAN).headerUnmarshaller(MarshallingType.INSTANT, HeaderUnmarshaller.INSTANT).headerUnmarshaller(MarshallingType.FLOAT, HeaderUnmarshaller.FLOAT).headerUnmarshaller(MarshallingType.MAP, HeaderUnmarshaller.MAP).headerUnmarshaller(MarshallingType.LIST, HeaderUnmarshaller.LIST).payloadUnmarshaller(MarshallingType.STRING, XmlPayloadUnmarshaller.STRING).payloadUnmarshaller(MarshallingType.INTEGER, XmlPayloadUnmarshaller.INTEGER).payloadUnmarshaller(MarshallingType.LONG, XmlPayloadUnmarshaller.LONG).payloadUnmarshaller(MarshallingType.SHORT, XmlPayloadUnmarshaller.SHORT).payloadUnmarshaller(MarshallingType.FLOAT, XmlPayloadUnmarshaller.FLOAT).payloadUnmarshaller(MarshallingType.DOUBLE, XmlPayloadUnmarshaller.DOUBLE).payloadUnmarshaller(MarshallingType.BIG_DECIMAL, XmlPayloadUnmarshaller.BIG_DECIMAL).payloadUnmarshaller(MarshallingType.BOOLEAN, XmlPayloadUnmarshaller.BOOLEAN).payloadUnmarshaller(MarshallingType.INSTANT, XmlPayloadUnmarshaller.INSTANT).payloadUnmarshaller(MarshallingType.SDK_BYTES, XmlPayloadUnmarshaller.SDK_BYTES).payloadUnmarshaller(MarshallingType.SDK_POJO, XmlPayloadUnmarshaller::unmarshallSdkPojo).payloadUnmarshaller(MarshallingType.LIST, XmlPayloadUnmarshaller::unmarshallList).payloadUnmarshaller(MarshallingType.MAP, XmlPayloadUnmarshaller::unmarshallMap).build();
    }
}

