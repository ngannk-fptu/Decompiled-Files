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

public enum StorageClass {
    STANDARD("STANDARD"),
    REDUCED_REDUNDANCY("REDUCED_REDUNDANCY"),
    STANDARD_IA("STANDARD_IA"),
    ONEZONE_IA("ONEZONE_IA"),
    INTELLIGENT_TIERING("INTELLIGENT_TIERING"),
    GLACIER("GLACIER"),
    DEEP_ARCHIVE("DEEP_ARCHIVE"),
    OUTPOSTS("OUTPOSTS"),
    GLACIER_IR("GLACIER_IR"),
    SNOW("SNOW"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, StorageClass> VALUE_MAP;
    private final String value;

    private StorageClass(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static StorageClass fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<StorageClass> knownValues() {
        EnumSet<StorageClass> knownValues = EnumSet.allOf(StorageClass.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(StorageClass.class, StorageClass::toString);
    }
}

