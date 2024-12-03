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
public final class DocdbServiceMetadata
implements ServiceMetadata {
    private static final String ENDPOINT_PREFIX = "docdb";
    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(Region.of("ap-northeast-1"), Region.of("ap-northeast-2"), Region.of("ap-south-1"), Region.of("ap-southeast-1"), Region.of("ap-southeast-2"), Region.of("ca-central-1"), Region.of("eu-central-1"), Region.of("eu-west-1"), Region.of("eu-west-2"), Region.of("eu-west-3"), Region.of("sa-east-1"), Region.of("us-east-1"), Region.of("us-east-2"), Region.of("us-west-2"), Region.of("cn-northwest-1"), Region.of("us-gov-west-1")));
    private static final List<ServicePartitionMetadata> PARTITIONS = Collections.unmodifiableList(Arrays.asList(new DefaultServicePartitionMetadata("aws", null), new DefaultServicePartitionMetadata("aws-cn", null), new DefaultServicePartitionMetadata("aws-us-gov", null)));
    private static final Map<ServiceEndpointKey, String> SIGNING_REGIONS_BY_REGION = ImmutableMap.builder().put(ServiceEndpointKey.builder().region(Region.of("ap-northeast-1")).build(), "ap-northeast-1").put(ServiceEndpointKey.builder().region(Region.of("ap-northeast-2")).build(), "ap-northeast-2").put(ServiceEndpointKey.builder().region(Region.of("ap-south-1")).build(), "ap-south-1").put(ServiceEndpointKey.builder().region(Region.of("ap-southeast-1")).build(), "ap-southeast-1").put(ServiceEndpointKey.builder().region(Region.of("ap-southeast-2")).build(), "ap-southeast-2").put(ServiceEndpointKey.builder().region(Region.of("ca-central-1")).build(), "ca-central-1").put(ServiceEndpointKey.builder().region(Region.of("eu-central-1")).build(), "eu-central-1").put(ServiceEndpointKey.builder().region(Region.of("eu-west-1")).build(), "eu-west-1").put(ServiceEndpointKey.builder().region(Region.of("eu-west-2")).build(), "eu-west-2").put(ServiceEndpointKey.builder().region(Region.of("eu-west-3")).build(), "eu-west-3").put(ServiceEndpointKey.builder().region(Region.of("sa-east-1")).build(), "sa-east-1").put(ServiceEndpointKey.builder().region(Region.of("us-east-1")).build(), "us-east-1").put(ServiceEndpointKey.builder().region(Region.of("us-east-2")).build(), "us-east-2").put(ServiceEndpointKey.builder().region(Region.of("us-west-2")).build(), "us-west-2").put(ServiceEndpointKey.builder().region(Region.of("cn-northwest-1")).build(), "cn-northwest-1").put(ServiceEndpointKey.builder().region(Region.of("us-gov-west-1")).build(), "us-gov-west-1").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> SIGNING_REGIONS_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> DNS_SUFFIXES_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> DNS_SUFFIXES_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> HOSTNAMES_BY_REGION = ImmutableMap.builder().put(ServiceEndpointKey.builder().region(Region.of("ap-northeast-1")).build(), "rds.ap-northeast-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("ap-northeast-2")).build(), "rds.ap-northeast-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("ap-south-1")).build(), "rds.ap-south-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("ap-southeast-1")).build(), "rds.ap-southeast-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("ap-southeast-2")).build(), "rds.ap-southeast-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("ca-central-1")).build(), "rds.ca-central-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("eu-central-1")).build(), "rds.eu-central-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("eu-west-1")).build(), "rds.eu-west-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("eu-west-2")).build(), "rds.eu-west-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("eu-west-3")).build(), "rds.eu-west-3.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("sa-east-1")).build(), "rds.sa-east-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("us-east-1")).build(), "rds.us-east-1.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("us-east-2")).build(), "rds.us-east-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("us-west-2")).build(), "rds.us-west-2.amazonaws.com").put(ServiceEndpointKey.builder().region(Region.of("cn-northwest-1")).build(), "rds.cn-northwest-1.amazonaws.com.cn").put(ServiceEndpointKey.builder().region(Region.of("us-gov-west-1")).build(), "rds.us-gov-west-1.amazonaws.com").build();
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

