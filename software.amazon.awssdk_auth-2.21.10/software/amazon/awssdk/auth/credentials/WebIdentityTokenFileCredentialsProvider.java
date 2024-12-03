/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.auth.credentials;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityCredentialsUtils;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public class WebIdentityTokenFileCredentialsProvider
implements AwsCredentialsProvider,
SdkAutoCloseable,
ToCopyableBuilder<Builder, WebIdentityTokenFileCredentialsProvider> {
    private static final Logger log = Logger.loggerFor(WebIdentityTokenFileCredentialsProvider.class);
    private final AwsCredentialsProvider credentialsProvider;
    private final RuntimeException loadException;
    private final String roleArn;
    private final String roleSessionName;
    private final Path webIdentityTokenFile;
    private final Boolean asyncCredentialUpdateEnabled;
    private final Duration prefetchTime;
    private final Duration staleTime;
    private final Duration roleSessionDuration;

    private WebIdentityTokenFileCredentialsProvider(BuilderImpl builder) {
        AwsCredentialsProvider credentialsProvider = null;
        RuntimeException loadException = null;
        String roleArn = null;
        String roleSessionName = null;
        Path webIdentityTokenFile = null;
        Boolean asyncCredentialUpdateEnabled = null;
        Duration prefetchTime = null;
        Duration staleTime = null;
        Duration roleSessionDuration = null;
        try {
            webIdentityTokenFile = builder.webIdentityTokenFile != null ? builder.webIdentityTokenFile : Paths.get(StringUtils.trim((String)SdkSystemSetting.AWS_WEB_IDENTITY_TOKEN_FILE.getStringValueOrThrow()), new String[0]);
            roleArn = builder.roleArn != null ? builder.roleArn : StringUtils.trim((String)SdkSystemSetting.AWS_ROLE_ARN.getStringValueOrThrow());
            roleSessionName = builder.roleSessionName != null ? builder.roleSessionName : (String)SdkSystemSetting.AWS_ROLE_SESSION_NAME.getStringValue().orElse(null);
            asyncCredentialUpdateEnabled = builder.asyncCredentialUpdateEnabled != null ? builder.asyncCredentialUpdateEnabled : false;
            prefetchTime = builder.prefetchTime;
            staleTime = builder.staleTime;
            roleSessionDuration = builder.roleSessionDuration;
            WebIdentityTokenCredentialProperties credentialProperties = WebIdentityTokenCredentialProperties.builder().roleArn(roleArn).roleSessionName(roleSessionName).webIdentityTokenFile(webIdentityTokenFile).asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled).prefetchTime(prefetchTime).staleTime(staleTime).roleSessionDuration(roleSessionDuration).build();
            credentialsProvider = WebIdentityCredentialsUtils.factory().create(credentialProperties);
        }
        catch (RuntimeException e) {
            loadException = e;
        }
        this.loadException = loadException;
        this.credentialsProvider = credentialsProvider;
        this.roleArn = roleArn;
        this.roleSessionName = roleSessionName;
        this.webIdentityTokenFile = webIdentityTokenFile;
        this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
        this.prefetchTime = prefetchTime;
        this.staleTime = staleTime;
        this.roleSessionDuration = roleSessionDuration;
    }

    public static WebIdentityTokenFileCredentialsProvider create() {
        return WebIdentityTokenFileCredentialsProvider.builder().build();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        if (this.loadException != null) {
            throw this.loadException;
        }
        return this.credentialsProvider.resolveCredentials();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public String toString() {
        return ToString.create((String)"WebIdentityTokenCredentialsProvider");
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public void close() {
        IoUtils.closeIfCloseable((Object)this.credentialsProvider, null);
    }

    static final class BuilderImpl
    implements Builder {
        private String roleArn;
        private String roleSessionName;
        private Path webIdentityTokenFile;
        private Boolean asyncCredentialUpdateEnabled;
        private Duration prefetchTime;
        private Duration staleTime;
        private Duration roleSessionDuration;

        BuilderImpl() {
        }

        private BuilderImpl(WebIdentityTokenFileCredentialsProvider provider) {
            this.roleArn = provider.roleArn;
            this.roleSessionName = provider.roleSessionName;
            this.webIdentityTokenFile = provider.webIdentityTokenFile;
            this.asyncCredentialUpdateEnabled = provider.asyncCredentialUpdateEnabled;
            this.prefetchTime = provider.prefetchTime;
            this.staleTime = provider.staleTime;
            this.roleSessionDuration = provider.roleSessionDuration;
        }

        @Override
        public Builder roleArn(String roleArn) {
            this.roleArn = roleArn;
            return this;
        }

        public void setRoleArn(String roleArn) {
            this.roleArn(roleArn);
        }

        @Override
        public Builder roleSessionName(String roleSessionName) {
            this.roleSessionName = roleSessionName;
            return this;
        }

        public void setRoleSessionName(String roleSessionName) {
            this.roleSessionName(roleSessionName);
        }

        @Override
        public Builder webIdentityTokenFile(Path webIdentityTokenFile) {
            this.webIdentityTokenFile = webIdentityTokenFile;
            return this;
        }

        public void setWebIdentityTokenFile(Path webIdentityTokenFile) {
            this.webIdentityTokenFile(webIdentityTokenFile);
        }

        @Override
        public Builder asyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled = asyncCredentialUpdateEnabled;
            return this;
        }

        public void setAsyncCredentialUpdateEnabled(Boolean asyncCredentialUpdateEnabled) {
            this.asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled);
        }

        @Override
        public Builder prefetchTime(Duration prefetchTime) {
            this.prefetchTime = prefetchTime;
            return this;
        }

        public void setPrefetchTime(Duration prefetchTime) {
            this.prefetchTime(prefetchTime);
        }

        @Override
        public Builder staleTime(Duration staleTime) {
            this.staleTime = staleTime;
            return this;
        }

        public void setStaleTime(Duration staleTime) {
            this.staleTime(staleTime);
        }

        @Override
        public Builder roleSessionDuration(Duration sessionDuration) {
            this.roleSessionDuration = sessionDuration;
            return this;
        }

        public void setRoleSessionDuration(Duration roleSessionDuration) {
            this.roleSessionDuration(roleSessionDuration);
        }

        @Override
        public WebIdentityTokenFileCredentialsProvider build() {
            return new WebIdentityTokenFileCredentialsProvider(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, WebIdentityTokenFileCredentialsProvider> {
        public Builder roleArn(String var1);

        public Builder roleSessionName(String var1);

        public Builder webIdentityTokenFile(Path var1);

        public Builder asyncCredentialUpdateEnabled(Boolean var1);

        public Builder prefetchTime(Duration var1);

        public Builder staleTime(Duration var1);

        public Builder roleSessionDuration(Duration var1);

        public WebIdentityTokenFileCredentialsProvider build();
    }
}

