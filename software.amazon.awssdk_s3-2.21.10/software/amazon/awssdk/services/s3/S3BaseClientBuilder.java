/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.awscore.client.builder.AwsClientBuilder
 */
package software.amazon.awssdk.services.s3;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeProvider;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;

@SdkPublicApi
public interface S3BaseClientBuilder<B extends S3BaseClientBuilder<B, C>, C>
extends AwsClientBuilder<B, C> {
    public B serviceConfiguration(S3Configuration var1);

    default public B serviceConfiguration(Consumer<S3Configuration.Builder> serviceConfiguration) {
        return this.serviceConfiguration((S3Configuration)((S3Configuration.Builder)S3Configuration.builder().applyMutation(serviceConfiguration)).build());
    }

    default public B endpointProvider(S3EndpointProvider endpointProvider) {
        throw new UnsupportedOperationException();
    }

    default public B authSchemeProvider(S3AuthSchemeProvider authSchemeProvider) {
        throw new UnsupportedOperationException();
    }

    public B accelerate(Boolean var1);

    public B disableMultiRegionAccessPoints(Boolean var1);

    public B forcePathStyle(Boolean var1);

    public B useArnRegion(Boolean var1);

    public B crossRegionAccessEnabled(Boolean var1);
}

