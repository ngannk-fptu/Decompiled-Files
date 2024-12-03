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
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.protocols.core.InstantToString
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.ProtocolMarshaller
 *  software.amazon.awssdk.protocols.core.ProtocolUtils
 *  software.amazon.awssdk.protocols.core.ValueToStringConverter$ValueToString
 *  software.amazon.awssdk.utils.StringInputStream
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.core.InstantToString;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.core.ProtocolUtils;
import software.amazon.awssdk.protocols.core.ValueToStringConverter;
import software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory;
import software.amazon.awssdk.protocols.xml.internal.marshall.HeaderMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.QueryParamMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.SimpleTypePathMarshaller;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlGenerator;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerContext;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlMarshallerRegistry;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlPayloadMarshaller;
import software.amazon.awssdk.utils.StringInputStream;

@SdkInternalApi
public final class XmlProtocolMarshaller
implements ProtocolMarshaller<SdkHttpFullRequest> {
    public static final ValueToStringConverter.ValueToString<Instant> INSTANT_VALUE_TO_STRING = InstantToString.create(XmlProtocolMarshaller.getDefaultTimestampFormats());
    private static final XmlMarshallerRegistry MARSHALLER_REGISTRY = XmlProtocolMarshaller.createMarshallerRegistry();
    private final URI endpoint;
    private final SdkHttpFullRequest.Builder request;
    private final String rootElement;
    private final XmlMarshallerContext marshallerContext;

    private XmlProtocolMarshaller(Builder builder) {
        this.endpoint = builder.endpoint;
        this.request = ProtocolUtils.createSdkHttpRequest((OperationInfo)builder.operationInfo, (URI)this.endpoint);
        this.rootElement = (String)builder.operationInfo.addtionalMetadata(AwsXmlProtocolFactory.ROOT_MARSHALL_LOCATION_ATTRIBUTE);
        this.marshallerContext = XmlMarshallerContext.builder().xmlGenerator(builder.xmlGenerator).marshallerRegistry(MARSHALLER_REGISTRY).protocolMarshaller(this).request(this.request).build();
    }

    public SdkHttpFullRequest marshall(SdkPojo pojo) {
        if (this.rootElement != null) {
            this.marshallerContext.xmlGenerator().startElement(this.rootElement);
        }
        this.doMarshall(pojo);
        if (this.rootElement != null) {
            this.marshallerContext.xmlGenerator().endElement();
        }
        return this.finishMarshalling(pojo);
    }

    void doMarshall(SdkPojo pojo) {
        for (SdkField field : pojo.sdkFields()) {
            Object val;
            if (this.isBinary(field, val = field.getValueOrDefault((Object)pojo))) {
                this.request.contentStreamProvider(() -> ((SdkBytes)((SdkBytes)val)).asInputStream());
                this.setContentTypeHeaderIfNeeded("binary/octet-stream");
                continue;
            }
            if (this.isExplicitPayloadMember(field) && val instanceof String) {
                byte[] content = ((String)val).getBytes(StandardCharsets.UTF_8);
                this.request.contentStreamProvider(() -> new ByteArrayInputStream(content));
                this.request.putHeader("Content-Length", Integer.toString(content.length));
                continue;
            }
            MARSHALLER_REGISTRY.getMarshaller(field.location(), field.marshallingType(), val).marshall(val, this.marshallerContext, field.locationName(), (SdkField<Object>)field);
        }
    }

    private SdkHttpFullRequest finishMarshalling(SdkPojo pojo) {
        String content;
        if (this.hasPayloadMembers(pojo) && this.request.contentStreamProvider() == null && this.marshallerContext.xmlGenerator() != null && !(content = this.marshallerContext.xmlGenerator().stringWriter().getBuffer().toString()).isEmpty()) {
            this.request.contentStreamProvider(() -> new StringInputStream(content));
            this.request.putHeader("Content-Length", Integer.toString(content.getBytes(StandardCharsets.UTF_8).length));
            this.setContentTypeHeaderIfNeeded("application/xml");
        }
        return this.request.build();
    }

    private boolean isBinary(SdkField<?> field, Object val) {
        return this.isExplicitPayloadMember(field) && val instanceof SdkBytes;
    }

    private boolean isExplicitPayloadMember(SdkField<?> field) {
        return field.containsTrait(PayloadTrait.class);
    }

    private boolean hasPayloadMembers(SdkPojo sdkPojo) {
        return sdkPojo.sdkFields().stream().anyMatch(f -> f.location() == MarshallLocation.PAYLOAD);
    }

    private void setContentTypeHeaderIfNeeded(String contentType) {
        if (contentType != null && !this.request.firstMatchingHeader("Content-Type").isPresent()) {
            this.request.putHeader("Content-Type", contentType);
        }
    }

    private static Map<MarshallLocation, TimestampFormatTrait.Format> getDefaultTimestampFormats() {
        EnumMap<MarshallLocation, TimestampFormatTrait.Format> formats = new EnumMap<MarshallLocation, TimestampFormatTrait.Format>(MarshallLocation.class);
        formats.put(MarshallLocation.HEADER, TimestampFormatTrait.Format.RFC_822);
        formats.put(MarshallLocation.PAYLOAD, TimestampFormatTrait.Format.ISO_8601);
        formats.put(MarshallLocation.QUERY_PARAM, TimestampFormatTrait.Format.ISO_8601);
        return Collections.unmodifiableMap(formats);
    }

    private static XmlMarshallerRegistry createMarshallerRegistry() {
        return XmlMarshallerRegistry.builder().payloadMarshaller(MarshallingType.STRING, XmlPayloadMarshaller.STRING).payloadMarshaller(MarshallingType.INTEGER, XmlPayloadMarshaller.INTEGER).payloadMarshaller(MarshallingType.LONG, XmlPayloadMarshaller.LONG).payloadMarshaller(MarshallingType.SHORT, XmlPayloadMarshaller.SHORT).payloadMarshaller(MarshallingType.FLOAT, XmlPayloadMarshaller.FLOAT).payloadMarshaller(MarshallingType.DOUBLE, XmlPayloadMarshaller.DOUBLE).payloadMarshaller(MarshallingType.BIG_DECIMAL, XmlPayloadMarshaller.BIG_DECIMAL).payloadMarshaller(MarshallingType.BOOLEAN, XmlPayloadMarshaller.BOOLEAN).payloadMarshaller(MarshallingType.INSTANT, XmlPayloadMarshaller.INSTANT).payloadMarshaller(MarshallingType.SDK_BYTES, XmlPayloadMarshaller.SDK_BYTES).payloadMarshaller(MarshallingType.SDK_POJO, XmlPayloadMarshaller.SDK_POJO).payloadMarshaller(MarshallingType.LIST, XmlPayloadMarshaller.LIST).payloadMarshaller(MarshallingType.MAP, XmlPayloadMarshaller.MAP).payloadMarshaller(MarshallingType.NULL, XmlPayloadMarshaller.NULL).headerMarshaller(MarshallingType.STRING, HeaderMarshaller.STRING).headerMarshaller(MarshallingType.INTEGER, HeaderMarshaller.INTEGER).headerMarshaller(MarshallingType.LONG, HeaderMarshaller.LONG).headerMarshaller(MarshallingType.SHORT, HeaderMarshaller.SHORT).headerMarshaller(MarshallingType.DOUBLE, HeaderMarshaller.DOUBLE).headerMarshaller(MarshallingType.FLOAT, HeaderMarshaller.FLOAT).headerMarshaller(MarshallingType.BOOLEAN, HeaderMarshaller.BOOLEAN).headerMarshaller(MarshallingType.INSTANT, HeaderMarshaller.INSTANT).headerMarshaller(MarshallingType.MAP, HeaderMarshaller.MAP).headerMarshaller(MarshallingType.LIST, HeaderMarshaller.LIST).headerMarshaller(MarshallingType.NULL, HeaderMarshaller.NULL).queryParamMarshaller(MarshallingType.STRING, QueryParamMarshaller.STRING).queryParamMarshaller(MarshallingType.INTEGER, QueryParamMarshaller.INTEGER).queryParamMarshaller(MarshallingType.LONG, QueryParamMarshaller.LONG).queryParamMarshaller(MarshallingType.SHORT, QueryParamMarshaller.SHORT).queryParamMarshaller(MarshallingType.DOUBLE, QueryParamMarshaller.DOUBLE).queryParamMarshaller(MarshallingType.FLOAT, QueryParamMarshaller.FLOAT).queryParamMarshaller(MarshallingType.BOOLEAN, QueryParamMarshaller.BOOLEAN).queryParamMarshaller(MarshallingType.INSTANT, QueryParamMarshaller.INSTANT).queryParamMarshaller(MarshallingType.LIST, QueryParamMarshaller.LIST).queryParamMarshaller(MarshallingType.MAP, QueryParamMarshaller.MAP).queryParamMarshaller(MarshallingType.NULL, QueryParamMarshaller.NULL).pathParamMarshaller(MarshallingType.STRING, SimpleTypePathMarshaller.STRING).pathParamMarshaller(MarshallingType.INTEGER, SimpleTypePathMarshaller.INTEGER).pathParamMarshaller(MarshallingType.LONG, SimpleTypePathMarshaller.LONG).pathParamMarshaller(MarshallingType.SHORT, SimpleTypePathMarshaller.SHORT).pathParamMarshaller(MarshallingType.NULL, SimpleTypePathMarshaller.NULL).greedyPathParamMarshaller(MarshallingType.STRING, SimpleTypePathMarshaller.GREEDY_STRING).greedyPathParamMarshaller(MarshallingType.NULL, SimpleTypePathMarshaller.NULL).build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private URI endpoint;
        private XmlGenerator xmlGenerator;
        private OperationInfo operationInfo;

        private Builder() {
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder xmlGenerator(XmlGenerator xmlGenerator) {
            this.xmlGenerator = xmlGenerator;
            return this;
        }

        public Builder operationInfo(OperationInfo operationInfo) {
            this.operationInfo = operationInfo;
            return this;
        }

        public XmlProtocolMarshaller build() {
            return new XmlProtocolMarshaller(this);
        }
    }
}

