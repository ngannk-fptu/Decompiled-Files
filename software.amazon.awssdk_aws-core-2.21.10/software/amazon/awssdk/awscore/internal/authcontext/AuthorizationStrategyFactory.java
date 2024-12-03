/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.CredentialType
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkAdvancedClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.TokenIdentity
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.awscore.internal.authcontext;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.awscore.internal.authcontext.AuthorizationStrategy;
import software.amazon.awssdk.awscore.internal.authcontext.AwsCredentialsAuthorizationStrategy;
import software.amazon.awssdk.awscore.internal.authcontext.TokenAuthorizationStrategy;
import software.amazon.awssdk.core.CredentialType;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.metrics.MetricCollector;

@Deprecated
@SdkInternalApi
public final class AuthorizationStrategyFactory {
    private final SdkRequest request;
    private final MetricCollector metricCollector;
    private final SdkClientConfiguration clientConfiguration;

    public AuthorizationStrategyFactory(SdkRequest request, MetricCollector metricCollector, SdkClientConfiguration clientConfiguration) {
        this.request = request;
        this.metricCollector = metricCollector;
        this.clientConfiguration = clientConfiguration;
    }

    public AuthorizationStrategy strategyFor(CredentialType credentialType) {
        if (credentialType == CredentialType.TOKEN) {
            return this.tokenAuthorizationStrategy();
        }
        return this.awsCredentialsAuthorizationStrategy();
    }

    private TokenAuthorizationStrategy tokenAuthorizationStrategy() {
        Signer defaultSigner = (Signer)this.clientConfiguration.option((ClientOption)SdkAdvancedClientOption.TOKEN_SIGNER);
        IdentityProvider defaultTokenProvider = this.clientConfiguration.option(AwsClientOption.TOKEN_IDENTITY_PROVIDER) == null ? (IdentityProvider)this.clientConfiguration.option(AwsClientOption.TOKEN_PROVIDER) : (IdentityProvider)this.clientConfiguration.option(AwsClientOption.TOKEN_IDENTITY_PROVIDER);
        return TokenAuthorizationStrategy.builder().request(this.request).defaultSigner(defaultSigner).defaultTokenProvider((IdentityProvider<? extends TokenIdentity>)defaultTokenProvider).metricCollector(this.metricCollector).build();
    }

    private AwsCredentialsAuthorizationStrategy awsCredentialsAuthorizationStrategy() {
        Signer defaultSigner = (Signer)this.clientConfiguration.option((ClientOption)SdkAdvancedClientOption.SIGNER);
        IdentityProvider defaultCredentialsProvider = (IdentityProvider)this.clientConfiguration.option(AwsClientOption.CREDENTIALS_IDENTITY_PROVIDER);
        return AwsCredentialsAuthorizationStrategy.builder().request(this.request).defaultSigner(defaultSigner).defaultCredentialsProvider((IdentityProvider<? extends AwsCredentialsIdentity>)defaultCredentialsProvider).metricCollector(this.metricCollector).build();
    }
}

