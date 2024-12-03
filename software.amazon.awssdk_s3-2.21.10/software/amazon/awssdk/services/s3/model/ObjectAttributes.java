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

public enum ObjectAttributes {
    E_TAG("ETag"),
    CHECKSUM("Checksum"),
    OBJECT_PARTS("ObjectParts"),
    STORAGE_CLASS("StorageClass"),
    OBJECT_SIZE("ObjectSize"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ObjectAttributes> VALUE_MAP;
    private final String value;

    private ObjectAttributes(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ObjectAttributes fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ObjectAttributes> knownValues() {
        EnumSet<ObjectAttributes> knownValues = EnumSet.allOf(ObjectAttributes.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ObjectAttributes.class, ObjectAttributes::toString);
    }
}

