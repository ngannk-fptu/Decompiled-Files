/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.internal.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.util.SdkUserAgent;
import software.amazon.awssdk.regions.internal.util.Ec2MetadataConfigProvider;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;

@SdkInternalApi
public final class InstanceProviderTokenEndpointProvider
implements ResourcesEndpointProvider {
    private static final String TOKEN_RESOURCE_PATH = "/latest/api/token";
    private static final String EC2_METADATA_TOKEN_TTL_HEADER = "x-aws-ec2-metadata-token-ttl-seconds";
    private static final String DEFAULT_TOKEN_TTL = "21600";
    private static final Ec2MetadataConfigProvider EC2_METADATA_CONFIG_PROVIDER = Ec2MetadataConfigProvider.builder().build();

    @Override
    public URI endpoint() {
        String host = EC2_METADATA_CONFIG_PROVIDER.getEndpoint();
        if (host.endsWith("/")) {
            host = host.substring(0, host.length() - 1);
        }
        return URI.create(host + TOKEN_RESOURCE_PATH);
    }

    @Override
    public Map<String, String> headers() {
        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("User-Agent", SdkUserAgent.create().userAgent());
        requestHeaders.put("Accept", "*/*");
        requestHeaders.put("Connection", "keep-alive");
        requestHeaders.put(EC2_METADATA_TOKEN_TTL_HEADER, DEFAULT_TOKEN_TTL);
        return requestHeaders;
    }
}

