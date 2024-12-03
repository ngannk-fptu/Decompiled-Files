/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.resource;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum S3ResourceType {
    BUCKET("bucket_name"),
    ACCESS_POINT("accesspoint"),
    OBJECT("object"),
    OUTPOST("outpost"),
    OBJECT_LAMBDA("object-lambda");

    private final String value;

    private S3ResourceType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static S3ResourceType fromValue(String value) {
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException("Value cannot be null or empty!");
        }
        for (S3ResourceType enumEntry : S3ResourceType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

