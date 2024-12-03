/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.DefaultServiceMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceEndpointKey;
import software.amazon.awssdk.regions.ServiceMetadataConfiguration;
import software.amazon.awssdk.regions.ServicePartitionMetadata;
import software.amazon.awssdk.regions.internal.MetadataLoader;

@SdkPublicApi
public interface ServiceMetadata {
    default public URI endpointFor(Region region) {
        return this.endpointFor(ServiceEndpointKey.builder().region(region).build());
    }

    default public URI endpointFor(ServiceEndpointKey key) {
        throw new UnsupportedOperationException();
    }

    default public Region signingRegion(Region region) {
        return this.signingRegion(ServiceEndpointKey.builder().region(region).build());
    }

    default public Region signingRegion(ServiceEndpointKey key) {
        throw new UnsupportedOperationException();
    }

    public List<Region> regions();

    public List<ServicePartitionMetadata> servicePartitions();

    public static ServiceMetadata of(String serviceEndpointPrefix) {
        ServiceMetadata metadata = MetadataLoader.serviceMetadata(serviceEndpointPrefix);
        return metadata == null ? new DefaultServiceMetadata(serviceEndpointPrefix) : metadata;
    }

    default public ServiceMetadata reconfigure(ServiceMetadataConfiguration configuration) {
        return this;
    }

    default public ServiceMetadata reconfigure(Consumer<ServiceMetadataConfiguration.Builder> consumer) {
        ServiceMetadataConfiguration.Builder configuration = ServiceMetadataConfiguration.builder();
        consumer.accept(configuration);
        return this.reconfigure(configuration.build());
    }
}

