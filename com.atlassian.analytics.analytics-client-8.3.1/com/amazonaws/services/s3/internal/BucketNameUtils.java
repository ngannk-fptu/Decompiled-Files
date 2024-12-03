/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.services.s3.model.IllegalBucketNameException;
import java.util.regex.Pattern;

public enum BucketNameUtils {

    private static final int MIN_BUCKET_NAME_LENGTH = 3;
    private static final int MAX_BUCKET_NAME_LENGTH = 63;
    private static final Pattern ipAddressPattern = Pattern.compile("(\\d+\\.){3}\\d+");

    public static void validateBucketName(String bucketName) {
        BucketNameUtils.isValidV2BucketName(bucketName, true);
    }

    public static boolean isValidV2BucketName(String bucketName) {
        return BucketNameUtils.isValidV2BucketName(bucketName, false);
    }

    public static boolean isDNSBucketName(String bucketName) {
        return BucketNameUtils.isValidV2BucketName(bucketName);
    }

    private static boolean isValidV2BucketName(String bucketName, boolean throwOnError) {
        if (bucketName == null) {
            return BucketNameUtils.exception(throwOnError, "Bucket name cannot be null");
        }
        if (bucketName.length() < 3 || bucketName.length() > 63) {
            return BucketNameUtils.exception(throwOnError, "Bucket name should be between 3 and 63 characters long");
        }
        if (ipAddressPattern.matcher(bucketName).matches()) {
            return BucketNameUtils.exception(throwOnError, "Bucket name must not be formatted as an IP Address");
        }
        int previous = 0;
        for (int i = 0; i < bucketName.length(); ++i) {
            char next = bucketName.charAt(i);
            if (next >= 'A' && next <= 'Z') {
                return BucketNameUtils.exception(throwOnError, "Bucket name should not contain uppercase characters");
            }
            if (next == ' ' || next == '\t' || next == '\r' || next == '\n') {
                return BucketNameUtils.exception(throwOnError, "Bucket name should not contain white space");
            }
            if (next == '.') {
                if (previous == 0) {
                    return BucketNameUtils.exception(throwOnError, "Bucket name should not begin with a period");
                }
                if (previous == 46) {
                    return BucketNameUtils.exception(throwOnError, "Bucket name should not contain two adjacent periods");
                }
                if (previous == 45) {
                    return BucketNameUtils.exception(throwOnError, "Bucket name should not contain dashes next to periods");
                }
            } else if (next == '-') {
                if (previous == 46) {
                    return BucketNameUtils.exception(throwOnError, "Bucket name should not contain dashes next to periods");
                }
                if (previous == 0) {
                    return BucketNameUtils.exception(throwOnError, "Bucket name should not begin with a '-'");
                }
            } else if (next < '0' || next > '9' && next < 'a' || next > 'z') {
                return BucketNameUtils.exception(throwOnError, "Bucket name should not contain '" + next + "'");
            }
            previous = next;
        }
        if (previous == 46 || previous == 45) {
            return BucketNameUtils.exception(throwOnError, "Bucket name should not end with '-' or '.'");
        }
        return true;
    }

    private static boolean exception(boolean exception, String message) {
        if (exception) {
            throw new IllegalBucketNameException(message);
        }
        return false;
    }
}

