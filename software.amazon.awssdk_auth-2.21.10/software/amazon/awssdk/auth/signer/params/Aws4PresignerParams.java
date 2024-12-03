/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.auth.signer.params;

import java.time.Instant;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class Aws4PresignerParams
extends Aws4SignerParams
implements ToCopyableBuilder<Builder, Aws4PresignerParams> {
    private final Instant expirationTime;

    private Aws4PresignerParams(BuilderImpl builder) {
        super(builder);
        this.expirationTime = builder.expirationTime;
    }

    public Optional<Instant> expirationTime() {
        return Optional.ofNullable(this.expirationTime);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    private static final class BuilderImpl
    extends Aws4SignerParams.BuilderImpl<Builder>
    implements Builder {
        private Instant expirationTime;

        private BuilderImpl() {
        }

        private BuilderImpl(Aws4PresignerParams params) {
            super(params);
            this.expirationTime = params.expirationTime;
        }

        @Override
        public Builder expirationTime(Instant expirationTime) {
            this.expirationTime = expirationTime;
            return this;
        }

        public void setExpirationTime(Instant expirationTime) {
            this.expirationTime(expirationTime);
        }

        @Override
        public Aws4PresignerParams build() {
            return new Aws4PresignerParams(this);
        }
    }

    public static interface Builder
    extends Aws4SignerParams.Builder<Builder>,
    CopyableBuilder<Builder, Aws4PresignerParams> {
        public Builder expirationTime(Instant var1);

        @Override
        public Aws4PresignerParams build();
    }
}

