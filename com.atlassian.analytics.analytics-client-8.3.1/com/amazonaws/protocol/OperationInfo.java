/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.protocol.Protocol;

@SdkProtectedApi
public class OperationInfo {
    private final Protocol protocol;
    private final String requestUri;
    private final HttpMethodName httpMethodName;
    private final String operationIdentifier;
    private final String serviceName;
    private final boolean hasExplicitPayloadMember;
    private final boolean hasPayloadMembers;
    private final String serviceId;

    private OperationInfo(Builder builder) {
        this.protocol = builder.protocol;
        this.requestUri = builder.requestUri;
        this.httpMethodName = builder.httpMethodName;
        this.operationIdentifier = builder.operationIdentifier;
        this.serviceName = builder.serviceName;
        this.hasExplicitPayloadMember = builder.hasExplicitPayloadMember;
        this.hasPayloadMembers = builder.hasPayloadMembers;
        this.serviceId = builder.serviceId;
    }

    public Protocol protocol() {
        return this.protocol;
    }

    public String requestUri() {
        return this.requestUri;
    }

    public HttpMethodName httpMethodName() {
        return this.httpMethodName;
    }

    public String operationIdentifier() {
        return this.operationIdentifier;
    }

    public String serviceName() {
        return this.serviceName;
    }

    public boolean hasExplicitPayloadMember() {
        return this.hasExplicitPayloadMember;
    }

    public boolean hasPayloadMembers() {
        return this.hasPayloadMembers;
    }

    public String serviceId() {
        return this.serviceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Protocol protocol;
        private String requestUri;
        private HttpMethodName httpMethodName;
        private String operationIdentifier;
        private String serviceName;
        private boolean hasExplicitPayloadMember;
        private boolean hasPayloadMembers;
        private String serviceId;

        public Builder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder requestUri(String requestUri) {
            this.requestUri = requestUri;
            return this;
        }

        public Builder httpMethodName(HttpMethodName httpMethodName) {
            this.httpMethodName = httpMethodName;
            return this;
        }

        public Builder operationIdentifier(String operationIdentifier) {
            this.operationIdentifier = operationIdentifier;
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
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

        public Builder serviceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        private Builder() {
        }

        public OperationInfo build() {
            return new OperationInfo(this);
        }
    }
}

