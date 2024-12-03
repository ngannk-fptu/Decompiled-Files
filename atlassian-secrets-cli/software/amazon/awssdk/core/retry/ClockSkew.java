/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.Logger;

@ThreadSafe
@SdkProtectedApi
public final class ClockSkew {
    private static final Logger log = Logger.loggerFor(ClockSkew.class);
    private static final Duration CLOCK_SKEW_ADJUST_THRESHOLD = Duration.ofMinutes(4L);

    private ClockSkew() {
    }

    public static boolean isClockSkewed(Instant clientTime, Instant serverTime) {
        Duration requestClockSkew = ClockSkew.getClockSkew(clientTime, serverTime);
        return requestClockSkew.abs().compareTo(CLOCK_SKEW_ADJUST_THRESHOLD) >= 0;
    }

    public static Duration getClockSkew(Instant clientTime, Instant serverTime) {
        if (clientTime == null || serverTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(serverTime, clientTime);
    }

    public static Optional<Instant> getServerTime(SdkHttpResponse serviceResponse) {
        Optional<String> responseDateHeader = serviceResponse.firstMatchingHeader("Date");
        if (responseDateHeader.isPresent()) {
            String serverDate = responseDateHeader.get();
            log.debug(() -> "Reported service date: " + serverDate);
            try {
                return Optional.of(DateUtils.parseRfc822Date(serverDate));
            }
            catch (RuntimeException e) {
                log.warn(() -> "Unable to parse clock skew offset from response: " + serverDate, e);
                return Optional.empty();
            }
        }
        log.debug(() -> "Service did not return a Date header, so clock skew adjustments will not be applied.");
        return Optional.empty();
    }
}

