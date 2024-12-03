/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.utils.internal.EnumUtils
 */
package software.amazon.awssdk.services.s3.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum ServerSideEncryption {
    AES256("AES256"),
    AWS_KMS("aws:kms"),
    AWS_KMS_DSSE("aws:kms:dsse"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ServerSideEncryption> VALUE_MAP;
    private final String value;

    private ServerSideEncryption(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ServerSideEncryption fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ServerSideEncryption> knownValues() {
        EnumSet<ServerSideEncryption> knownValues = EnumSet.allOf(ServerSideEncryption.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ServerSideEncryption.class, ServerSideEncryption::toString);
    }
}

