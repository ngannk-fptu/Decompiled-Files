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
public final class AwsCnPartitionMetadata
implements PartitionMetadata {
    private static final Map<PartitionEndpointKey, String> DNS_SUFFIXES = ImmutableMap.builder().put(PartitionEndpointKey.builder().build(), "amazonaws.com.cn").put(PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build(), "amazonaws.com.cn").put(PartitionEndpointKey.builder().tags(EndpointTag.of("dualstack"), EndpointTag.of("fips")).build(), "api.amazonwebservices.com.cn").put(PartitionEndpointKey.builder().tags(EndpointTag.of("dualstack")).build(), "api.amazonwebservices.com.cn").build();
    private static final Map<PartitionEndpointKey, String> HOSTNAMES = ImmutableMap.builder().put(PartitionEndpointKey.builder().build(), "{service}.{region}.{dnsSuffix}").put(PartitionEndpointKey.builder().tags(EndpointTag.of("fips")).build(), "{service}-fips.{region}.{dnsSuffix}").put(PartitionEndpointKey.builder().tags(EndpointTag.of("dualstack"), EndpointTag.of("fips")).build(), "{service}-fips.{region}.{dnsSuffix}").put(PartitionEndpointKey.builder().tags(EndpointTag.of("dualstack")).build(), "{service}.{region}.{dnsSuffix}").build();
    private static final String ID = "aws-cn";
    private static final String NAME = "AWS China";
    private static final String REGION_REGEX = "^cn\\-\\w+\\-\\d+$";

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

