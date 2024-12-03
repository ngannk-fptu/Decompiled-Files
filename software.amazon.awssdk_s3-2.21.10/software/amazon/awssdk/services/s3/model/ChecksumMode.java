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

public enum ChecksumMode {
    ENABLED("ENABLED"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ChecksumMode> VALUE_MAP;
    private final String value;

    private ChecksumMode(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ChecksumMode fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ChecksumMode> knownValues() {
        EnumSet<ChecksumMode> knownValues = EnumSet.allOf(ChecksumMode.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ChecksumMode.class, ChecksumMode::toString);
    }
}

