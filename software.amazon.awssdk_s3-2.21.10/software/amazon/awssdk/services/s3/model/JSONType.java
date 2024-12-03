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

public enum JSONType {
    DOCUMENT("DOCUMENT"),
    LINES("LINES"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, JSONType> VALUE_MAP;
    private final String value;

    private JSONType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static JSONType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<JSONType> knownValues() {
        EnumSet<JSONType> knownValues = EnumSet.allOf(JSONType.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(JSONType.class, JSONType::toString);
    }
}

