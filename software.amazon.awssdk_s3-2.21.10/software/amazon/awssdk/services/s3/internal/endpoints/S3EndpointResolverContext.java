/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.internal.endpoints;

import java.net.URI;
import java.util.Objects;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;

@SdkInternalApi
public final class S3EndpointResolverContext {
    private final SdkHttpRequest request;
    private final SdkRequest originalRequest;
    private final Region region;
    private final S3Configuration serviceConfiguration;
    private final URI endpointOverride;
    private final boolean disableHostPrefixInjection;
    private final boolean fipsEnabled;

    private S3EndpointResolverContext(Builder builder) {
        this.request = builder.request;
        this.originalRequest = builder.originalRequest;
        this.region = builder.region;
        this.serviceConfiguration = builder.serviceConfiguration;
        this.endpointOverride = builder.endpointOverride;
        this.disableHostPrefixInjection = builder.disableHostPrefixInjection;
        this.fipsEnabled = builder.fipsEnabled != null ? builder.fipsEnabled : false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SdkHttpRequest request() {
        return this.request;
    }

    public SdkRequest originalRequest() {
        return this.originalRequest;
    }

    public Region region() {
        return this.region;
    }

    public S3Configuration serviceConfiguration() {
        return this.serviceConfiguration;
    }

    public boolean fipsEnabled() {
        return this.fipsEnabled;
    }

    public URI endpointOverride() {
        return this.endpointOverride;
    }

    public boolean isDisableHostPrefixInjection() {
        return this.disableHostPrefixInjection;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3EndpointResolverContext that = (S3EndpointResolverContext)o;
        return Objects.equals(this.endpointOverride, that.endpointOverride) && Objects.equals(this.request, that.request) && Objects.equals(this.originalRequest, that.originalRequest) && Objects.equals(this.region, that.region) && Objects.equals(this.serviceConfiguration, that.serviceConfiguration) && this.disableHostPrefixInjection == that.disableHostPrefixInjection;
    }

    public int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.request());
        hashCode = 31 * hashCode + Objects.hashCode(this.originalRequest());
        hashCode = 31 * hashCode + Objects.hashCode(this.region());
        hashCode = 31 * hashCode + Objects.hashCode(this.serviceConfiguration());
        hashCode = 31 * hashCode + Objects.hashCode(this.endpointOverride());
        hashCode = 31 * hashCode + Objects.hashCode(this.isDisableHostPrefixInjection());
        hashCode = 31 * hashCode + Boolean.hashCode(this.fipsEnabled());
        return hashCode;
    }

    public Builder toBuilder() {
        return S3EndpointResolverContext.builder().endpointOverride(this.endpointOverride).request(this.request).originalRequest(this.originalRequest).region(this.region).serviceConfiguration(this.serviceConfiguration).fipsEnabled(this.fipsEnabled);
    }

    public static final class Builder {
        private SdkHttpRequest request;
        private SdkRequest originalRequest;
        private Region region;
        private S3Configuration serviceConfiguration;
        private URI endpointOverride;
        private boolean disableHostPrefixInjection;
        private Boolean fipsEnabled;
        private Supplier<ProfileFile> profileFile;
        private String profileName;

        private Builder() {
        }

        public Builder request(SdkHttpRequest request) {
            this.request = request;
            return this;
        }

        public Builder originalRequest(SdkRequest originalRequest) {
            this.originalRequest = originalRequest;
            return this;
        }

        public Builder region(Region region) {
            this.region = region;
            return this;
        }

        public Builder serviceConfiguration(S3Configuration serviceConfiguration) {
            this.serviceConfiguration = serviceConfiguration;
            return this;
        }

        public Builder endpointOverride(URI endpointOverride) {
            this.endpointOverride = endpointOverride;
            return this;
        }

        public Builder disableHostPrefixInjection(boolean disableHostPrefixInjection) {
            this.disableHostPrefixInjection = disableHostPrefixInjection;
            return this;
        }

        public Builder fipsEnabled(Boolean fipsEnabled) {
            this.fipsEnabled = fipsEnabled;
            return this;
        }

        public S3EndpointResolverContext build() {
            return new S3EndpointResolverContext(this);
        }
    }
}

