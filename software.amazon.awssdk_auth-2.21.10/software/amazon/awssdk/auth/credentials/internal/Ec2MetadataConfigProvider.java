/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.profiles.Profile
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;

@SdkInternalApi
public final class Ec2MetadataConfigProvider {
    private static final String EC2_METADATA_SERVICE_URL_IPV4 = "http://169.254.169.254";
    private static final String EC2_METADATA_SERVICE_URL_IPV6 = "http://[fd00:ec2::254]";
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;

    private Ec2MetadataConfigProvider(Builder builder) {
        this.profileFile = builder.profileFile;
        this.profileName = builder.profileName;
    }

    public String getEndpoint() {
        String endpointOverride = this.getEndpointOverride();
        if (endpointOverride != null) {
            return endpointOverride;
        }
        EndpointMode endpointMode = this.getEndpointMode();
        switch (endpointMode) {
            case IPV4: {
                return EC2_METADATA_SERVICE_URL_IPV4;
            }
            case IPV6: {
                return EC2_METADATA_SERVICE_URL_IPV6;
            }
        }
        throw SdkClientException.create((String)("Unknown endpoint mode: " + (Object)((Object)endpointMode)));
    }

    public EndpointMode getEndpointMode() {
        Optional endpointMode = SdkSystemSetting.AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE.getNonDefaultStringValue();
        if (endpointMode.isPresent()) {
            return EndpointMode.fromValue((String)endpointMode.get());
        }
        return this.configFileEndpointMode().orElseGet(() -> EndpointMode.fromValue(SdkSystemSetting.AWS_EC2_METADATA_SERVICE_ENDPOINT_MODE.defaultValue()));
    }

    public String getEndpointOverride() {
        Optional endpointOverride = SdkSystemSetting.AWS_EC2_METADATA_SERVICE_ENDPOINT.getNonDefaultStringValue();
        if (endpointOverride.isPresent()) {
            return (String)endpointOverride.get();
        }
        Optional<String> configFileValue = this.configFileEndpointOverride();
        return configFileValue.orElse(null);
    }

    public static Builder builder() {
        return new Builder();
    }

    private Optional<EndpointMode> configFileEndpointMode() {
        return this.resolveProfile().flatMap(p -> p.property("ec2_metadata_service_endpoint_mode")).map(EndpointMode::fromValue);
    }

    private Optional<String> configFileEndpointOverride() {
        return this.resolveProfile().flatMap(p -> p.property("ec2_metadata_service_endpoint"));
    }

    private Optional<Profile> resolveProfile() {
        ProfileFile profileFileToUse = this.resolveProfileFile();
        String profileNameToUse = this.resolveProfileName();
        return profileFileToUse.profile(profileNameToUse);
    }

    private ProfileFile resolveProfileFile() {
        if (this.profileFile != null) {
            return this.profileFile.get();
        }
        return ProfileFile.defaultProfileFile();
    }

    private String resolveProfileName() {
        if (this.profileName != null) {
            return this.profileName;
        }
        return ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
    }

    public static class Builder {
        private Supplier<ProfileFile> profileFile;
        private String profileName;

        public Builder profileFile(Supplier<ProfileFile> profileFile) {
            this.profileFile = profileFile;
            return this;
        }

        public Builder profileName(String profileName) {
            this.profileName = profileName;
            return this;
        }

        public Ec2MetadataConfigProvider build() {
            return new Ec2MetadataConfigProvider(this);
        }
    }

    public static enum EndpointMode {
        IPV4,
        IPV6;


        public static EndpointMode fromValue(String s) {
            if (s == null) {
                return null;
            }
            for (EndpointMode value : EndpointMode.values()) {
                if (!value.name().equalsIgnoreCase(s)) continue;
                return value;
            }
            throw new IllegalArgumentException("Unrecognized value for endpoint mode: " + s);
        }
    }
}

