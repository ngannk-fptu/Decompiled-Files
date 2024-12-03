/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.ToString
 */
package software.amazon.awssdk.auth.token.credentials.aws;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.token.credentials.ProfileTokenProvider;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProvider;
import software.amazon.awssdk.auth.token.credentials.SdkTokenProviderChain;
import software.amazon.awssdk.auth.token.internal.LazyTokenProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.ToString;

@SdkPublicApi
public final class DefaultAwsTokenProvider
implements SdkTokenProvider,
SdkAutoCloseable {
    private static final DefaultAwsTokenProvider DEFAULT_TOKEN_PROVIDER = new DefaultAwsTokenProvider(DefaultAwsTokenProvider.builder());
    private final LazyTokenProvider providerChain;

    private DefaultAwsTokenProvider(Builder builder) {
        this.providerChain = DefaultAwsTokenProvider.createChain(builder);
    }

    public static DefaultAwsTokenProvider create() {
        return DEFAULT_TOKEN_PROVIDER;
    }

    private static LazyTokenProvider createChain(Builder builder) {
        return LazyTokenProvider.create(() -> SdkTokenProviderChain.of(new SdkTokenProvider[]{ProfileTokenProvider.builder().profileFile(builder.profileFile).profileName(builder.profileName).build()}));
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SdkToken resolveToken() {
        return this.providerChain.resolveToken();
    }

    public void close() {
        this.providerChain.close();
    }

    public String toString() {
        return ToString.builder((String)"DefaultAwsTokenProvider").add("providerChain", (Object)this.providerChain).build();
    }

    public static final class Builder {
        private Supplier<ProfileFile> profileFile;
        private String profileName;

        private Builder() {
        }

        public Builder profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public DefaultAwsTokenProvider build() {
            return new DefaultAwsTokenProvider(this);
        }
    }
}

