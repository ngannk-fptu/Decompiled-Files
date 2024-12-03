/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum SortOrderType {
    ASC("asc"),
    DESC("desc"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, SortOrderType> VALUE_MAP;
    private final String value;

    private SortOrderType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static SortOrderType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<SortOrderType> knownValues() {
        EnumSet<SortOrderType> knownValues = EnumSet.allOf(SortOrderType.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(SortOrderType.class, SortOrderType::toString);
    }
}

