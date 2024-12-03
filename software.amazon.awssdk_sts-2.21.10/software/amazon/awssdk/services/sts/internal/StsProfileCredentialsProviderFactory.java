/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentials
 *  software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
 *  software.amazon.awssdk.auth.credentials.ChildProfileCredentialsProviderFactory
 *  software.amazon.awssdk.profiles.Profile
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.services.sts.internal;

import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ChildProfileCredentialsProviderFactory;
import software.amazon.awssdk.profiles.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.StsClientBuilder;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkInternalApi
public final class StsProfileCredentialsProviderFactory
implements ChildProfileCredentialsProviderFactory {
    private static final String MISSING_PROPERTY_ERROR_FORMAT = "'%s' must be set to use role-based credential loading in the '%s' profile.";

    public AwsCredentialsProvider create(AwsCredentialsProvider sourceCredentialsProvider, Profile profile) {
        return new StsProfileCredentialsProvider(sourceCredentialsProvider, profile);
    }

    private static final class StsProfileCredentialsProvider
    implements AwsCredentialsProvider,
    SdkAutoCloseable {
        private final StsClient stsClient;
        private final AwsCredentialsProvider parentCredentialsProvider;
        private final StsAssumeRoleCredentialsProvider credentialsProvider;

        private StsProfileCredentialsProvider(AwsCredentialsProvider parentCredentialsProvider, Profile profile) {
            String roleArn = this.requireProperty(profile, "role_arn");
            String roleSessionName = profile.property("role_session_name").orElseGet(() -> "aws-sdk-java-" + System.currentTimeMillis());
            String externalId = profile.property("external_id").orElse(null);
            AssumeRoleRequest assumeRoleRequest = (AssumeRoleRequest)((Object)AssumeRoleRequest.builder().roleArn(roleArn).roleSessionName(roleSessionName).externalId(externalId).build());
            this.stsClient = (StsClient)((StsClientBuilder)((StsClientBuilder)StsClient.builder().applyMutation(client -> this.configureEndpoint((StsClientBuilder)client, profile))).credentialsProvider(parentCredentialsProvider)).build();
            this.parentCredentialsProvider = parentCredentialsProvider;
            this.credentialsProvider = ((StsAssumeRoleCredentialsProvider.Builder)StsAssumeRoleCredentialsProvider.builder().stsClient(this.stsClient)).refreshRequest(assumeRoleRequest).build();
        }

        private void configureEndpoint(StsClientBuilder stsClientBuilder, Profile profile) {
            Region stsRegion = profile.property("region").map(Region::of).orElseGet(() -> {
                try {
                    return new DefaultAwsRegionProviderChain().getRegion();
                }
                catch (RuntimeException e) {
                    return null;
                }
            });
            if (stsRegion != null) {
                stsClientBuilder.region(stsRegion);
            } else {
                stsClientBuilder.region(Region.US_EAST_1);
                stsClientBuilder.endpointOverride(URI.create("https://sts.amazonaws.com"));
            }
        }

        private String requireProperty(Profile profile, String requiredProperty) {
            return (String)profile.property(requiredProperty).orElseThrow(() -> new IllegalArgumentException(String.format(StsProfileCredentialsProviderFactory.MISSING_PROPERTY_ERROR_FORMAT, requiredProperty, profile.name())));
        }

        public AwsCredentials resolveCredentials() {
            return this.credentialsProvider.resolveCredentials();
        }

        public void close() {
            IoUtils.closeIfCloseable((Object)this.parentCredentialsProvider, null);
            IoUtils.closeQuietly((AutoCloseable)((Object)this.credentialsProvider), null);
            IoUtils.closeQuietly((AutoCloseable)((Object)this.stsClient), null);
        }
    }
}

