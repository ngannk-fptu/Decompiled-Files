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
public final class IamServiceMetadata
implements ServiceMetadata {
    private static final String ENDPOINT_PREFIX = "iam";
    private static final List<Region> REGIONS = Collections.unmodifiableList(Arrays.asList(Region.of("aws-global"), Region.of("aws-global-fips"), Region.of("aws-cn-global"), Region.of("aws-us-gov-global"), Region.of("aws-us-gov-global-fips"), Region.of("aws-iso-global"), Region.of("aws-iso-b-global")));
    private static final List<ServicePartitionMetadata> PARTITIONS = Collections.unmodifiableList(Arrays.asList(new DefaultServicePartitionMetadata("aws", Region.of("aws-global")), new DefaultServicePartitionMetadata("aws-cn", Region.of("aws-cn-global")), new DefaultServicePartitionMetadata("aws-us-gov", Region.of("aws-us-gov-global")), new DefaultServicePartitionMetadata("aws-iso", Region.of("aws-iso-global")), new DefaultServicePartitionMetadata("aws-iso-b", Region.of("aws-iso-b-global"))));
    private static final Map<ServiceEndpointKey, String> SIGNING_REGIONS_BY_REGION = ImmutableMap.builder().put((Object)ServiceEndpointKey.builder().region(Region.of("aws-global")).build(), (Object)"us-east-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-global")).tags(EndpointTag.of("fips")).build(), (Object)"us-east-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-global-fips")).build(), (Object)"us-east-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-cn-global")).build(), (Object)"cn-north-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-iso-global")).build(), (Object)"us-iso-east-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-iso-b-global")).build(), (Object)"us-isob-east-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-us-gov-global")).build(), (Object)"us-gov-west-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-us-gov-global")).tags(EndpointTag.of("fips")).build(), (Object)"us-gov-west-1").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-us-gov-global-fips")).build(), (Object)"us-gov-west-1").build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> SIGNING_REGIONS_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> DNS_SUFFIXES_BY_REGION = ImmutableMap.builder().build();
    private static final Map<Pair<String, PartitionEndpointKey>, String> DNS_SUFFIXES_BY_PARTITION = ImmutableMap.builder().build();
    private static final Map<ServiceEndpointKey, String> HOSTNAMES_BY_REGION = ImmutableMap.builder().put((Object)ServiceEndpointKey.builder().region(Region.of("aws-global")).build(), (Object)"iam.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-global")).tags(EndpointTag.of("fips")).build(), (Object)"iam-fips.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-global-fips")).build(), (Object)"iam-fips.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-cn-global")).build(), (Object)"iam.cn-north-1.amazonaws.com.cn").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-iso-global")).build(), (Object)"iam.us-iso-east-1.c2s.ic.gov").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-iso-b-global")).build(), (Object)"iam.us-isob-east-1.sc2s.sgov.gov").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-us-gov-global")).build(), (Object)"iam.us-gov.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-us-gov-global")).tags(EndpointTag.of("fips")).build(), (Object)"iam.us-gov.amazonaws.com").put((Object)ServiceEndpointKey.builder().region(Region.of("aws-us-gov-global-fips")).build(), (Object)"iam.us-gov.amazonaws.com").build();
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

