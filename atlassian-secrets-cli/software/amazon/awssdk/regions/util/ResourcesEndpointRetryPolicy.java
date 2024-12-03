/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.util;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.regions.util.ResourcesEndpointRetryParameters;

@SdkProtectedApi
public interface ResourcesEndpointRetryPolicy {
    public static final ResourcesEndpointRetryPolicy NO_RETRY = (retriesAttempted, retryParams) -> false;

    public boolean shouldRetry(int var1, ResourcesEndpointRetryParameters var2);
}

