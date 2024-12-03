/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal;

import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class BucketUtils {
    private static final int MIN_BUCKET_NAME_LENGTH = 3;
    private static final int MAX_BUCKET_NAME_LENGTH = 63;
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("(\\d+\\.){3}\\d+");

    private BucketUtils() {
    }

    public static boolean isValidDnsBucketName(String bucketName, boolean throwOnError) {
        if (bucketName == null) {
            return BucketUtils.exception(throwOnError, "Bucket name cannot be null");
        }
        if (bucketName.length() < 3 || bucketName.length() > 63) {
            return BucketUtils.exception(throwOnError, "Bucket name should be between 3 and 63 characters long");
        }
        if (IP_ADDRESS_PATTERN.matcher(bucketName).matches()) {
            return BucketUtils.exception(throwOnError, "Bucket name must not be formatted as an IP Address");
        }
        int previous = 0;
        for (int i = 0; i < bucketName.length(); ++i) {
            char next = bucketName.charAt(i);
            if (next >= 'A' && next <= 'Z') {
                return BucketUtils.exception(throwOnError, "Bucket name should not contain uppercase characters");
            }
            if (next == ' ' || next == '\t' || next == '\r' || next == '\n') {
                return BucketUtils.exception(throwOnError, "Bucket name should not contain white space");
            }
            if (next == '.') {
                if (previous == 0) {
                    return BucketUtils.exception(throwOnError, "Bucket name should not begin with a period");
                }
                if (previous == 46) {
                    return BucketUtils.exception(throwOnError, "Bucket name should not contain two adjacent periods");
                }
                if (previous == 45) {
                    return BucketUtils.exception(throwOnError, "Bucket name should not contain dashes next to periods");
                }
            } else if (next == '-') {
                if (previous == 46) {
                    return BucketUtils.exception(throwOnError, "Bucket name should not contain dashes next to periods");
                }
                if (previous == 0) {
                    return BucketUtils.exception(throwOnError, "Bucket name should not begin with a '-'");
                }
            } else if (next < '0' || next > '9' && next < 'a' || next > 'z') {
                return BucketUtils.exception(throwOnError, "Bucket name should not contain '" + next + "'");
            }
            previous = next;
        }
        if (previous == 46 || previous == 45) {
            return BucketUtils.exception(throwOnError, "Bucket name should not end with '-' or '.'");
        }
        return true;
    }

    public static boolean isVirtualAddressingCompatibleBucketName(String bucketName, boolean throwOnError) {
        return BucketUtils.isValidDnsBucketName(bucketName, throwOnError) && !bucketName.contains(".");
    }

    private static boolean exception(boolean exception, String message) {
        if (exception) {
            throw new IllegalArgumentException(message);
        }
        return false;
    }
}

