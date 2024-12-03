/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.servicemetadata;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.PartitionEndpointKey;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceEndpointKey;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServicePartitionMetadata;
import software.amazon.awssdk.regions.internal.DefaultServicePartitionMetadata;
import software.amazon.awssdk.regions.internal.util.ServiceMetadataUtils;
import software.amazon.awssdk.utils.ImmutableMap;
import software.amazon.awssdk.utils.Pair;

@SdkPublicApi
public final class ChimeServiceMetadata
implements ServiceMetadata {
    private static final String ENDPOINT_PREFIX = "chime";
    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(Region.of("aws-global")));
    private static final List<ServicePartitionMetadata> PARTITIONS = Collections.unmodifiableList(Arrays.asList(new DefaultServicePartitionMetadata("aws", Region.of("aws-global"))));
    private static final Map<ServiceEndpointKey, String> SIGNING_REGIONS_BY_REGION = ImmutableMap.builder().put(ServiceEndpointKey.builder().region(Region.of("aws-global")).build(), "us-east-1").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> SIGNING_REGIONS_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> DNS_SUFFIXES_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> DNS_SUFFIXES_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> HOSTNAMES_BY_REGION = ImmutableMap.builder().put(ServiceEndpointKey.builder().region(Region.of("aws-global")).build(), "chime.us-east-1.amazonaws.com").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> HOSTNAMES_BY_PARTITION = ImmutableMap.builder().build();

    @Override
    public List<Region> regions() {
        return REGIONS;
    }

    @Override
    public List<ServicePartitionMetadata> servicePartitions() {
        return PARTITIONS;
    }

    @Override
    public URI endpointFor(ServiceEndpointKey key) {
        return ServiceMetadataUtils.endpointFor(ServiceMetadataUtils.hostname(key, HOSTNAMES_BY_REGION, HOSTNAMES_BY_PARTITION), ENDPOINT_PREFIX, key.region().id(), ServiceMetadataUtils.dnsSuffix(key, DNS_SUFFIXES_BY_REGION, DNS_SUFFIXES_BY_PARTITION));
    }

    @Override
    public Region signingRegion(ServiceEndpointKey key) {
        return ServiceMetadataUtils.signingRegion(key, SIGNING_REGIONS_BY_REGION, SIGNING_REGIONS_BY_PARTITION);
    }
}

