/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.regions.Region
 */
package software.amazon.awssdk.services.s3.internal.crossregion.endpointprovider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;

@SdkInternalApi
public class BucketEndpointProvider
implements S3EndpointProvider {
    private final S3EndpointProvider delegateEndPointProvider;
    private final Supplier<Region> regionSupplier;

    private BucketEndpointProvider(S3EndpointProvider delegateEndPointProvider, Supplier<Region> regionSupplier) {
        this.delegateEndPointProvider = delegateEndPointProvider;
        this.regionSupplier = regionSupplier;
    }

    public static BucketEndpointProvider create(S3EndpointProvider delegateEndPointProvider, Supplier<Region> regionSupplier) {
        return new BucketEndpointProvider(delegateEndPointProvider, regionSupplier);
    }

    @Override
    public CompletableFuture<Endpoint> resolveEndpoint(S3EndpointParams endpointParams) {
        Region crossRegion = this.regionSupplier.get();
        return this.delegateEndPointProvider.resolveEndpoint(crossRegion != null ? (S3EndpointParams)endpointParams.copy(c -> c.region(crossRegion)) : endpointParams);
    }
}

