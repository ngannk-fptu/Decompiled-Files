/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.services.s3.internal.resource;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public enum OutpostResourceType {
    OUTPOST_BUCKET("bucket"),
    OUTPOST_ACCESS_POINT("accesspoint");

    private final String value;

    private OutpostResourceType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static OutpostResourceType fromValue(String value) {
        if (StringUtils.isEmpty((CharSequence)value)) {
            throw new IllegalArgumentException("value cannot be null or empty!");
        }
        for (OutpostResourceType enumEntry : OutpostResourceType.values()) {
            if (!enumEntry.toString().equals(value)) continue;
            return enumEntry;
        }
        throw new IllegalArgumentException("Cannot create enum from " + value + " value!");
    }
}

