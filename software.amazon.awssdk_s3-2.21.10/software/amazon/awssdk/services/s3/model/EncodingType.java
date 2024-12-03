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

public enum EncodingType {
    URL("url"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, EncodingType> VALUE_MAP;
    private final String value;

    private EncodingType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static EncodingType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<EncodingType> knownValues() {
        EnumSet<EncodingType> knownValues = EnumSet.allOf(EncodingType.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(EncodingType.class, EncodingType::toString);
    }
}

