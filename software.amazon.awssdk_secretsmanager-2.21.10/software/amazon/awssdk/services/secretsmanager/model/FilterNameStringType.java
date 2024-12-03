/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.utils.internal.EnumUtils
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum FilterNameStringType {
    DESCRIPTION("description"),
    NAME("name"),
    TAG_KEY("tag-key"),
    TAG_VALUE("tag-value"),
    PRIMARY_REGION("primary-region"),
    OWNING_SERVICE("owning-service"),
    ALL("all"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, FilterNameStringType> VALUE_MAP;
    private final String value;

    private FilterNameStringType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static FilterNameStringType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<FilterNameStringType> knownValues() {
        EnumSet<FilterNameStringType> knownValues = EnumSet.allOf(FilterNameStringType.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(FilterNameStringType.class, FilterNameStringType::toString);
    }
}

