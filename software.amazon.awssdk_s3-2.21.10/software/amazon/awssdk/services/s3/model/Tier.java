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

public enum Tier {
    STANDARD("Standard"),
    BULK("Bulk"),
    EXPEDITED("Expedited"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, Tier> VALUE_MAP;
    private final String value;

    private Tier(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static Tier fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<Tier> knownValues() {
        EnumSet<Tier> knownValues = EnumSet.allOf(Tier.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(Tier.class, Tier::toString);
    }
}

