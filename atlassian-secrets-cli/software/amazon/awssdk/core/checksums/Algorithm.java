/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.checksums;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.internal.EnumUtils;

@SdkPublicApi
public enum Algorithm {
    CRC32C("crc32c", 8),
    CRC32("crc32", 8),
    SHA256("sha256", 44),
    SHA1("sha1", 28);

    private static final Map<String, Algorithm> VALUE_MAP;
    private final String value;
    private final int length;

    private Algorithm(String value, int length) {
        this.value = value;
        this.length = length;
    }

    public static Algorithm fromValue(String value) {
        String normalizedValue;
        if (value == null) {
            return null;
        }
        Algorithm algorithm = VALUE_MAP.get(value);
        if (algorithm == null && (algorithm = VALUE_MAP.get(normalizedValue = StringUtils.upperCase(value))) == null) {
            throw new IllegalArgumentException("The provided value is not a valid algorithm " + value);
        }
        return algorithm;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public Integer base64EncodedLength() {
        return this.length;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(Algorithm.class, a -> StringUtils.upperCase(a.value));
    }
}

