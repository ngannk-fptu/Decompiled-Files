/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.endpoints.EndpointProvider
 */
package software.amazon.awssdk.services.s3.endpoints;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.internal.DefaultS3EndpointProvider;

@SdkPublicApi
public interface S3EndpointProvider
extends EndpointProvider {
    public CompletableFuture<Endpoint> resolveEndpoint(S3EndpointParams var1);

    default public CompletableFuture<Endpoint> resolveEndpoint(Consumer<S3EndpointParams.Builder> endpointParamsConsumer) {
        S3EndpointParams.Builder paramsBuilder = S3EndpointParams.builder();
        endpointParamsConsumer.accept(paramsBuilder);
        return this.resolveEndpoint(paramsBuilder.build());
    }

    public static S3EndpointProvider defaultProvider() {
        return new DefaultS3EndpointProvider();
    }
}

