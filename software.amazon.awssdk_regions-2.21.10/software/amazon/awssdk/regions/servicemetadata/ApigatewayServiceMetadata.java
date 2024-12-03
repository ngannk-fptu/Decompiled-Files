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
public final class ApigatewayServiceMetadata
implements ServiceMetadata {
    private static final String ENDPOINT_PREFIX = "apigateway";
    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(Region.of("af-south-1"), Region.of("ap-east-1"), Region.of("ap-northeast-1"), Region.of("ap-northeast-2"), Region.of("ap-northeast-3"), Region.of("ap-south-1"), Region.of("ap-south-2"), Region.of("ap-southeast-1"), Region.of("ap-southeast-2"), Region.of("ap-southeast-3"), Region.of("ap-southeast-4"), Region.of("ca-central-1"), Region.of("eu-central-1"), Region.of("eu-central-2"), Region.of("eu-north-1"), Region.of("eu-south-1"), Region.of("eu-south-2"), Region.of("eu-west-1"), Region.of("eu-west-2"), Region.of("eu-west-3"), Region.of("fips-ca-central-1"), Region.of("fips-us-east-1"), Region.of("fips-us-east-2"), Region.of("fips-us-west-1"), Region.of("fips-us-west-2"), Region.of("il-central-1"), Region.of("me-central-1"), Region.of("me-south-1"), Region.of("sa-east-1"), Region.of("us-east-1"), Region.of("us-east-2"), Region.of("us-west-1"), Region.of("us-west-2"), Region.of("cn-north-1"), Region.of("cn-northwest-1"), Region.of("us-gov-east-1"), Region.of("us-gov-west-1"), Region.of("us-iso-east-1")));
    private static final List<ServicePartitionMetadata> PARTITIONS = Collections.unmodifiableList(Arrays.asList(new DefaultServicePartitionMetadata("aws", null), new DefaultServicePartitionMetadata("aws-cn", null), new DefaultServicePartitionMetadata("aws-us-gov", null), new DefaultServicePartitionMetadata("aws-iso", null)));
    private static final Map<ServiceEndpointKey, String> SIGNING_REGIONS_BY_REGION = ImmutableMap.builder().put((Object)ServiceEndpointKey.builder().region(Region.of("fips-ca-central-1")).build(), (Object)"ca-central-1").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-east-1")).build(), (Object)"us-east-1").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-east-2")).build(), (Object)"us-east-2").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-west-1")).build(), (Object)"us-west-1").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-west-2")).build(), (Object)"us-west-2").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> SIGNING_REGIONS_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> DNS_SUFFIXES_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> DNS_SUFFIXES_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> HOSTNAMES_BY_REGION = ImmutableMap.builder().put((Object)ServiceEndpointKey.builder().region(Region.of("ca-central-1")).tags(EndpointTag.of("fips")).build(), (Object)"apigateway-fips.ca-central-1.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-ca-central-1")).build(), (Object)"apigateway-fips.ca-central-1.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-east-1")).build(), (Object)"apigateway-fips.us-east-1.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-east-2")).build(), (Object)"apigateway-fips.us-east-2.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-west-1")).build(), (Object)"apigateway-fips.us-west-1.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("fips-us-west-2")).build(), (Object)"apigateway-fips.us-west-2.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("us-east-1")).tags(EndpointTag.of("fips")).build(), (Object)"apigateway-fips.us-east-1.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("us-east-2")).tags(EndpointTag.of("fips")).build(), (Object)"apigateway-fips.us-east-2.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("us-west-1")).tags(EndpointTag.of("fips")).build(), (Object)"apigateway-fips.us-west-1.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("us-west-2")).tags(EndpointTag.of("fips")).build(), (Object)"apigateway-fips.us-west-2.amazonaws.com").build();
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

