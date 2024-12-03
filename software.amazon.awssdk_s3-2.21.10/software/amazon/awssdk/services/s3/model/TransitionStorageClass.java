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

public enum TransitionStorageClass {
    GLACIER("GLACIER"),
    STANDARD_IA("STANDARD_IA"),
    ONEZONE_IA("ONEZONE_IA"),
    INTELLIGENT_TIERING("INTELLIGENT_TIERING"),
    DEEP_ARCHIVE("DEEP_ARCHIVE"),
    GLACIER_IR("GLACIER_IR"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, TransitionStorageClass> VALUE_MAP;
    private final String value;

    private TransitionStorageClass(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static TransitionStorageClass fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<TransitionStorageClass> knownValues() {
        EnumSet<TransitionStorageClass> knownValues = EnumSet.allOf(TransitionStorageClass.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(TransitionStorageClass.class, TransitionStorageClass::toString);
    }
}

