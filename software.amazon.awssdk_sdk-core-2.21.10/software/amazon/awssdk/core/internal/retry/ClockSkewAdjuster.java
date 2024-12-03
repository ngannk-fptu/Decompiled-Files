/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.retry;

import java.time.Duration;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.retry.ClockSkew;
import software.amazon.awssdk.core.retry.RetryUtils;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Logger;

@ThreadSafe
@SdkInternalApi
public final class ClockSkewAdjuster {
    private static final Logger log = Logger.loggerFor(ClockSkewAdjuster.class);

    public boolean shouldAdjust(SdkException exception) {
        return RetryUtils.isClockSkewException(exception);
    }

    public Integer getAdjustmentInSeconds(SdkHttpResponse response) {
        Instant now = Instant.now();
        Instant serverTime = ClockSkew.getServerTime(response).orElse(null);
        Duration skew = ClockSkew.getClockSkew(now, serverTime);
        try {
            return Math.toIntExact(skew.getSeconds());
        }
        catch (ArithmeticException e) {
            log.warn(() -> "The clock skew between the client and server was too large to be compensated for (" + now + " versus " + serverTime + ").");
            return 0;
        }
    }
}

