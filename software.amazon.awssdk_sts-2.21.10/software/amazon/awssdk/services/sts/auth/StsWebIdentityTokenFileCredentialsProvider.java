/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentials
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.AwsSessionCredentials
 *  software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.auth;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleWithWebIdentityCredentialsProvider;
import software.amazon.awssdk.services.sts.auth.StsCredentialsProvider;
import software.amazon.awssdk.services.sts.internal.AssumeRoleWithWebIdentityRequestSupplier;
import software.amazon.awssdk.services.sts.internal.StsAuthUtils;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class StsWebIdentityTokenFileCredentialsProvider
extends StsCredentialsProvider
implements ToCopyableBuilder<Builder, StsWebIdentityTokenFileCredentialsProvider> {
    private final AwsCredentialsProvider credentialsProvider;
    private final RuntimeException loadException;
    private final Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequest;
    private final Path webIdentityTokenFile;
    private final String roleArn;
    private final String roleSessionName;
    private final Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequestFromBuilder;

    private StsWebIdentityTokenFileCredentialsProvider(Builder builder) {
        super(builder, "sts-assume-role-with-web-identity-credentials-provider");
        Path webIdentityTokenFile = builder.webIdentityTokenFile != null ? builder.webIdentityTokenFile : Paths.get(StringUtils.trim((String)SdkSystemSetting.AWS_WEB_IDENTITY_TOKEN_FILE.getStringValueOrThrow()), new String[0]);
        String roleArn = builder.roleArn != null ? builder.roleArn : StringUtils.trim((String)SdkSystemSetting.AWS_ROLE_ARN.getStringValueOrThrow());
        String sessionName = builder.roleSessionName != null ? builder.roleSessionName : SdkSystemSetting.AWS_ROLE_SESSION_NAME.getStringValue().orElse("aws-sdk-java-" + System.currentTimeMillis());
        WebIdentityTokenCredentialProperties credentialProperties = WebIdentityTokenCredentialProperties.builder().roleArn(roleArn).roleSessionName(builder.roleSessionName).webIdentityTokenFile(webIdentityTokenFile).build();
        this.assumeRoleWithWebIdentityRequest = builder.assumeRoleWithWebIdentityRequestSupplier != null ? builder.assumeRoleWithWebIdentityRequestSupplier : () -> (AssumeRoleWithWebIdentityRequest)((Object)((Object)AssumeRoleWithWebIdentityRequest.builder().roleArn(credentialProperties.roleArn()).roleSessionName(sessionName).build()));
        StsAssumeRoleWithWebIdentityCredentialsProvider credentialsProviderLocal = null;
        RuntimeException loadExceptionLocal = null;
        try {
            AssumeRoleWithWebIdentityRequestSupplier supplier = AssumeRoleWithWebIdentityRequestSupplier.builder().assumeRoleWithWebIdentityRequest(this.assumeRoleWithWebIdentityRequest.get()).webIdentityTokenFile(credentialProperties.webIdentityTokenFile()).build();
            credentialsProviderLocal = ((StsAssumeRoleWithWebIdentityCredentialsProvider.Builder)StsAssumeRoleWithWebIdentityCredentialsProvider.builder().stsClient(builder.stsClient)).refreshRequest(supplier).build();
        }
        catch (RuntimeException e) {
            loadExceptionLocal = e;
        }
        this.loadException = loadExceptionLocal;
        this.credentialsProvider = credentialsProviderLocal;
        this.webIdentityTokenFile = builder.webIdentityTokenFile;
        this.roleArn = builder.roleArn;
        this.roleSessionName = builder.roleSessionName;
        this.assumeRoleWithWebIdentityRequestFromBuilder = builder.assumeRoleWithWebIdentityRequestSupplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        if (this.loadException != null) {
            throw this.loadException;
        }
        return this.credentialsProvider.resolveCredentials();
    }

    public String toString() {
        return ToString.create((String)"StsWebIdentityTokenFileCredentialsProvider");
    }

    @Override
    protected AwsSessionCredentials getUpdatedCredentials(StsClient stsClient) {
        AssumeRoleWithWebIdentityRequest request = this.assumeRoleWithWebIdentityRequest.get();
        Validate.notNull((Object)((Object)request), (String)"AssumeRoleWithWebIdentityRequest can't be null", (Object[])new Object[0]);
        return StsAuthUtils.toAwsSessionCredentials(stsClient.assumeRoleWithWebIdentity(request).credentials());
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder
    extends StsCredentialsProvider.BaseBuilder<Builder, StsWebIdentityTokenFileCredentialsProvider> {
        private String roleArn;
        private String roleSessionName;
        private Path webIdentityTokenFile;
        private Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequestSupplier;
        private StsClient stsClient;

        private Builder() {
            super((B x$0) -> new StsWebIdentityTokenFileCredentialsProvider((Builder)x$0));
        }

        private Builder(StsWebIdentityTokenFileCredentialsProvider provider) {
            super((B x$0) -> new StsWebIdentityTokenFileCredentialsProvider((Builder)x$0));
            this.roleArn = provider.roleArn;
            this.roleSessionName = provider.roleSessionName;
            this.webIdentityTokenFile = provider.webIdentityTokenFile;
            this.assumeRoleWithWebIdentityRequestSupplier = provider.assumeRoleWithWebIdentityRequestFromBuilder;
            this.stsClient = provider.stsClient;
        }

        @Override
        public Builder stsClient(StsClient stsClient) {
            this.stsClient = stsClient;
            return (Builder)super.stsClient(stsClient);
        }

        public Builder roleArn(String roleArn) {
            this.roleArn = roleArn;
            return this;
        }

        public void setRoleArn(String roleArn) {
            this.roleArn(roleArn);
        }

        public Builder roleSessionName(String roleSessionName) {
            this.roleSessionName = roleSessionName;
            return this;
        }

        public void setRoleSessionName(String roleSessionName) {
            this.roleSessionName(roleSessionName);
        }

        public Builder webIdentityTokenFile(Path webIdentityTokenFile) {
            this.webIdentityTokenFile = webIdentityTokenFile;
            return this;
        }

        public void setWebIdentityTokenFile(Path webIdentityTokenFile) {
            this.webIdentityTokenFile(webIdentityTokenFile);
        }

        public Builder refreshRequest(AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) {
            return this.refreshRequest(() -> assumeRoleWithWebIdentityRequest);
        }

        public Builder refreshRequest(Supplier<AssumeRoleWithWebIdentityRequest> assumeRoleWithWebIdentityRequestSupplier) {
            this.assumeRoleWithWebIdentityRequestSupplier = assumeRoleWithWebIdentityRequestSupplier;
            return this;
        }

        public Builder refreshRequest(Consumer<AssumeRoleWithWebIdentityRequest.Builder> assumeRoleWithWebIdentityRequest) {
            return this.refreshRequest((AssumeRoleWithWebIdentityRequest)((Object)((AssumeRoleWithWebIdentityRequest.Builder)AssumeRoleWithWebIdentityRequest.builder().applyMutation(assumeRoleWithWebIdentityRequest)).build()));
        }

        @Override
        public StsWebIdentityTokenFileCredentialsProvider build() {
            return new StsWebIdentityTokenFileCredentialsProvider(this);
        }
    }
}

