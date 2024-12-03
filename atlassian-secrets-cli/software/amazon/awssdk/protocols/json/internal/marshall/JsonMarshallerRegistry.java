/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.marshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.protocols.core.AbstractMarshallingRegistry;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonMarshaller;

@SdkInternalApi
public final class JsonMarshallerRegistry
extends AbstractMarshallingRegistry {
    private JsonMarshallerRegistry(Builder builder) {
        super(builder);
    }

    public <T> JsonMarshaller<T> getMarshaller(MarshallLocation marshallLocation, T val) {
        return (JsonMarshaller)this.get(marshallLocation, this.toMarshallingType(val));
    }

    public <T> JsonMarshaller<Object> getMarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType, Object val) {
        return (JsonMarshaller)this.get(marshallLocation, val == null ? MarshallingType.NULL : marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    extends AbstractMarshallingRegistry.Builder {
        private Builder() {
        }

        public <T> Builder payloadMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            this.register(MarshallLocation.PAYLOAD, marshallingType, marshaller);
            return this;
        }

        public <T> Builder headerMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            this.register(MarshallLocation.HEADER, marshallingType, marshaller);
            return this;
        }

        public <T> Builder queryParamMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            this.register(MarshallLocation.QUERY_PARAM, marshallingType, marshaller);
            return this;
        }

        public <T> Builder pathParamMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            this.register(MarshallLocation.PATH, marshallingType, marshaller);
            return this;
        }

        public <T> Builder greedyPathParamMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            this.register(MarshallLocation.GREEDY_PATH, marshallingType, marshaller);
            return this;
        }

        public JsonMarshallerRegistry build() {
            return new JsonMarshallerRegistry(this);
        }
    }
}

