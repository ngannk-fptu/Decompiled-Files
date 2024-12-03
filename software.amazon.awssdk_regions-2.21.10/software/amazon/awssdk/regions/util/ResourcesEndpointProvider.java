/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.util.SdkUserAgent
 */
package software.amazon.awssdk.regions.util;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.util.SdkUserAgent;
import software.amazon.awssdk.regions.util.ResourcesEndpointRetryPolicy;

@FunctionalInterface
@SdkProtectedApi
public interface ResourcesEndpointProvider {
    public URI endpoint() throws IOException;

    default public ResourcesEndpointRetryPolicy retryPolicy() {
        return ResourcesEndpointRetryPolicy.NO_RETRY;
    }

    default public Map<String, String> headers() {
        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put("User-Agent", SdkUserAgent.create().userAgent());
        requestHeaders.put("Accept", "*/*");
        requestHeaders.put("Connection", "keep-alive");
        return requestHeaders;
    }
}

