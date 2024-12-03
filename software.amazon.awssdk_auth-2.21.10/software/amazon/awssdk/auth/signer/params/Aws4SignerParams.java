/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.auth.signer.params;

import java.time.Clock;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.signer.params.SignerChecksumParams;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public class Aws4SignerParams {
    private final Boolean doubleUrlEncode;
    private final Boolean normalizePath;
    private final AwsCredentials awsCredentials;
    private final String signingName;
    private final Region signingRegion;
    private final Integer timeOffset;
    private final Clock signingClockOverride;
    private final SignerChecksumParams checksumParams;

    Aws4SignerParams(BuilderImpl<?> builder) {
        this.doubleUrlEncode = (Boolean)Validate.paramNotNull((Object)((BuilderImpl)builder).doubleUrlEncode, (String)"Double url encode");
        this.normalizePath = (Boolean)Validate.paramNotNull((Object)((BuilderImpl)builder).normalizePath, (String)"Normalize resource path");
        this.awsCredentials = (AwsCredentials)Validate.paramNotNull((Object)((BuilderImpl)builder).awsCredentials, (String)"Credentials");
        this.signingName = (String)Validate.paramNotNull((Object)((BuilderImpl)builder).signingName, (String)"service signing name");
        this.signingRegion = (Region)Validate.paramNotNull((Object)((BuilderImpl)builder).signingRegion, (String)"signing region");
        this.timeOffset = ((BuilderImpl)builder).timeOffset;
        this.signingClockOverride = ((BuilderImpl)builder).signingClockOverride;
        this.checksumParams = ((BuilderImpl)builder).checksumParams;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public Boolean doubleUrlEncode() {
        return this.doubleUrlEncode;
    }

    public Boolean normalizePath() {
        return this.normalizePath;
    }

    public AwsCredentials awsCredentials() {
        return this.awsCredentials;
    }

    public String signingName() {
        return this.signingName;
    }

    public Region signingRegion() {
        return this.signingRegion;
    }

    public Optional<Integer> timeOffset() {
        return Optional.ofNullable(this.timeOffset);
    }

    public Optional<Clock> signingClockOverride() {
        return Optional.ofNullable(this.signingClockOverride);
    }

    public SignerChecksumParams checksumParams() {
        return this.checksumParams;
    }

    protected static class BuilderImpl<B extends Builder<B>>
    implements Builder<B> {
        private static final Boolean DEFAULT_DOUBLE_URL_ENCODE = Boolean.TRUE;
        private Boolean doubleUrlEncode = DEFAULT_DOUBLE_URL_ENCODE;
        private Boolean normalizePath = Boolean.TRUE;
        private AwsCredentials awsCredentials;
        private String signingName;
        private Region signingRegion;
        private Integer timeOffset;
        private Clock signingClockOverride;
        private SignerChecksumParams checksumParams;

        protected BuilderImpl() {
        }

        protected BuilderImpl(Aws4SignerParams params) {
            this.doubleUrlEncode = params.doubleUrlEncode;
            this.normalizePath = params.normalizePath;
            this.awsCredentials = params.awsCredentials;
            this.signingName = params.signingName;
            this.signingRegion = params.signingRegion;
            this.timeOffset = params.timeOffset;
            this.signingClockOverride = params.signingClockOverride;
            this.checksumParams = params.checksumParams;
        }

        @Override
        public B doubleUrlEncode(Boolean doubleUrlEncode) {
            this.doubleUrlEncode = doubleUrlEncode;
            return (B)this;
        }

        public void setDoubleUrlEncode(Boolean doubleUrlEncode) {
            this.doubleUrlEncode(doubleUrlEncode);
        }

        @Override
        public B normalizePath(Boolean normalizePath) {
            this.normalizePath = normalizePath;
            return (B)this;
        }

        public void setNormalizePath(Boolean normalizePath) {
            this.normalizePath(normalizePath);
        }

        @Override
        public B awsCredentials(AwsCredentials awsCredentials) {
            this.awsCredentials = awsCredentials;
            return (B)this;
        }

        public void setAwsCredentials(AwsCredentials awsCredentials) {
            this.awsCredentials(awsCredentials);
        }

        @Override
        public B signingName(String signingName) {
            this.signingName = signingName;
            return (B)this;
        }

        public void setSigningName(String signingName) {
            this.signingName(signingName);
        }

        @Override
        public B signingRegion(Region signingRegion) {
            this.signingRegion = signingRegion;
            return (B)this;
        }

        public void setSigningRegion(Region signingRegion) {
            this.signingRegion(signingRegion);
        }

        @Override
        public B timeOffset(Integer timeOffset) {
            this.timeOffset = timeOffset;
            return (B)this;
        }

        public void setTimeOffset(Integer timeOffset) {
            this.timeOffset(timeOffset);
        }

        @Override
        public B signingClockOverride(Clock signingClockOverride) {
            this.signingClockOverride = signingClockOverride;
            return (B)this;
        }

        @Override
        public B checksumParams(SignerChecksumParams checksumParams) {
            this.checksumParams = checksumParams;
            return (B)this;
        }

        public void setSigningClockOverride(Clock signingClockOverride) {
            this.signingClockOverride(signingClockOverride);
        }

        @Override
        public Aws4SignerParams build() {
            return new Aws4SignerParams(this);
        }
    }

    public static interface Builder<B extends Builder<B>> {
        public B doubleUrlEncode(Boolean var1);

        public B normalizePath(Boolean var1);

        public B awsCredentials(AwsCredentials var1);

        public B signingName(String var1);

        public B signingRegion(Region var1);

        public B timeOffset(Integer var1);

        public B signingClockOverride(Clock var1);

        public B checksumParams(SignerChecksumParams var1);

        public Aws4SignerParams build();
    }
}

