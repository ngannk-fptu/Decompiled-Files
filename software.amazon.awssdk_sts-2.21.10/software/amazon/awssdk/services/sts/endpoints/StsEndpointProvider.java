/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.endpoints.EndpointProvider
 */
package software.amazon.awssdk.services.sts.endpoints;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.services.sts.endpoints.StsEndpointParams;
import software.amazon.awssdk.services.sts.endpoints.internal.DefaultStsEndpointProvider;

@SdkPublicApi
public interface StsEndpointProvider
extends EndpointProvider {
    public CompletableFuture<Endpoint> resolveEndpoint(StsEndpointParams var1);

    default public CompletableFuture<Endpoint> resolveEndpoint(Consumer<StsEndpointParams.Builder> endpointParamsConsumer) {
        StsEndpointParams.Builder paramsBuilder = StsEndpointParams.builder();
        endpointParamsConsumer.accept(paramsBuilder);
        return this.resolveEndpoint(paramsBuilder.build());
    }

    public static StsEndpointProvider defaultProvider() {
        return new DefaultStsEndpointProvider();
    }
}

