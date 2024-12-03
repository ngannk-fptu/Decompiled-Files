/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointProvider;

@SdkPublicApi
public interface SecretsManagerBaseClientBuilder<B extends SecretsManagerBaseClientBuilder<B, C>, C>
extends AwsClientBuilder<B, C> {
    default public B endpointProvider(SecretsManagerEndpointProvider endpointProvider) {
        throw new UnsupportedOperationException();
    }

    default public B authSchemeProvider(SecretsManagerAuthSchemeProvider authSchemeProvider) {
        throw new UnsupportedOperationException();
    }
}

