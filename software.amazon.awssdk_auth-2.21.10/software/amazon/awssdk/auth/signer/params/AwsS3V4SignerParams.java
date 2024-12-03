/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 */
package software.amazon.awssdk.auth.signer.params;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;

@SdkPublicApi
public final class AwsS3V4SignerParams
extends Aws4SignerParams {
    private final Boolean enableChunkedEncoding;
    private final Boolean enablePayloadSigning;

    private AwsS3V4SignerParams(BuilderImpl builder) {
        super(builder);
        this.enableChunkedEncoding = builder.enableChunkedEncoding;
        this.enablePayloadSigning = builder.enablePayloadSigning;
    }

    public Boolean enableChunkedEncoding() {
        return this.enableChunkedEncoding;
    }

    public Boolean enablePayloadSigning() {
        return this.enablePayloadSigning;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static final class BuilderImpl
    extends Aws4SignerParams.BuilderImpl<Builder>
    implements Builder {
        static final boolean DEFAULT_CHUNKED_ENCODING_ENABLED = false;
        static final boolean DEFAULT_PAYLOAD_SIGNING_ENABLED = false;
        private Boolean enableChunkedEncoding = false;
        private Boolean enablePayloadSigning = false;

        private BuilderImpl() {
            this.normalizePath(false);
        }

        @Override
        public Builder enableChunkedEncoding(Boolean enableChunkedEncoding) {
            this.enableChunkedEncoding = enableChunkedEncoding;
            return this;
        }

        public void setEnableChunkedEncoding(Boolean enableChunkedEncoding) {
            this.enableChunkedEncoding(enableChunkedEncoding);
        }

        @Override
        public Builder enablePayloadSigning(Boolean enablePayloadSigning) {
            this.enablePayloadSigning = enablePayloadSigning;
            return this;
        }

        public void setEnablePayloadSigning(Boolean enablePayloadSigning) {
            this.enablePayloadSigning(enablePayloadSigning);
        }

        @Override
        public AwsS3V4SignerParams build() {
            return new AwsS3V4SignerParams(this);
        }
    }

    public static interface Builder
    extends Aws4SignerParams.Builder<Builder> {
        public Builder enableChunkedEncoding(Boolean var1);

        public Builder enablePayloadSigning(Boolean var1);

        @Override
        public AwsS3V4SignerParams build();
    }
}

