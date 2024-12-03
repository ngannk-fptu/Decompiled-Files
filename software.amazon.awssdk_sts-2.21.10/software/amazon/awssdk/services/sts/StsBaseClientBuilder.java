/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.client.builder.AwsClientBuilder
 */
package software.amazon.awssdk.services.sts;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.services.sts.auth.scheme.StsAuthSchemeProvider;
import software.amazon.awssdk.services.sts.endpoints.StsEndpointProvider;

@SdkPublicApi
public interface StsBaseClientBuilder<B extends StsBaseClientBuilder<B, C>, C>
extends AwsClientBuilder<B, C> {
    default public B endpointProvider(StsEndpointProvider endpointProvider) {
        throw new UnsupportedOperationException();
    }

    default public B authSchemeProvider(StsAuthSchemeProvider authSchemeProvider) {
        throw new UnsupportedOperationException();
    }
}

