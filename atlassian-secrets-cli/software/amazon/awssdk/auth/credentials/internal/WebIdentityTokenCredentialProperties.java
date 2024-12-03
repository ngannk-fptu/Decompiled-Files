/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.nio.file.Path;
import java.time.Duration;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public class WebIdentityTokenCredentialProperties {
    private final String roleArn;
    private final String roleSessionName;
    private final Path webIdentityTokenFile;
    private final Boolean asyncCredentialUpdateEnabled;
    private final Duration prefetchTime;
    private final Duration staleTime;
    private final Duration roleSessionDuration;

    private WebIdentityTokenCredentialProperties(Builder builder) {
        this.roleArn = builder.roleArn;
        this.roleSessionName = builder.roleSessionName;
        this.webIdentityTokenFile = builder.webIdentityTokenFile;
        this.asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled;
        this.prefetchTime = builder.prefetchTime;
        this.staleTime = builder.staleTime;
        this.roleSessionDuration = builder.roleSessionDuration;
    }

    public String roleArn() {
        return this.roleArn;
    }

    public String roleSessionName() {
        return this.roleSessionName;
    }

    public Path webIdentityTokenFile() {
        return this.webIdentityTokenFile;
    }

    public Boolean asyncCredentialUpdateEnabled() {
        return this.asyncCredentialUpdateEnabled;
    }

    public Duration prefetchTime() {
        return this.prefetchTime;
    }

    public Duration staleTime() {
        return this.staleTime;
    }

    public Duration roleSessionDuration() {
        return this.roleSessionDuration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String roleArn;
        private String roleSessionName;
        private Path webIdentityTokenFile;
        private Boolean asyncCredentialUpdateEnabled;
        private Duration prefetchTime;
        private Duration staleTime;
        private Duration roleSessionDuration;

        public Builder roleArn(String roleArn) {
            this.roleArn = roleArn;
            return this;
        }

        public Builder roleSessionName(String roleSessionName) {
            this.roleSessionName = roleSessionName;
            return this;
        }

        public Builder webIdentityTokenFile(Path webIdentityTokenFile) {
            this.webIdentityTokenFile = webIdentityTokenFile;
            return this;
        }

        public Builder asyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return this;
        }

        public Builder prefetchTime(Duration prefetchTime) {
            this.prefetchTime = prefetchTime;
            return this;
        }

        public Builder staleTime(Duration staleTime) {
            this.staleTime = staleTime;
            return this;
        }

        public Builder roleSessionDuration(Duration roleSessionDuration) {
            this.roleSessionDuration = roleSessionDuration;
            return this;
        }

        public WebIdentityTokenCredentialProperties build() {
            return new WebIdentityTokenCredentialProperties(this);
        }
    }
}

