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

public enum ChecksumAlgorithm {
    CRC32("CRC32"),
    CRC32_C("CRC32C"),
    SHA1("SHA1"),
    SHA256("SHA256"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, ChecksumAlgorithm> VALUE_MAP;
    private final String value;

    private ChecksumAlgorithm(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static ChecksumAlgorithm fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<ChecksumAlgorithm> knownValues() {
        EnumSet<ChecksumAlgorithm> knownValues = EnumSet.allOf(ChecksumAlgorithm.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(ChecksumAlgorithm.class, ChecksumAlgorithm::toString);
    }
}

