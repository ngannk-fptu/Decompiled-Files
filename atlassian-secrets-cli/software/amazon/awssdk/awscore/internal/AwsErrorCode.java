/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class AwsErrorCode {
    public static final Set<String> RETRYABLE_ERROR_CODES;
    public static final Set<String> THROTTLING_ERROR_CODES;
    public static final Set<String> DEFINITE_CLOCK_SKEW_ERROR_CODES;
    public static final Set<String> POSSIBLE_CLOCK_SKEW_ERROR_CODES;

    private AwsErrorCode() {
    }

    public static boolean isThrottlingErrorCode(String errorCode) {
        return THROTTLING_ERROR_CODES.contains(errorCode);
    }

    public static boolean isDefiniteClockSkewErrorCode(String errorCode) {
        return DEFINITE_CLOCK_SKEW_ERROR_CODES.contains(errorCode);
    }

    public static boolean isPossibleClockSkewErrorCode(String errorCode) {
        return POSSIBLE_CLOCK_SKEW_ERROR_CODES.contains(errorCode);
    }

    public static boolean isRetryableErrorCode(String errorCode) {
        return RETRYABLE_ERROR_CODES.contains(errorCode);
    }

    static {
        HashSet<String> throttlingErrorCodes = new HashSet<String>(9);
        throttlingErrorCodes.add("Throttling");
        throttlingErrorCodes.add("ThrottlingException");
        throttlingErrorCodes.add("ThrottledException");
        throttlingErrorCodes.add("ProvisionedThroughputExceededException");
        throttlingErrorCodes.add("SlowDown");
        throttlingErrorCodes.add("TooManyRequestsException");
        throttlingErrorCodes.add("RequestLimitExceeded");
        throttlingErrorCodes.add("BandwidthLimitExceeded");
        throttlingErrorCodes.add("RequestThrottled");
        throttlingErrorCodes.add("RequestThrottledException");
        throttlingErrorCodes.add("EC2ThrottledException");
        throttlingErrorCodes.add("TransactionInProgressException");
        THROTTLING_ERROR_CODES = Collections.unmodifiableSet(throttlingErrorCodes);
        HashSet<String> definiteClockSkewErrorCodes = new HashSet<String>(3);
        definiteClockSkewErrorCodes.add("RequestTimeTooSkewed");
        definiteClockSkewErrorCodes.add("RequestExpired");
        definiteClockSkewErrorCodes.add("RequestInTheFuture");
        DEFINITE_CLOCK_SKEW_ERROR_CODES = Collections.unmodifiableSet(definiteClockSkewErrorCodes);
        HashSet<String> possibleClockSkewErrorCodes = new HashSet<String>(3);
        possibleClockSkewErrorCodes.add("InvalidSignatureException");
        possibleClockSkewErrorCodes.add("SignatureDoesNotMatch");
        possibleClockSkewErrorCodes.add("AuthFailure");
        POSSIBLE_CLOCK_SKEW_ERROR_CODES = Collections.unmodifiableSet(possibleClockSkewErrorCodes);
        HashSet<String> retryableErrorCodes = new HashSet<String>(1);
        retryableErrorCodes.add("PriorRequestNotComplete");
        retryableErrorCodes.add("RequestTimeout");
        retryableErrorCodes.add("RequestTimeoutException");
        retryableErrorCodes.add("InternalError");
        RETRYABLE_ERROR_CODES = Collections.unmodifiableSet(retryableErrorCodes);
    }
}

