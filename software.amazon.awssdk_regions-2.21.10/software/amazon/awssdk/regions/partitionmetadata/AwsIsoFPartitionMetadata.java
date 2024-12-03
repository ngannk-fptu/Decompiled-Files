/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ImmutableMap
 */
package software.amazon.awssdk.regions.partitionmetadata;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.EndpointTag;
import software.amazon.awssdk.regions.PartitionEndpointKey;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.utils.ImmutableMap;

@SdkPublicApi
public final class AwsIsoFPartitionMetadata
implements PartitionMetadata {
    private static final Map<PartitionEndpointKey, String> DNS_SUFFIXES = ImmutableMap.builder().put((Object)PartitionEndpointKey.builder().build(), (Object)"csp.hci.ic.gov").put((Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build(), (Object)"csp.hci.ic.gov").build();
    private static final Map<PartitionEndpointKey, String> HOSTNAMES = ImmutableMap.builder().put((Object)PartitionEndpointKey.builder().build(), (Object)"{service}.{region}.{dnsSuffix}").put((Object)PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build(), (Object)"{service}-fips.{region}.{dnsSuffix}").build();
    private static final String ID = "aws-iso-f";
    private static final String NAME = "AWS ISOF";
    private static final String REGION_REGEX = "^us\\-isof\\-\\w+\\-\\d+$";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String regionRegex() {
        return REGION_REGEX;
    }

    @Override
    public String dnsSuffix(PartitionEndpointKey key) {
        return DNS_SUFFIXES.get(key);
    }

    @Override
    public String hostname(PartitionEndpointKey key) {
        return HOSTNAMES.get(key);
    }
}

