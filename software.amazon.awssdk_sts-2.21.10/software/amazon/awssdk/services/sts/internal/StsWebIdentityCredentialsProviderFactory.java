/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.AwsCredentials
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.WebIdentityTokenCredentialsProviderFactory
 *  software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties
 *  software.amazon.awssdk.core.retry.conditions.OrRetryCondition
 *  software.amazon.awssdk.core.retry.conditions.RetryCondition
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.services.sts.internal;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenCredentialsProviderFactory;
import software.amazon.awssdk.auth.credentials.internal.WebIdentityTokenCredentialProperties;
import software.amazon.awssdk.core.retry.conditions.OrRetryCondition;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleWithWebIdentityCredentialsProvider;
import software.amazon.awssdk.services.sts.internal.AssumeRoleWithWebIdentityRequestSupplier;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.IdpCommunicationErrorException;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkProtectedApi
public final class StsWebIdentityCredentialsProviderFactory
implements WebIdentityTokenCredentialsProviderFactory {
    public AwsCredentialsProvider create(WebIdentityTokenCredentialProperties credentialProperties) {
        return new StsWebIdentityCredentialsProvider(credentialProperties);
    }

    private static final class StsWebIdentityCredentialsProvider
    implements AwsCredentialsProvider,
    SdkAutoCloseable {
        private final StsClient stsClient;
        private final StsAssumeRoleWithWebIdentityCredentialsProvider credentialsProvider;

        private StsWebIdentityCredentialsProvider(WebIdentityTokenCredentialProperties credentialProperties) {
            String roleSessionName = credentialProperties.roleSessionName();
            String sessionName = roleSessionName != null ? roleSessionName : "aws-sdk-java-" + System.currentTimeMillis();
            Boolean asyncCredentialUpdateEnabled = credentialProperties.asyncCredentialUpdateEnabled() != null ? credentialProperties.asyncCredentialUpdateEnabled() : false;
            OrRetryCondition retryCondition = OrRetryCondition.create((RetryCondition[])new RetryCondition[]{context -> context.exception() instanceof IdpCommunicationErrorException, RetryCondition.defaultRetryCondition()});
            this.stsClient = (StsClient)((StsClientBuilder)((StsClientBuilder)((StsClientBuilder)StsClient.builder().applyMutation(this::configureEndpoint)).credentialsProvider((AwsCredentialsProvider)AnonymousCredentialsProvider.create())).overrideConfiguration(o -> o.retryPolicy(r -> r.retryCondition((RetryCondition)retryCondition)))).build();
            AssumeRoleWithWebIdentityRequest.Builder requestBuilder = AssumeRoleWithWebIdentityRequest.builder().roleArn(credentialProperties.roleArn()).roleSessionName(sessionName);
            if (credentialProperties.roleSessionDuration() != null) {
                requestBuilder.durationSeconds(NumericUtils.saturatedCast((long)credentialProperties.roleSessionDuration().getSeconds()));
            }
            AssumeRoleWithWebIdentityRequestSupplier supplier = AssumeRoleWithWebIdentityRequestSupplier.builder().assumeRoleWithWebIdentityRequest((AssumeRoleWithWebIdentityRequest)((Object)requestBuilder.build())).webIdentityTokenFile(credentialProperties.webIdentityTokenFile()).build();
            StsAssumeRoleWithWebIdentityCredentialsProvider.Builder builder = ((StsAssumeRoleWithWebIdentityCredentialsProvider.Builder)((StsAssumeRoleWithWebIdentityCredentialsProvider.Builder)StsAssumeRoleWithWebIdentityCredentialsProvider.builder().asyncCredentialUpdateEnabled(asyncCredentialUpdateEnabled)).stsClient(this.stsClient)).refreshRequest(supplier);
            if (credentialProperties.prefetchTime() != null) {
                builder.prefetchTime(credentialProperties.prefetchTime());
            }
            if (credentialProperties.staleTime() != null) {
                builder.staleTime(credentialProperties.staleTime());
            }
            this.credentialsProvider = builder.build();
        }

        public AwsCredentials resolveCredentials() {
            return this.credentialsProvider.resolveCredentials();
        }

        public void close() {
            IoUtils.closeQuietly((AutoCloseable)((Object)this.credentialsProvider), null);
            IoUtils.closeQuietly((AutoCloseable)((Object)this.stsClient), null);
        }

        private void configureEndpoint(StsClientBuilder stsClientBuilder) {
            Region stsRegion;
            try {
                stsRegion = new DefaultAwsRegionProviderChain().getRegion();
            }
            catch (RuntimeException e) {
                stsRegion = null;
            }
            if (stsRegion != null) {
                stsClientBuilder.region(stsRegion);
            } else {
                stsClientBuilder.region(Region.US_EAST_1);
                stsClientBuilder.endpointOverride(URI.create("https://sts.amazonaws.com"));
            }
        }
    }
}

