/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.awscore.endpoint;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class FipsEnabledProvider {
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private FipsEnabledProvider(Builder builder) {
        this.profileFile = (Supplier)Validate.paramNotNull((Object)builder.profileFile, (String)"profileFile");
        this.profileName = builder.profileName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Boolean> isFipsEnabled() {
        Optional setting = SdkSystemSetting.AWS_USE_FIPS_ENDPOINT.getBooleanValue();
        if (setting.isPresent()) {
            return setting;
        }
        return this.profileFile.get().profile(this.profileName()).flatMap(p -> p.booleanProperty("use_fips_endpoint"));
    }

    private String profileName() {
        return this.profileName != null ? this.profileName : ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
    }

    public static final class Builder {
        private Supplier<ProfileFile> profileFile = ProfileFile::defaultProfileFile;
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

        public FipsEnabledProvider build() {
            return new FipsEnabledProvider(this);
        }
    }
}

