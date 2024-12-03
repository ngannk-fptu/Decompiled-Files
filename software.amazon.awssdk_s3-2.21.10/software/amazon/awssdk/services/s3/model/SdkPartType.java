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

public enum SdkPartType {
    DEFAULT("DEFAULT"),
    LAST("LAST"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, SdkPartType> VALUE_MAP;
    private final String value;

    private SdkPartType(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static SdkPartType fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<SdkPartType> knownValues() {
        EnumSet<SdkPartType> knownValues = EnumSet.allOf(SdkPartType.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(SdkPartType.class, SdkPartType::toString);
    }
}

