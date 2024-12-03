/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionEndpointKey;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceEndpointKey;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServicePartitionMetadata;
import software.amazon.awssdk.regions.internal.util.ServiceMetadataUtils;

@SdkPublicApi
public class DefaultServiceMetadata
implements ServiceMetadata {
    private final String endpointPrefix;

    public DefaultServiceMetadata(String endpointPrefix) {
        this.endpointPrefix = endpointPrefix;
    }

    @Override
    public URI endpointFor(ServiceEndpointKey key) {
        PartitionMetadata partition = PartitionMetadata.of(key.region());
        PartitionEndpointKey endpointKey = PartitionEndpointKey.builder().tags(key.tags()).build();
        String hostname = partition.hostname(endpointKey);
        String dnsName = partition.dnsSuffix(endpointKey);
        return ServiceMetadataUtils.endpointFor(hostname, this.endpointPrefix, key.region().id(), dnsName);
    }

    @Override
    public Region signingRegion(ServiceEndpointKey key) {
        return key.region();
    }

    @Override
    public List<Region> regions() {
        return Collections.emptyList();
    }

    @Override
    public List<ServicePartitionMetadata> servicePartitions() {
        return Collections.emptyList();
    }
}

