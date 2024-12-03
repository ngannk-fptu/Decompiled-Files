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

public enum StorageClassAnalysisSchemaVersion {
    V_1("V_1"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, StorageClassAnalysisSchemaVersion> VALUE_MAP;
    private final String value;

    private StorageClassAnalysisSchemaVersion(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static StorageClassAnalysisSchemaVersion fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<StorageClassAnalysisSchemaVersion> knownValues() {
        EnumSet<StorageClassAnalysisSchemaVersion> knownValues = EnumSet.allOf(StorageClassAnalysisSchemaVersion.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(StorageClassAnalysisSchemaVersion.class, StorageClassAnalysisSchemaVersion::toString);
    }
}

