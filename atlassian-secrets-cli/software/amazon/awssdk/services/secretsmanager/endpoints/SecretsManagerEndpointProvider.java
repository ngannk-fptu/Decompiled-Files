/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointParams;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.DefaultSecretsManagerEndpointProvider;

@SdkPublicApi
public interface SecretsManagerEndpointProvider
extends EndpointProvider {
    public CompletableFuture<Endpoint> resolveEndpoint(SecretsManagerEndpointParams var1);

    default public CompletableFuture<Endpoint> resolveEndpoint(Consumer<SecretsManagerEndpointParams.Builder> endpointParamsConsumer) {
        SecretsManagerEndpointParams.Builder paramsBuilder = SecretsManagerEndpointParams.builder();
        endpointParamsConsumer.accept(paramsBuilder);
        return this.resolveEndpoint(paramsBuilder.build());
    }

    public static SecretsManagerEndpointProvider defaultProvider() {
        return new DefaultSecretsManagerEndpointProvider();
    }
}

