/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.internal.util;

import java.net.URI;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.regions.PartitionEndpointKey;
import software.amazon.awssdk.regions.PartitionMetadata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceEndpointKey;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public class ServiceMetadataUtils {
    private static final String[] SEARCH_LIST = new String[]{"{service}", "{region}", "{dnsSuffix}"};

    private ServiceMetadataUtils() {
    }

    public static URI endpointFor(String hostname, String endpointPrefix, String region, String dnsSuffix) {
        return URI.create(StringUtils.replaceEach(hostname, SEARCH_LIST, new String[]{endpointPrefix, region, dnsSuffix}));
    }

    public static Region signingRegion(ServiceEndpointKey key, Map<ServiceEndpointKey, String> signingRegionsByRegion, Map<Pair<String, PartitionEndpointKey>, String> signingRegionsByPartition) {
        String region = signingRegionsByRegion.get(key);
        if (region == null) {
            region = signingRegionsByPartition.get(ServiceMetadataUtils.partitionKey(key));
        }
        return region != null ? Region.of(region) : key.region();
    }

    public static String dnsSuffix(ServiceEndpointKey key, Map<ServiceEndpointKey, String> dnsSuffixesByRegion, Map<Pair<String, PartitionEndpointKey>, String> dnsSuffixesByPartition) {
        String dnsSuffix = dnsSuffixesByRegion.get(key);
        if (dnsSuffix == null) {
            dnsSuffix = dnsSuffixesByPartition.get(ServiceMetadataUtils.partitionKey(key));
        }
        if (dnsSuffix == null) {
            dnsSuffix = PartitionMetadata.of(key.region()).dnsSuffix(PartitionEndpointKey.builder().tags(key.tags()).build());
        }
        Validate.notNull(dnsSuffix, "No endpoint known for " + key.tags() + " in " + key.region() + " with this service. A newer SDK version may have an endpoint available, or you could configure the endpoint directly after consulting service documentation.", new Object[0]);
        return dnsSuffix;
    }

    public static String hostname(ServiceEndpointKey key, Map<ServiceEndpointKey, String> hostnamesByRegion, Map<Pair<String, PartitionEndpointKey>, String> hostnamesByPartition) {
        String hostname = hostnamesByRegion.get(key);
        if (hostname == null) {
            hostname = hostnamesByPartition.get(ServiceMetadataUtils.partitionKey(key));
        }
        if (hostname == null) {
            hostname = PartitionMetadata.of(key.region()).hostname(PartitionEndpointKey.builder().tags(key.tags()).build());
        }
        Validate.notNull(hostname, "No endpoint known for " + key.tags() + " in " + key.region() + " with this service. A newer SDK version may have an endpoint available, or you could configure the endpoint directly after consulting service documentation.", new Object[0]);
        return hostname;
    }

    public static Pair<String, PartitionEndpointKey> partitionKey(ServiceEndpointKey key) {
        return Pair.of(PartitionMetadata.of(key.region()).id(), PartitionEndpointKey.builder().tags(key.tags()).build());
    }
}

