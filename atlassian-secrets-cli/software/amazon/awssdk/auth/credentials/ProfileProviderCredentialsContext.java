/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;

@SdkProtectedApi
public final class ProfileProviderCredentialsContext {
    private final Profile profile;
    private final ProfileFile profileFile;

    private ProfileProviderCredentialsContext(Profile profile2, ProfileFile profileFile) {
        this.profile = profile2;
        this.profileFile = profileFile;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Profile profile() {
        return this.profile;
    }

    public ProfileFile profileFile() {
        return this.profileFile;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProfileProviderCredentialsContext that = (ProfileProviderCredentialsContext)o;
        return Objects.equals(this.profile, that.profile) && Objects.equals(this.profileFile, that.profileFile);
    }

    public int hashCode() {
        int result = this.profile != null ? this.profile.hashCode() : 0;
        result = 31 * result + (this.profileFile != null ? this.profileFile.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private Profile profile;
        private ProfileFile profileFile;

        private Builder() {
        }

        public Builder profile(Profile profile2) {
            this.profile = profile2;
            return this;
        }

        public Builder profileFile(ProfileFile profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public ProfileProviderCredentialsContext build() {
            return new ProfileProviderCredentialsContext(this.profile, this.profileFile);
        }
    }
}

