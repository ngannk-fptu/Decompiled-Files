/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class AwsStatusCode {
    public static final Set<Integer> POSSIBLE_CLOCK_SKEW_STATUS_CODES;

    private AwsStatusCode() {
    }

    public static boolean isPossibleClockSkewStatusCode(int statusCode) {
        return POSSIBLE_CLOCK_SKEW_STATUS_CODES.contains(statusCode);
    }

    static {
        HashSet<Integer> clockSkewErrorCodes = new HashSet<Integer>(2);
        clockSkewErrorCodes.add(401);
        clockSkewErrorCodes.add(403);
        POSSIBLE_CLOCK_SKEW_STATUS_CODES = Collections.unmodifiableSet(clockSkewErrorCodes);
    }
}

