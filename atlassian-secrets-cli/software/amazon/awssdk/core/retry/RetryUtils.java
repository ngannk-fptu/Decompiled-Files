/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;

@SdkProtectedApi
public final class RetryUtils {
    private RetryUtils() {
    }

    public static boolean isRequestEntityTooLargeException(SdkException exception) {
        return RetryUtils.isServiceException(exception) && RetryUtils.toServiceException(exception).statusCode() == 413;
    }

    public static boolean isServiceException(SdkException e) {
        return e instanceof SdkServiceException;
    }

    public static SdkServiceException toServiceException(SdkException e) {
        if (!(e instanceof SdkServiceException)) {
            throw new IllegalStateException("Received non-SdkServiceException where one was expected.", e);
        }
        return (SdkServiceException)e;
    }

    public static boolean isClockSkewException(SdkException exception) {
        return RetryUtils.isServiceException(exception) && RetryUtils.toServiceException(exception).isClockSkewException();
    }

    public static boolean isThrottlingException(SdkException exception) {
        return RetryUtils.isServiceException(exception) && RetryUtils.toServiceException(exception).isThrottlingException();
    }
}

