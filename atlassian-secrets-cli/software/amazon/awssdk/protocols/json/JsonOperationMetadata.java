/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class JsonOperationMetadata {
    private final boolean hasStreamingSuccessResponse;
    private final boolean isPayloadJson;

    private JsonOperationMetadata(Builder builder) {
        this.hasStreamingSuccessResponse = builder.hasStreamingSuccessResponse;
        this.isPayloadJson = builder.isPayloadJson;
    }

    public boolean hasStreamingSuccessResponse() {
        return this.hasStreamingSuccessResponse;
    }

    public boolean isPayloadJson() {
        return this.isPayloadJson;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean hasStreamingSuccessResponse;
        private boolean isPayloadJson;

        private Builder() {
        }

        public Builder isPayloadJson(boolean payloadJson) {
            this.isPayloadJson = payloadJson;
            return this;
        }

        public Builder hasStreamingSuccessResponse(boolean hasStreamingSuccessResponse) {
            this.hasStreamingSuccessResponse = hasStreamingSuccessResponse;
            return this;
        }

        public JsonOperationMetadata build() {
            return new JsonOperationMetadata(this);
        }
    }
}

