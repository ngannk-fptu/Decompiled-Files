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
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

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
import software.amazon.awssdk.protocols.json.AwsJsonProtocol;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolMetadata;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.json.internal.marshall.HeaderMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerContext;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerRegistry;
import software.amazon.awssdk.protocols.json.internal.marshall.QueryParamMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.SimpleTypeJsonMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.SimpleTypePathMarshaller;

@SdkInternalApi
public class JsonProtocolMarshaller
implements ProtocolMarshaller<SdkHttpFullRequest> {
    public static final ValueToStringConverter.ValueToString<Instant> INSTANT_VALUE_TO_STRING = InstantToString.create(JsonProtocolMarshaller.getDefaultTimestampFormats());
    private static final JsonMarshallerRegistry MARSHALLER_REGISTRY = JsonProtocolMarshaller.createMarshallerRegistry();
    private final URI endpoint;
    private final StructuredJsonGenerator jsonGenerator;
    private final SdkHttpFullRequest.Builder request;
    private final String contentType;
    private final AwsJsonProtocolMetadata protocolMetadata;
    private final boolean hasExplicitPayloadMember;
    private final boolean hasImplicitPayloadMembers;
    private final boolean hasStreamingInput;
    private final JsonMarshallerContext marshallerContext;
    private final boolean hasEventStreamingInput;
    private final boolean hasEvent;

    JsonProtocolMarshaller(URI endpoint, StructuredJsonGenerator jsonGenerator, String contentType, OperationInfo operationInfo, AwsJsonProtocolMetadata protocolMetadata) {
        this.endpoint = endpoint;
        this.jsonGenerator = jsonGenerator;
        this.contentType = contentType;
        this.protocolMetadata = protocolMetadata;
        this.hasExplicitPayloadMember = operationInfo.hasExplicitPayloadMember();
        this.hasImplicitPayloadMembers = operationInfo.hasImplicitPayloadMembers();
        this.hasStreamingInput = operationInfo.hasStreamingInput();
        this.hasEventStreamingInput = operationInfo.hasEventStreamingInput();
        this.hasEvent = operationInfo.hasEvent();
        this.request = this.fillBasicRequestParams(operationInfo);
        this.marshallerContext = JsonMarshallerContext.builder().jsonGenerator(jsonGenerator).marshallerRegistry(MARSHALLER_REGISTRY).protocolHandler(this).request(this.request).build();
    }

    private static JsonMarshallerRegistry createMarshallerRegistry() {
        return JsonMarshallerRegistry.builder().payloadMarshaller(MarshallingType.STRING, SimpleTypeJsonMarshaller.STRING).payloadMarshaller(MarshallingType.INTEGER, SimpleTypeJsonMarshaller.INTEGER).payloadMarshaller(MarshallingType.LONG, SimpleTypeJsonMarshaller.LONG).payloadMarshaller(MarshallingType.SHORT, SimpleTypeJsonMarshaller.SHORT).payloadMarshaller(MarshallingType.DOUBLE, SimpleTypeJsonMarshaller.DOUBLE).payloadMarshaller(MarshallingType.FLOAT, SimpleTypeJsonMarshaller.FLOAT).payloadMarshaller(MarshallingType.BIG_DECIMAL, SimpleTypeJsonMarshaller.BIG_DECIMAL).payloadMarshaller(MarshallingType.BOOLEAN, SimpleTypeJsonMarshaller.BOOLEAN).payloadMarshaller(MarshallingType.INSTANT, SimpleTypeJsonMarshaller.INSTANT).payloadMarshaller(MarshallingType.SDK_BYTES, SimpleTypeJsonMarshaller.SDK_BYTES).payloadMarshaller(MarshallingType.SDK_POJO, SimpleTypeJsonMarshaller.SDK_POJO).payloadMarshaller(MarshallingType.LIST, SimpleTypeJsonMarshaller.LIST).payloadMarshaller(MarshallingType.MAP, SimpleTypeJsonMarshaller.MAP).payloadMarshaller(MarshallingType.NULL, SimpleTypeJsonMarshaller.NULL).payloadMarshaller(MarshallingType.DOCUMENT, SimpleTypeJsonMarshaller.DOCUMENT).headerMarshaller(MarshallingType.STRING, HeaderMarshaller.STRING).headerMarshaller(MarshallingType.INTEGER, HeaderMarshaller.INTEGER).headerMarshaller(MarshallingType.LONG, HeaderMarshaller.LONG).headerMarshaller(MarshallingType.SHORT, HeaderMarshaller.SHORT).headerMarshaller(MarshallingType.DOUBLE, HeaderMarshaller.DOUBLE).headerMarshaller(MarshallingType.FLOAT, HeaderMarshaller.FLOAT).headerMarshaller(MarshallingType.BOOLEAN, HeaderMarshaller.BOOLEAN).headerMarshaller(MarshallingType.INSTANT, HeaderMarshaller.INSTANT).headerMarshaller(MarshallingType.LIST, HeaderMarshaller.LIST).headerMarshaller(MarshallingType.NULL, HeaderMarshaller.NULL).queryParamMarshaller(MarshallingType.STRING, QueryParamMarshaller.STRING).queryParamMarshaller(MarshallingType.INTEGER, QueryParamMarshaller.INTEGER).queryParamMarshaller(MarshallingType.LONG, QueryParamMarshaller.LONG).queryParamMarshaller(MarshallingType.SHORT, QueryParamMarshaller.SHORT).queryParamMarshaller(MarshallingType.DOUBLE, QueryParamMarshaller.DOUBLE).queryParamMarshaller(MarshallingType.FLOAT, QueryParamMarshaller.FLOAT).queryParamMarshaller(MarshallingType.BOOLEAN, QueryParamMarshaller.BOOLEAN).queryParamMarshaller(MarshallingType.INSTANT, QueryParamMarshaller.INSTANT).queryParamMarshaller(MarshallingType.LIST, QueryParamMarshaller.LIST).queryParamMarshaller(MarshallingType.MAP, QueryParamMarshaller.MAP).queryParamMarshaller(MarshallingType.NULL, QueryParamMarshaller.NULL).pathParamMarshaller(MarshallingType.STRING, SimpleTypePathMarshaller.STRING).pathParamMarshaller(MarshallingType.INTEGER, SimpleTypePathMarshaller.INTEGER).pathParamMarshaller(MarshallingType.LONG, SimpleTypePathMarshaller.LONG).pathParamMarshaller(MarshallingType.SHORT, SimpleTypePathMarshaller.SHORT).pathParamMarshaller(MarshallingType.NULL, SimpleTypePathMarshaller.NULL).greedyPathParamMarshaller(MarshallingType.STRING, SimpleTypePathMarshaller.GREEDY_STRING).greedyPathParamMarshaller(MarshallingType.NULL, SimpleTypePathMarshaller.NULL).build();
    }

    private static Map<MarshallLocation, TimestampFormatTrait.Format> getDefaultTimestampFormats() {
        EnumMap<MarshallLocation, TimestampFormatTrait.Format> formats = new EnumMap<MarshallLocation, TimestampFormatTrait.Format>(MarshallLocation.class);
        formats.put(MarshallLocation.HEADER, TimestampFormatTrait.Format.RFC_822);
        formats.put(MarshallLocation.PAYLOAD, TimestampFormatTrait.Format.UNIX_TIMESTAMP);
        formats.put(MarshallLocation.QUERY_PARAM, TimestampFormatTrait.Format.ISO_8601);
        return Collections.unmodifiableMap(formats);
    }

    private SdkHttpFullRequest.Builder fillBasicRequestParams(OperationInfo operationInfo) {
        return ProtocolUtils.createSdkHttpRequest((OperationInfo)operationInfo, (URI)this.endpoint).applyMutation(b -> {
            if (operationInfo.operationIdentifier() != null) {
                b.putHeader("X-Amz-Target", operationInfo.operationIdentifier());
            }
        });
    }

    private void startMarshalling() {
        if (this.needTopLevelJsonObject()) {
            this.jsonGenerator.writeStartObject();
        }
    }

    void doMarshall(SdkPojo pojo) {
        for (SdkField field : pojo.sdkFields()) {
            Object val = field.getValueOrDefault((Object)pojo);
            if (this.isExplicitBinaryPayload(field)) {
                if (val == null) continue;
                this.request.contentStreamProvider(() -> ((SdkBytes)((SdkBytes)val)).asInputStream());
                continue;
            }
            if (this.isExplicitStringPayload(field)) {
                if (val == null) continue;
                byte[] content = ((String)val).getBytes(StandardCharsets.UTF_8);
                this.request.contentStreamProvider(() -> new ByteArrayInputStream(content));
                continue;
            }
            if (this.isExplicitPayloadMember(field)) {
                this.marshallExplicitJsonPayload(field, val);
                continue;
            }
            this.marshallField(field, val);
        }
    }

    private boolean isExplicitBinaryPayload(SdkField<?> field) {
        return this.isExplicitPayloadMember(field) && MarshallingType.SDK_BYTES.equals(field.marshallingType());
    }

    private boolean isExplicitStringPayload(SdkField<?> field) {
        return this.isExplicitPayloadMember(field) && MarshallingType.STRING.equals(field.marshallingType());
    }

    private boolean isExplicitPayloadMember(SdkField<?> field) {
        return field.containsTrait(PayloadTrait.class);
    }

    private void marshallExplicitJsonPayload(SdkField<?> field, Object val) {
        this.jsonGenerator.writeStartObject();
        if (val != null) {
            if (MarshallingType.DOCUMENT.equals(field.marshallingType())) {
                this.marshallField(field, val);
            } else {
                this.doMarshall((SdkPojo)val);
            }
        }
        this.jsonGenerator.writeEndObject();
    }

    public SdkHttpFullRequest marshall(SdkPojo pojo) {
        this.startMarshalling();
        this.doMarshall(pojo);
        return this.finishMarshalling();
    }

    private SdkHttpFullRequest finishMarshalling() {
        if (this.request.contentStreamProvider() == null) {
            byte[] content;
            if (this.needTopLevelJsonObject()) {
                this.jsonGenerator.writeEndObject();
            }
            if ((content = this.jsonGenerator.getBytes()) != null) {
                this.request.contentStreamProvider(() -> new ByteArrayInputStream(content));
                if (content.length > 0) {
                    this.request.putHeader("Content-Length", Integer.toString(content.length));
                }
            }
        }
        if (!this.request.firstMatchingHeader("Content-Type").isPresent() && !this.hasEvent) {
            if (this.hasEventStreamingInput) {
                AwsJsonProtocol protocol = this.protocolMetadata.protocol();
                if (protocol == AwsJsonProtocol.AWS_JSON) {
                    this.request.putHeader("Content-Type", this.contentType);
                } else if (protocol == AwsJsonProtocol.REST_JSON) {
                    this.request.putHeader("Content-Type", "application/vnd.amazon.eventstream");
                } else {
                    throw new IllegalArgumentException("Unknown AwsJsonProtocol: " + (Object)((Object)protocol));
                }
                this.request.removeHeader("Content-Length");
                this.request.putHeader("Transfer-Encoding", "chunked");
            } else if (this.contentType != null && !this.hasStreamingInput && this.request.firstMatchingHeader("Content-Length").isPresent()) {
                this.request.putHeader("Content-Type", this.contentType);
            }
        }
        return this.request.build();
    }

    private void marshallField(SdkField<?> field, Object val) {
        MARSHALLER_REGISTRY.getMarshaller(field.location(), field.marshallingType(), val).marshall(val, this.marshallerContext, field.locationName(), field);
    }

    private boolean needTopLevelJsonObject() {
        return AwsJsonProtocol.AWS_JSON.equals((Object)this.protocolMetadata.protocol()) || !this.hasExplicitPayloadMember && this.hasImplicitPayloadMembers;
    }
}

