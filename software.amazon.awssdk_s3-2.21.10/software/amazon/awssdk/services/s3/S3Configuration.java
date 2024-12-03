/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.core.ServiceConfiguration
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSupplier
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.ServiceConfiguration;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSupplier;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.services.s3.internal.FieldWithDefault;
import software.amazon.awssdk.services.s3.internal.settingproviders.DisableMultiRegionProviderChain;
import software.amazon.awssdk.services.s3.internal.settingproviders.UseArnRegionProviderChain;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class S3Configuration
implements ServiceConfiguration,
ToCopyableBuilder<Builder, S3Configuration> {
    private static final boolean DEFAULT_PATH_STYLE_ACCESS_ENABLED = false;
    private static final boolean DEFAULT_ACCELERATE_MODE_ENABLED = false;
    private static final boolean DEFAULT_DUALSTACK_ENABLED = false;
    private static final boolean DEFAULT_CHECKSUM_VALIDATION_ENABLED = true;
    private static final boolean DEFAULT_CHUNKED_ENCODING_ENABLED = true;
    private final FieldWithDefault<Boolean> pathStyleAccessEnabled;
    private final FieldWithDefault<Boolean> accelerateModeEnabled;
    private final FieldWithDefault<Boolean> dualstackEnabled;
    private final FieldWithDefault<Boolean> checksumValidationEnabled;
    private final FieldWithDefault<Boolean> chunkedEncodingEnabled;
    private final Boolean useArnRegionEnabled;
    private final Boolean multiRegionEnabled;
    private final FieldWithDefault<Supplier<ProfileFile>> profileFile;
    private final FieldWithDefault<String> profileName;

    private S3Configuration(DefaultS3ServiceConfigurationBuilder builder) {
        this.dualstackEnabled = FieldWithDefault.create(builder.dualstackEnabled, false);
        this.accelerateModeEnabled = FieldWithDefault.create(builder.accelerateModeEnabled, false);
        this.pathStyleAccessEnabled = FieldWithDefault.create(builder.pathStyleAccessEnabled, false);
        this.checksumValidationEnabled = FieldWithDefault.create(builder.checksumValidationEnabled, true);
        this.chunkedEncodingEnabled = FieldWithDefault.create(builder.chunkedEncodingEnabled, true);
        this.profileFile = FieldWithDefault.create(builder.profileFile, ProfileFile::defaultProfileFile);
        this.profileName = FieldWithDefault.create(builder.profileName, ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow());
        this.useArnRegionEnabled = builder.useArnRegionEnabled;
        this.multiRegionEnabled = builder.multiRegionEnabled;
        if (this.accelerateModeEnabled() && this.pathStyleAccessEnabled()) {
            throw new IllegalArgumentException("Accelerate mode cannot be used with path style addressing");
        }
    }

    private boolean resolveUseArnRegionEnabled() {
        return UseArnRegionProviderChain.create(this.profileFile.value(), this.profileName.value()).resolveUseArnRegion().orElse(false);
    }

    private boolean resolveMultiRegionEnabled() {
        return DisableMultiRegionProviderChain.create(this.profileFile.value(), this.profileName.value()).resolve().orElse(false) == false;
    }

    public static Builder builder() {
        return new DefaultS3ServiceConfigurationBuilder();
    }

    public boolean pathStyleAccessEnabled() {
        return this.pathStyleAccessEnabled.value();
    }

    public boolean accelerateModeEnabled() {
        return this.accelerateModeEnabled.value();
    }

    public boolean dualstackEnabled() {
        return this.dualstackEnabled.value();
    }

    public boolean checksumValidationEnabled() {
        return this.checksumValidationEnabled.value();
    }

    public boolean chunkedEncodingEnabled() {
        return this.chunkedEncodingEnabled.value();
    }

    public boolean useArnRegionEnabled() {
        return Optional.ofNullable(this.useArnRegionEnabled).orElseGet(this::resolveUseArnRegionEnabled);
    }

    public boolean multiRegionEnabled() {
        return Optional.ofNullable(this.multiRegionEnabled).orElseGet(this::resolveMultiRegionEnabled);
    }

    public Builder toBuilder() {
        return S3Configuration.builder().dualstackEnabled(this.dualstackEnabled.valueOrNullIfDefault()).multiRegionEnabled(this.multiRegionEnabled).accelerateModeEnabled(this.accelerateModeEnabled.valueOrNullIfDefault()).pathStyleAccessEnabled(this.pathStyleAccessEnabled.valueOrNullIfDefault()).checksumValidationEnabled(this.checksumValidationEnabled.valueOrNullIfDefault()).chunkedEncodingEnabled(this.chunkedEncodingEnabled.valueOrNullIfDefault()).useArnRegionEnabled(this.useArnRegionEnabled).profileFile(this.profileFile.valueOrNullIfDefault()).profileName(this.profileName.valueOrNullIfDefault());
    }

    static final class DefaultS3ServiceConfigurationBuilder
    implements Builder {
        private Boolean dualstackEnabled;
        private Boolean accelerateModeEnabled;
        private Boolean pathStyleAccessEnabled;
        private Boolean checksumValidationEnabled;
        private Boolean chunkedEncodingEnabled;
        private Boolean useArnRegionEnabled;
        private Boolean multiRegionEnabled;
        private Supplier<ProfileFile> profileFile;
        private String profileName;

        DefaultS3ServiceConfigurationBuilder() {
        }

        @Override
        public Boolean dualstackEnabled() {
            return this.dualstackEnabled;
        }

        @Override
        public Builder dualstackEnabled(Boolean dualstackEnabled) {
            this.dualstackEnabled = dualstackEnabled;
            return this;
        }

        @Override
        public Boolean accelerateModeEnabled() {
            return this.accelerateModeEnabled;
        }

        public void setDualstackEnabled(Boolean dualstackEnabled) {
            this.dualstackEnabled(dualstackEnabled);
        }

        @Override
        public Builder accelerateModeEnabled(Boolean accelerateModeEnabled) {
            this.accelerateModeEnabled = accelerateModeEnabled;
            return this;
        }

        @Override
        public Boolean pathStyleAccessEnabled() {
            return this.pathStyleAccessEnabled;
        }

        public void setAccelerateModeEnabled(Boolean accelerateModeEnabled) {
            this.accelerateModeEnabled(accelerateModeEnabled);
        }

        @Override
        public Builder pathStyleAccessEnabled(Boolean pathStyleAccessEnabled) {
            this.pathStyleAccessEnabled = pathStyleAccessEnabled;
            return this;
        }

        @Override
        public Boolean checksumValidationEnabled() {
            return this.checksumValidationEnabled;
        }

        public void setPathStyleAccessEnabled(Boolean pathStyleAccessEnabled) {
            this.pathStyleAccessEnabled(pathStyleAccessEnabled);
        }

        @Override
        public Builder checksumValidationEnabled(Boolean checksumValidationEnabled) {
            this.checksumValidationEnabled = checksumValidationEnabled;
            return this;
        }

        @Override
        public Boolean chunkedEncodingEnabled() {
            return this.chunkedEncodingEnabled;
        }

        public void setChecksumValidationEnabled(Boolean checksumValidationEnabled) {
            this.checksumValidationEnabled(checksumValidationEnabled);
        }

        @Override
        public Builder chunkedEncodingEnabled(Boolean chunkedEncodingEnabled) {
            this.chunkedEncodingEnabled = chunkedEncodingEnabled;
            return this;
        }

        @Override
        public Boolean useArnRegionEnabled() {
            return this.useArnRegionEnabled;
        }

        public void setChunkedEncodingEnabled(Boolean chunkedEncodingEnabled) {
            this.chunkedEncodingEnabled(chunkedEncodingEnabled);
        }

        @Override
        public Builder useArnRegionEnabled(Boolean useArnRegionEnabled) {
            this.useArnRegionEnabled = useArnRegionEnabled;
            return this;
        }

        @Override
        public Boolean multiRegionEnabled() {
            return this.multiRegionEnabled;
        }

        @Override
        public Builder multiRegionEnabled(Boolean multiRegionEnabled) {
            this.multiRegionEnabled = multiRegionEnabled;
            return this;
        }

        @Override
        public ProfileFile profileFile() {
            return Optional.ofNullable(this.profileFile).map(Supplier::get).orElse(null);
        }

        @Override
        public Builder profileFile(ProfileFile profileFile) {
            return this.profileFile((Supplier<ProfileFile>)Optional.ofNullable(profileFile).map(ProfileFileSupplier::fixedProfileFile).orElse(null));
        }

        @Override
        public Supplier<ProfileFile> profileFileSupplier() {
            return this.profileFile;
        }

        @Override
        public Builder profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        @Override
        public String profileName() {
            return this.profileName;
        }

        @Override
        public Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public void setUseArnRegionEnabled(Boolean useArnRegionEnabled) {
            this.useArnRegionEnabled(useArnRegionEnabled);
        }

        public S3Configuration build() {
            return new S3Configuration(this);
        }
    }

    @NotThreadSafe
    public static interface Builder
    extends CopyableBuilder<Builder, S3Configuration> {
        public Boolean dualstackEnabled();

        @Deprecated
        public Builder dualstackEnabled(Boolean var1);

        public Boolean accelerateModeEnabled();

        public Builder accelerateModeEnabled(Boolean var1);

        public Boolean pathStyleAccessEnabled();

        public Builder pathStyleAccessEnabled(Boolean var1);

        public Boolean checksumValidationEnabled();

        public Builder checksumValidationEnabled(Boolean var1);

        public Boolean chunkedEncodingEnabled();

        public Builder chunkedEncodingEnabled(Boolean var1);

        public Boolean useArnRegionEnabled();

        public Builder useArnRegionEnabled(Boolean var1);

        public Boolean multiRegionEnabled();

        public Builder multiRegionEnabled(Boolean var1);

        public ProfileFile profileFile();

        public Builder profileFile(ProfileFile var1);

        public Supplier<ProfileFile> profileFileSupplier();

        public Builder profileFile(Supplier<ProfileFile> var1);

        public String profileName();

        public Builder profileName(String var1);
    }
}

