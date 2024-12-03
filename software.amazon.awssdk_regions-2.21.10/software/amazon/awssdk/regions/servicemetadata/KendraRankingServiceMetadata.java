/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ImmutableMap
 *  software.amazon.awssdk.utils.Pair
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
public final class KendraRankingServiceMetadata
implements ServiceMetadata {
    private static final String ENDPOINT_PREFIX = "kendra-ranking";
    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(Region.of("af-south-1"), Region.of("ap-east-1"), Region.of("ap-northeast-1"), Region.of("ap-northeast-2"), Region.of("ap-northeast-3"), Region.of("ap-south-1"), Region.of("ap-south-2"), Region.of("ap-southeast-1"), Region.of("ap-southeast-2"), Region.of("ap-southeast-3"), Region.of("ap-southeast-4"), Region.of("ca-central-1"), Region.of("eu-central-2"), Region.of("eu-north-1"), Region.of("eu-south-1"), Region.of("eu-south-2"), Region.of("eu-west-1"), Region.of("eu-west-3"), Region.of("il-central-1"), Region.of("me-central-1"), Region.of("me-south-1"), Region.of("sa-east-1"), Region.of("us-east-1"), Region.of("us-east-2"), Region.of("us-west-1"), Region.of("us-west-2"), Region.of("cn-north-1"), Region.of("cn-northwest-1"), Region.of("us-gov-east-1"), Region.of("us-gov-west-1")));
    private static final List<ServicePartitionMetadata> PARTITIONS = Collections.unmodifiableList(Arrays.asList(new DefaultServicePartitionMetadata("aws", null), new DefaultServicePartitionMetadata("aws-cn", null), new DefaultServicePartitionMetadata("aws-us-gov", null)));
    private static final Map<ServiceEndpointKey, String> SIGNING_REGIONS_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> SIGNING_REGIONS_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> DNS_SUFFIXES_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> DNS_SUFFIXES_BY_PARTITION = ImmutableMap.builder().put((Object)Pair.of((Object)"aws", (Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build()), (Object)"api.aws").put((Object)Pair.of((Object)"aws-cn", (Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build()), (Object)"api.amazonwebservices.com.cn").put((Object)Pair.of((Object)"aws-us-gov", (Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build()), (Object)"api.aws").build();
    private static final Map<ServiceEndpointKey, String> HOSTNAMES_BY_REGION = ImmutableMap.builder().put((Object)ServiceEndpointKey.builder().region(Region.of("af-south-1")).build(), (Object)"kendra-ranking.af-south-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-east-1")).build(), (Object)"kendra-ranking.ap-east-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-northeast-1")).build(), (Object)"kendra-ranking.ap-northeast-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-northeast-2")).build(), (Object)"kendra-ranking.ap-northeast-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-northeast-3")).build(), (Object)"kendra-ranking.ap-northeast-3.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-south-1")).build(), (Object)"kendra-ranking.ap-south-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-south-2")).build(), (Object)"kendra-ranking.ap-south-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-southeast-1")).build(), (Object)"kendra-ranking.ap-southeast-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-southeast-2")).build(), (Object)"kendra-ranking.ap-southeast-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-southeast-3")).build(), (Object)"kendra-ranking.ap-southeast-3.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ap-southeast-4")).build(), (Object)"kendra-ranking.ap-southeast-4.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ca-central-1")).build(), (Object)"kendra-ranking.ca-central-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("ca-central-1")).tags(EndpointTag.of("fips")).build(), (Object)"kendra-ranking-fips.ca-central-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("eu-central-2")).build(), (Object)"kendra-ranking.eu-central-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("eu-north-1")).build(), (Object)"kendra-ranking.eu-north-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("eu-south-1")).build(), (Object)"kendra-ranking.eu-south-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("eu-south-2")).build(), (Object)"kendra-ranking.eu-south-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("eu-west-1")).build(), (Object)"kendra-ranking.eu-west-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("eu-west-3")).build(), (Object)"kendra-ranking.eu-west-3.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("il-central-1")).build(), (Object)"kendra-ranking.il-central-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("me-central-1")).build(), (Object)"kendra-ranking.me-central-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("me-south-1")).build(), (Object)"kendra-ranking.me-south-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("sa-east-1")).build(), (Object)"kendra-ranking.sa-east-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-east-1")).build(), (Object)"kendra-ranking.us-east-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-east-1")).tags(EndpointTag.of("fips")).build(), (Object)"kendra-ranking-fips.us-east-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-east-2")).build(), (Object)"kendra-ranking.us-east-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-east-2")).tags(EndpointTag.of("fips")).build(), (Object)"kendra-ranking-fips.us-east-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-west-1")).build(), (Object)"kendra-ranking.us-west-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-west-2")).build(), (Object)"kendra-ranking.us-west-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-west-2")).tags(EndpointTag.of("fips")).build(), (Object)"kendra-ranking-fips.us-west-2.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("cn-north-1")).build(), (Object)"kendra-ranking.cn-north-1.api.amazonwebservices.com.cn").put((Object)ServiceEndpointKey.builder().region(Region.of("cn-northwest-1")).build(), (Object)"kendra-ranking.cn-northwest-1.api.amazonwebservices.com.cn").put((Object)ServiceEndpointKey.builder().region(Region.of("us-gov-east-1")).build(), (Object)"kendra-ranking.us-gov-east-1.api.aws").put((Object)ServiceEndpointKey.builder().region(Region.of("us-gov-west-1")).build(), (Object)"kendra-ranking.us-gov-west-1.api.aws").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> HOSTNAMES_BY_PARTITION = ImmutableMap.builder().put((Object)Pair.of((Object)"aws", (Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build()), (Object)"{service}-fips.{region}.{dnsSuffix}").put((Object)Pair.of((Object)"aws-cn", (Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build()), (Object)"{service}-fips.{region}.{dnsSuffix}").put((Object)Pair.of((Object)"aws-us-gov", (Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build()), (Object)"{service}-fips.{region}.{dnsSuffix}").build();

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

