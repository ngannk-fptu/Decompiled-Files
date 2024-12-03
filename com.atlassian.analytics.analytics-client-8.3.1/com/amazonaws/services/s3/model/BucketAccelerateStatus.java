/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

public enum BucketAccelerateStatus {
    Enabled("Enabled"),
    Suspended("Suspended");

    private final String accelerateStatus;

    public static BucketAccelerateStatus fromValue(String statusString) throws IllegalArgumentException {
        for (BucketAccelerateStatus accelerateStatus : BucketAccelerateStatus.values()) {
            if (!accelerateStatus.toString().equals(statusString)) continue;
            return accelerateStatus;
        }
        throw new IllegalArgumentException("Cannot create enum from " + statusString + " value!");
    }

    private BucketAccelerateStatus(String status) {
        this.accelerateStatus = status;
    }

    public String toString() {
        return this.accelerateStatus;
    }
}

