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

public enum ObjectLockMode {
    GOVERNANCE("GOVERNANCE"),
    COMPLIANCE("COMPLIANCE"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ObjectLockMode> VALUE_MAP;
    private final String value;

    private ObjectLockMode(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ObjectLockMode fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ObjectLockMode> knownValues() {
        EnumSet<ObjectLockMode> knownValues = EnumSet.allOf(ObjectLockMode.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ObjectLockMode.class, ObjectLockMode::toString);
    }
}

