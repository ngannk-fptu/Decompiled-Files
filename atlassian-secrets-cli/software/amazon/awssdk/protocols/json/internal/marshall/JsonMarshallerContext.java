/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshallerRegistry;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonProtocolMarshaller;

@SdkInternalApi
public final class JsonMarshallerContext {
    private final StructuredJsonGenerator jsonGenerator;
    private final JsonProtocolMarshaller protocolHandler;
    private final JsonMarshallerRegistry marshallerRegistry;
    private final SdkHttpFullRequest.Builder request;

    private JsonMarshallerContext(Builder builder) {
        this.jsonGenerator = builder.jsonGenerator;
        this.protocolHandler = builder.protocolHandler;
        this.marshallerRegistry = builder.marshallerRegistry;
        this.request = builder.request;
    }

    public StructuredJsonGenerator jsonGenerator() {
        return this.jsonGenerator;
    }

    public JsonProtocolMarshaller protocolHandler() {
        return this.protocolHandler;
    }

    public JsonMarshallerRegistry marshallerRegistry() {
        return this.marshallerRegistry;
    }

    public SdkHttpFullRequest.Builder request() {
        return this.request;
    }

    public void marshall(MarshallLocation marshallLocation, Object val) {
        this.marshallerRegistry().getMarshaller(marshallLocation, val).marshall(val, this, null, null);
    }

    public <T> void marshall(MarshallLocation marshallLocation, T val, String paramName) {
        this.marshallerRegistry().getMarshaller(marshallLocation, val).marshall(val, this, paramName, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private StructuredJsonGenerator jsonGenerator;
        private JsonProtocolMarshaller protocolHandler;
        private JsonMarshallerRegistry marshallerRegistry;
        private SdkHttpFullRequest.Builder request;

        private Builder() {
        }

        public Builder jsonGenerator(StructuredJsonGenerator jsonGenerator) {
            this.jsonGenerator = jsonGenerator;
            return this;
        }

        public Builder protocolHandler(JsonProtocolMarshaller protocolHandler) {
            this.protocolHandler = protocolHandler;
            return this;
        }

        public Builder marshallerRegistry(JsonMarshallerRegistry marshallerRegistry) {
            this.marshallerRegistry = marshallerRegistry;
            return this;
        }

        public Builder request(SdkHttpFullRequest.Builder request) {
            this.request = request;
            return this;
        }

        public JsonMarshallerContext build() {
            return new JsonMarshallerContext(this);
        }
    }
}

