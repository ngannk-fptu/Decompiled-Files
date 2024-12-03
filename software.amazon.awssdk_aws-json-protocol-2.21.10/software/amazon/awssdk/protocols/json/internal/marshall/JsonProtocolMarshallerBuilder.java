/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.ProtocolMarshaller
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolMetadata;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonProtocolMarshaller;
import software.amazon.awssdk.protocols.json.internal.marshall.NullAsEmptyBodyProtocolRequestMarshaller;

@SdkInternalApi
public final class JsonProtocolMarshallerBuilder {
    private URI endpoint;
    private StructuredJsonGenerator jsonGenerator;
    private String contentType;
    private OperationInfo operationInfo;
    private boolean sendExplicitNullForPayload;
    private AwsJsonProtocolMetadata protocolMetadata;

    private JsonProtocolMarshallerBuilder() {
    }

    public static JsonProtocolMarshallerBuilder create() {
        return new JsonProtocolMarshallerBuilder();
    }

    public JsonProtocolMarshallerBuilder endpoint(URI endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public JsonProtocolMarshallerBuilder jsonGenerator(StructuredJsonGenerator jsonGenerator) {
        this.jsonGenerator = jsonGenerator;
        return this;
    }

    public JsonProtocolMarshallerBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public JsonProtocolMarshallerBuilder operationInfo(OperationInfo operationInfo) {
        this.operationInfo = operationInfo;
        return this;
    }

    public JsonProtocolMarshallerBuilder sendExplicitNullForPayload(boolean sendExplicitNullForPayload) {
        this.sendExplicitNullForPayload = sendExplicitNullForPayload;
        return this;
    }

    public JsonProtocolMarshallerBuilder protocolMetadata(AwsJsonProtocolMetadata protocolMetadata) {
        this.protocolMetadata = protocolMetadata;
        return this;
    }

    public ProtocolMarshaller<SdkHttpFullRequest> build() {
        JsonProtocolMarshaller protocolMarshaller = new JsonProtocolMarshaller(this.endpoint, this.jsonGenerator, this.contentType, this.operationInfo, this.protocolMetadata);
        return this.sendExplicitNullForPayload ? protocolMarshaller : new NullAsEmptyBodyProtocolRequestMarshaller(protocolMarshaller);
    }
}

