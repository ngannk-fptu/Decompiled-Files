/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.utils.AttributeMap;

@SdkPublicApi
public final class ServiceMetadataConfiguration {
    private final Supplier<ProfileFile> profileFile;
    private final String profileName;
    private final AttributeMap advancedOptions;

    private ServiceMetadataConfiguration(Builder builder) {
        this.profileFile = builder.profileFile;
        this.profileName = builder.profileName;
        this.advancedOptions = builder.advancedOptions.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Supplier<ProfileFile> profileFile() {
        return this.profileFile;
    }

    public String profileName() {
        return this.profileName;
    }

    public <T> Optional<T> advancedOption(ServiceMetadataAdvancedOption<T> option) {
        return Optional.ofNullable(this.advancedOptions.get(option));
    }

    public static final class Builder {
        private Supplier<ProfileFile> profileFile;
        private String profileName;
        private AttributeMap.Builder advancedOptions = AttributeMap.builder();

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

        public <T> Builder putAdvancedOption(ServiceMetadataAdvancedOption<T> option, T value) {
            this.advancedOptions.put(option, value);
            return this;
        }

        public Builder advancedOptions(Map<ServiceMetadataAdvancedOption<?>, ?> advancedOptions) {
            this.advancedOptions.putAll(advancedOptions);
            return this;
        }

        public ServiceMetadataConfiguration build() {
            return new ServiceMetadataConfiguration(this);
        }
    }
}

