/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.signer.params;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public class SignerChecksumParams {
    private final Algorithm algorithm;
    private final String checksumHeaderName;
    private final boolean isStreamingRequest;

    private SignerChecksumParams(Builder builder) {
        Validate.notNull(builder.algorithm, "algorithm is null", new Object[0]);
        Validate.notNull(builder.checksumHeaderName, "checksumHeaderName is null", new Object[0]);
        this.algorithm = builder.algorithm;
        this.checksumHeaderName = builder.checksumHeaderName;
        this.isStreamingRequest = builder.isStreamingRequest;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Algorithm algorithm() {
        return this.algorithm;
    }

    public String checksumHeaderName() {
        return this.checksumHeaderName;
    }

    public boolean isStreamingRequest() {
        return this.isStreamingRequest;
    }

    public static final class Builder {
        private Algorithm algorithm;
        private String checksumHeaderName;
        private boolean isStreamingRequest;

        private Builder() {
        }

        public Builder algorithm(Algorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        public Builder checksumHeaderName(String checksumHeaderName) {
            this.checksumHeaderName = checksumHeaderName;
            return this;
        }

        public Builder isStreamingRequest(boolean isStreamingRequest) {
            this.isStreamingRequest = isStreamingRequest;
            return this;
        }

        public SignerChecksumParams build() {
            return new SignerChecksumParams(this);
        }
    }
}

