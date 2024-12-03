/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum StatusType {
    IN_SYNC("InSync"),
    FAILED("Failed"),
    IN_PROGRESS("InProgress"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, StatusType> VALUE_MAP;
    private final String value;

    private StatusType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static StatusType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<StatusType> knownValues() {
        EnumSet<StatusType> knownValues = EnumSet.allOf(StatusType.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(StatusType.class, StatusType::toString);
    }
}

