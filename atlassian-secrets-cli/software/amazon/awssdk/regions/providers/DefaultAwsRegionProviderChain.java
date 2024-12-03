/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.providers;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.providers.AwsProfileRegionProvider;
import software.amazon.awssdk.regions.providers.AwsRegionProviderChain;
import software.amazon.awssdk.regions.providers.InstanceProfileRegionProvider;
import software.amazon.awssdk.regions.providers.SystemSettingsRegionProvider;

@SdkProtectedApi
public final class DefaultAwsRegionProviderChain
extends AwsRegionProviderChain {
    public DefaultAwsRegionProviderChain() {
        super(new SystemSettingsRegionProvider(), new AwsProfileRegionProvider(), new InstanceProfileRegionProvider());
    }

    private DefaultAwsRegionProviderChain(Builder builder) {
        super(new SystemSettingsRegionProvider(), new AwsProfileRegionProvider(builder.profileFile, builder.profileName), new InstanceProfileRegionProvider());
    }

    public static Builder builder() {
        return new Builder();
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

        public DefaultAwsRegionProviderChain build() {
            return new DefaultAwsRegionProviderChain(this);
        }
    }
}

