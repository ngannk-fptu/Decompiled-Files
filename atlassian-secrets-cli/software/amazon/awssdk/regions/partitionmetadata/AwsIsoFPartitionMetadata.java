/*
 * Decompiled with CFR 0.152.
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
    private static final Map<PartitionEndpointKey, String> DNS_SUFFIXES = ImmutableMap.builder().put(PartitionEndpointKey.builder().build(), "csp.hci.ic.gov").put(PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build(), "csp.hci.ic.gov").build();
    private static final Map<PartitionEndpointKey, String> HOSTNAMES = ImmutableMap.builder().put(PartitionEndpointKey.builder().build(), "{service}.{region}.{dnsSuffix}").put(PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build(), "{service}-fips.{region}.{dnsSuffix}").build();
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

