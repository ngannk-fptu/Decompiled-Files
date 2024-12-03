/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshaller;

@SdkInternalApi
final class JsonUnmarshallerRegistry
extends AbstractMarshallingRegistry {
    private JsonUnmarshallerRegistry(Builder builder) {
        super(builder);
    }

    public <T> JsonUnmarshaller<Object> getUnmarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType) {
        return (JsonUnmarshaller)this.get(marshallLocation, marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends AbstractMarshallingRegistry.Builder {
        private Builder() {
        }

        public <T> Builder payloadUnmarshaller(MarshallingType<T> marshallingType, JsonUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.PAYLOAD, marshallingType, marshaller);
            return this;
        }

        public <T> Builder headerUnmarshaller(MarshallingType<T> marshallingType, JsonUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.HEADER, marshallingType, marshaller);
            return this;
        }

        public <T> Builder statusCodeUnmarshaller(MarshallingType<T> marshallingType, JsonUnmarshaller<T> marshaller) {
            this.register(MarshallLocation.STATUS_CODE, marshallingType, marshaller);
            return this;
        }

        public JsonUnmarshallerRegistry build() {
            return new JsonUnmarshallerRegistry(this);
        }
    }
}

