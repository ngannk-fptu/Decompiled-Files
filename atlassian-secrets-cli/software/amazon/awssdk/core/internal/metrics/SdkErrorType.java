/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.metrics;

import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.ApiCallAttemptTimeoutException;
import software.amazon.awssdk.core.exception.ApiCallTimeoutException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.retry.RetryUtils;

@SdkInternalApi
public enum SdkErrorType {
    THROTTLING("Throttling"),
    SERVER_ERROR("ServerError"),
    CONFIGURED_TIMEOUT("ConfiguredTimeout"),
    IO("IO"),
    OTHER("Other");

    private final String name;

    private SdkErrorType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static SdkErrorType fromException(Throwable e) {
        if (e instanceof IOException) {
            return IO;
        }
        if (e instanceof SdkException) {
            SdkException sdkError = (SdkException)e;
            if (sdkError instanceof ApiCallTimeoutException || sdkError instanceof ApiCallAttemptTimeoutException) {
                return CONFIGURED_TIMEOUT;
            }
            if (RetryUtils.isThrottlingException(sdkError)) {
                return THROTTLING;
            }
            if (e instanceof SdkServiceException) {
                return SERVER_ERROR;
            }
        }
        return OTHER;
    }
}

