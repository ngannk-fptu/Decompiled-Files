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

public enum ObjectOwnership {
    BUCKET_OWNER_PREFERRED("BucketOwnerPreferred"),
    OBJECT_WRITER("ObjectWriter"),
    BUCKET_OWNER_ENFORCED("BucketOwnerEnforced"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ObjectOwnership> VALUE_MAP;
    private final String value;

    private ObjectOwnership(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ObjectOwnership fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ObjectOwnership> knownValues() {
        EnumSet<ObjectOwnership> knownValues = EnumSet.allOf(ObjectOwnership.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ObjectOwnership.class, ObjectOwnership::toString);
    }
}

