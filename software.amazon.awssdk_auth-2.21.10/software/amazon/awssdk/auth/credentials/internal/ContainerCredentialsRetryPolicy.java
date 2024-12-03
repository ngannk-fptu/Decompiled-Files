/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.HttpStatusFamily
 *  software.amazon.awssdk.regions.util.ResourcesEndpointRetryParameters
 *  software.amazon.awssdk.regions.util.ResourcesEndpointRetryPolicy
 */
package software.amazon.awssdk.auth.credentials.internal;

import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.regions.util.ResourcesEndpointRetryParameters;
import software.amazon.awssdk.regions.util.ResourcesEndpointRetryPolicy;

@SdkInternalApi
public final class ContainerCredentialsRetryPolicy
implements ResourcesEndpointRetryPolicy {
    private static final int MAX_RETRIES = 5;

    public boolean shouldRetry(int retriesAttempted, ResourcesEndpointRetryParameters retryParams) {
        if (retriesAttempted >= 5) {
            return false;
        }
        Integer statusCode = retryParams.getStatusCode();
        if (statusCode != null && HttpStatusFamily.of((int)statusCode) == HttpStatusFamily.SERVER_ERROR) {
            return true;
        }
        return retryParams.getException() instanceof IOException;
    }
}

