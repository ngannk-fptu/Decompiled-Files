/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.json.internal.MarshallerUtil;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshaller;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonUnmarshallerRegistry;

@SdkInternalApi
public final class JsonUnmarshallerContext {
    private final SdkHttpFullResponse response;
    private final JsonUnmarshallerRegistry unmarshallerRegistry;

    private JsonUnmarshallerContext(Builder builder) {
        this.response = builder.response;
        this.unmarshallerRegistry = builder.unmarshallerRegistry;
    }

    public SdkHttpFullResponse response() {
        return this.response;
    }

    public JsonUnmarshaller<Object> getUnmarshaller(MarshallLocation location, MarshallingType<?> marshallingType) {
        if (MarshallerUtil.isInUri(location)) {
            location = MarshallLocation.PAYLOAD;
        }
        return this.unmarshallerRegistry.getUnmarshaller(location, marshallingType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SdkHttpFullResponse response;
        private JsonUnmarshallerRegistry unmarshallerRegistry;

        private Builder() {
        }

        public Builder response(SdkHttpFullResponse response) {
            this.response = response;
            return this;
        }

        public Builder unmarshallerRegistry(JsonUnmarshallerRegistry unmarshallerRegistry) {
            this.unmarshallerRegistry = unmarshallerRegistry;
            return this;
        }

        public JsonUnmarshallerContext build() {
            return new JsonUnmarshallerContext(this);
        }
    }
}

