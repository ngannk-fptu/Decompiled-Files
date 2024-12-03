/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.internal.AwsServiceProtocol;
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkInternalApi
public final class AwsProtocolMetadata
implements SdkProtocolMetadata,
ToCopyableBuilder<Builder, AwsProtocolMetadata> {
    private final AwsServiceProtocol serviceProtocol;

    private AwsProtocolMetadata(Builder builder) {
        this.serviceProtocol = builder.serviceProtocol;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String serviceProtocol() {
        return this.serviceProtocol.toString();
    }

    public String toString() {
        return ToString.builder("AwsProtocolMetadata").add("serviceProtocol", (Object)this.serviceProtocol).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AwsProtocolMetadata protocolMetadata = (AwsProtocolMetadata)o;
        return this.serviceProtocol == protocolMetadata.serviceProtocol;
    }

    public int hashCode() {
        return this.serviceProtocol != null ? this.serviceProtocol.hashCode() : 0;
    }

    public static final class Builder
    implements CopyableBuilder<Builder, AwsProtocolMetadata> {
        private AwsServiceProtocol serviceProtocol;

        private Builder() {
        }

        private Builder(AwsProtocolMetadata protocolMetadata) {
            this.serviceProtocol = protocolMetadata.serviceProtocol;
        }

        public Builder serviceProtocol(AwsServiceProtocol serviceProtocol) {
            this.serviceProtocol = serviceProtocol;
            return this;
        }

        @Override
        public AwsProtocolMetadata build() {
            return new AwsProtocolMetadata(this);
        }
    }
}

