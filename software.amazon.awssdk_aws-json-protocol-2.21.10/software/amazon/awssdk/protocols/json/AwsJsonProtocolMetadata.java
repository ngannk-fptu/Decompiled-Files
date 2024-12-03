/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.protocols.json;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.json.AwsJsonProtocol;

@SdkProtectedApi
public final class AwsJsonProtocolMetadata {
    private final AwsJsonProtocol protocol;
    private final String protocolVersion;
    private final String contentType;

    private AwsJsonProtocolMetadata(Builder builder) {
        this.protocol = builder.protocol;
        this.protocolVersion = builder.protocolVersion;
        this.contentType = builder.contentType;
    }

    public AwsJsonProtocol protocol() {
        return this.protocol;
    }

    public String protocolVersion() {
        return this.protocolVersion;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String contentType() {
        return this.contentType;
    }

    public static final class Builder {
        private AwsJsonProtocol protocol;
        private String protocolVersion;
        private String contentType;

        private Builder() {
        }

        public Builder protocol(AwsJsonProtocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder protocolVersion(String protocolVersion) {
            this.protocolVersion = protocolVersion;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public AwsJsonProtocolMetadata build() {
            return new AwsJsonProtocolMetadata(this);
        }
    }
}

