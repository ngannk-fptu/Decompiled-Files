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
import software.amazon.awssdk.regions.EndpointTag;
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
public final class HealthServiceMetadata
implements ServiceMetadata {
    private static final String ENDPOINT_PREFIX = "health";
    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(Region.of("aws-global"), Region.of("fips-us-east-2"), Region.of("us-east-2"), Region.of("aws-cn-global"), Region.of("fips-us-gov-west-1"), Region.of("us-gov-west-1"), Region.of("us-iso-east-1"), Region.of("us-isob-east-1")));
    private static final List<ServicePartitionMetadata> PARTITIONS = Collections.unmodifiableList(Arrays.asList(new DefaultServicePartitionMetadata("aws", Region.of("aws-global")), new DefaultServicePartitionMetadata("aws-cn", Region.of("aws-cn-global")), new DefaultServicePartitionMetadata("aws-us-gov", null), new DefaultServicePartitionMetadata("aws-iso", null), new DefaultServicePartitionMetadata("aws-iso-b", null)));
    private static final Map<ServiceEndpointKey, String> SIGNING_REGIONS_BY_REGION = ImmutableMap.builder().put(ServiceEndpointKey.builder().region(Region.of("aws-global")).build(), "us-east-1").put(ServiceEndpointKey.builder().region(Region.of("fips-us-east-2")).build(), "us-east-2").put(ServiceEndpointKey.builder().region(Region.of("us-east-2")).build(), "us-east-2").put(ServiceEndpointKey.builder().region(Region.of("us-east-2")).tags(EndpointTag.of("fips")).build(), "us-east-2").put(ServiceEndpointKey.builder().region(Region.of("aws-cn-global")).build(), "cn-northwest-1").put(ServiceEndpointKey.builder().region(Region.of("fips-us-gov-west-1")).build(), "us-gov-west-1").put(ServiceEndpointKey.builder().region(Region.of("us-gov-west-1")).build(), "us-gov-west-1").put(ServiceEndpointKey.builder().region(Region.of("us-gov-west-1")).tags(EndpointTag.of("fips")).build(), "us-gov-west-1").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> SIGNING_REGIONS_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> DNS_SUFFIXES_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> DNS_SUFFIXES_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> HOSTNAMES_BY_REGION = ImmutableMap.builder().put(ServiceEndpointKey.builder().region(Region.of("aws-global")).build(), "global.health.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("fips-us-east-2")).build(), "health-fips.us-east-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("us-east-2")).tags(EndpointTag.of("fips")).build(), "health-fips.us-east-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("aws-cn-global")).build(), "global.health.amazonaws.com.cn").put(ServiceEndpointKey.builder().region(Region.of("fips-us-gov-west-1")).build(), "health-fips.us-gov-west-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("us-gov-west-1")).tags(EndpointTag.of("fips")).build(), "health-fips.us-gov-west-1.amazonaws.com").build();
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

