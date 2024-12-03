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

public enum ObjectCannedACL {
    PRIVATE("private"),
    PUBLIC_READ("public-read"),
    PUBLIC_READ_WRITE("public-read-write"),
    AUTHENTICATED_READ("authenticated-read"),
    AWS_EXEC_READ("aws-exec-read"),
    BUCKET_OWNER_READ("bucket-owner-read"),
    BUCKET_OWNER_FULL_CONTROL("bucket-owner-full-control"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ObjectCannedACL> VALUE_MAP;
    private final String value;

    private ObjectCannedACL(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ObjectCannedACL fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ObjectCannedACL> knownValues() {
        EnumSet<ObjectCannedACL> knownValues = EnumSet.allOf(ObjectCannedACL.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ObjectCannedACL.class, ObjectCannedACL::toString);
    }
}

