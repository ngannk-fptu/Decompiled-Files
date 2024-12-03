/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.protocols.core.OperationMetadataAttribute;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public final class OperationInfo {
    private final String requestUri;
    private final SdkHttpMethod httpMethod;
    private final String operationIdentifier;
    private final String apiVersion;
    private final boolean hasExplicitPayloadMember;
    private final boolean hasPayloadMembers;
    private final boolean hasImplicitPayloadMembers;
    private final boolean hasStreamingInput;
    private final boolean hasEventStreamingInput;
    private final boolean hasEvent;
    private final AttributeMap additionalMetadata;

    private OperationInfo(Builder builder) {
        this.requestUri = builder.requestUri;
        this.httpMethod = builder.httpMethod;
        this.operationIdentifier = builder.operationIdentifier;
        this.apiVersion = builder.apiVersion;
        this.hasExplicitPayloadMember = builder.hasExplicitPayloadMember;
        this.hasImplicitPayloadMembers = builder.hasImplicitPayloadMembers;
        this.hasPayloadMembers = builder.hasPayloadMembers;
        this.hasStreamingInput = builder.hasStreamingInput;
        this.additionalMetadata = builder.additionalMetadata.build();
        this.hasEventStreamingInput = builder.hasEventStreamingInput;
        this.hasEvent = builder.hasEvent;
    }

    public String requestUri() {
        return this.requestUri;
    }

    public SdkHttpMethod httpMethod() {
        return this.httpMethod;
    }

    public String operationIdentifier() {
        return this.operationIdentifier;
    }

    public String apiVersion() {
        return this.apiVersion;
    }

    public boolean hasExplicitPayloadMember() {
        return this.hasExplicitPayloadMember;
    }

    public boolean hasPayloadMembers() {
        return this.hasPayloadMembers;
    }

    public boolean hasImplicitPayloadMembers() {
        return this.hasImplicitPayloadMembers;
    }

    public boolean hasStreamingInput() {
        return this.hasStreamingInput;
    }

    public boolean hasEventStreamingInput() {
        return this.hasEventStreamingInput;
    }

    public boolean hasEvent() {
        return this.hasEvent;
    }

    public <T> T addtionalMetadata(OperationMetadataAttribute<T> key) {
        return this.additionalMetadata.get(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String requestUri;
        private SdkHttpMethod httpMethod;
        private String operationIdentifier;
        private String apiVersion;
        private boolean hasExplicitPayloadMember;
        private boolean hasImplicitPayloadMembers;
        private boolean hasPayloadMembers;
        private boolean hasStreamingInput;
        private boolean hasEventStreamingInput;
        private boolean hasEvent;
        private AttributeMap.Builder additionalMetadata = AttributeMap.builder();

        private Builder() {
        }

        public Builder requestUri(String requestUri) {
            this.requestUri = requestUri;
            return this;
        }

        public Builder httpMethod(SdkHttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder operationIdentifier(String operationIdentifier) {
            this.operationIdentifier = operationIdentifier;
            return this;
        }

        public Builder apiVersion(String apiVersion) {
            this.apiVersion = apiVersion;
            return this;
        }

        public Builder hasExplicitPayloadMember(boolean hasExplicitPayloadMember) {
            this.hasExplicitPayloadMember = hasExplicitPayloadMember;
            return this;
        }

        public Builder hasPayloadMembers(boolean hasPayloadMembers) {
            this.hasPayloadMembers = hasPayloadMembers;
            return this;
        }

        public Builder hasImplicitPayloadMembers(boolean hasImplicitPayloadMembers) {
            this.hasImplicitPayloadMembers = hasImplicitPayloadMembers;
            return this;
        }

        public Builder hasStreamingInput(boolean hasStreamingInput) {
            this.hasStreamingInput = hasStreamingInput;
            return this;
        }

        public Builder hasEventStreamingInput(boolean hasEventStreamingInput) {
            this.hasEventStreamingInput = hasEventStreamingInput;
            return this;
        }

        public Builder hasEvent(boolean hasEvent) {
            this.hasEvent = hasEvent;
            return this;
        }

        public <T> Builder putAdditionalMetadata(OperationMetadataAttribute<T> key, T value) {
            this.additionalMetadata.put(key, value);
            return this;
        }

        public OperationInfo build() {
            return new OperationInfo(this);
        }
    }
}

