/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.internal;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkInternalApi
public class ConfiguredS3SdkHttpRequest
implements ToCopyableBuilder<Builder, ConfiguredS3SdkHttpRequest> {
    private final SdkHttpRequest sdkHttpRequest;
    private final Region signingRegionModification;
    private final String signingServiceModification;

    private ConfiguredS3SdkHttpRequest(Builder builder) {
        this.sdkHttpRequest = (SdkHttpRequest)Validate.notNull((Object)builder.sdkHttpRequest, (String)"sdkHttpRequest", (Object[])new Object[0]);
        this.signingRegionModification = builder.signingRegionModification;
        this.signingServiceModification = builder.signingServiceModification;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SdkHttpRequest sdkHttpRequest() {
        return this.sdkHttpRequest;
    }

    public Optional<Region> signingRegionModification() {
        return Optional.ofNullable(this.signingRegionModification);
    }

    public Optional<String> signingServiceModification() {
        return Optional.ofNullable(this.signingServiceModification);
    }

    public Builder toBuilder() {
        return ConfiguredS3SdkHttpRequest.builder().sdkHttpRequest(this.sdkHttpRequest).signingRegionModification(this.signingRegionModification).signingServiceModification(this.signingServiceModification);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfiguredS3SdkHttpRequest that = (ConfiguredS3SdkHttpRequest)o;
        if (!this.sdkHttpRequest.equals(that.sdkHttpRequest)) {
            return false;
        }
        if (this.signingRegionModification != null ? !this.signingRegionModification.equals(that.signingRegionModification) : that.signingRegionModification != null) {
            return false;
        }
        return this.signingServiceModification != null ? this.signingServiceModification.equals(that.signingServiceModification) : that.signingServiceModification == null;
    }

    public int hashCode() {
        int result = this.sdkHttpRequest.hashCode();
        result = 31 * result + (this.signingRegionModification != null ? this.signingRegionModification.hashCode() : 0);
        result = 31 * result + (this.signingServiceModification != null ? this.signingServiceModification.hashCode() : 0);
        return result;
    }

    public static class Builder
    implements CopyableBuilder<Builder, ConfiguredS3SdkHttpRequest> {
        private String signingServiceModification;
        private SdkHttpRequest sdkHttpRequest;
        private Region signingRegionModification;

        private Builder() {
        }

        public Builder sdkHttpRequest(SdkHttpRequest sdkHttpRequest) {
            this.sdkHttpRequest = sdkHttpRequest;
            return this;
        }

        public Builder signingRegionModification(Region signingRegionModification) {
            this.signingRegionModification = signingRegionModification;
            return this;
        }

        public Builder signingServiceModification(String signingServiceModification) {
            this.signingServiceModification = signingServiceModification;
            return this;
        }

        public ConfiguredS3SdkHttpRequest build() {
            return new ConfiguredS3SdkHttpRequest(this);
        }
    }
}

