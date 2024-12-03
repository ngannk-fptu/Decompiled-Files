/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.endpoint;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class DualstackEnabledProvider {
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private DualstackEnabledProvider(Builder builder) {
        this.profileFile = Validate.paramNotNull(builder.profileFile, "profileFile");
        this.profileName = builder.profileName;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<Boolean> isDualstackEnabled() {
        Optional<Boolean> setting = SdkSystemSetting.AWS_USE_DUALSTACK_ENDPOINT.getBooleanValue();
        if (setting.isPresent()) {
            return setting;
        }
        ProfileFile profileFile = this.profileFile.get();
        Optional<Profile> profile2 = profileFile.profile(this.profileName());
        return profile2.flatMap(p -> p.booleanProperty("use_dualstack_endpoint"));
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

        public DualstackEnabledProvider build() {
            return new DualstackEnabledProvider(this);
        }
    }
}

