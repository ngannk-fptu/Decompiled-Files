/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.checksums.spi.ChecksumAlgorithm
 */
package software.amazon.awssdk.checksums;

import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.checksums.spi.ChecksumAlgorithm;

@SdkProtectedApi
public final class DefaultChecksumAlgorithm {
    public static final ChecksumAlgorithm CRC32C = DefaultChecksumAlgorithm.of("CRC32C");
    public static final ChecksumAlgorithm CRC32 = DefaultChecksumAlgorithm.of("CRC32");
    public static final ChecksumAlgorithm MD5 = DefaultChecksumAlgorithm.of("MD5");
    public static final ChecksumAlgorithm SHA256 = DefaultChecksumAlgorithm.of("SHA256");
    public static final ChecksumAlgorithm SHA1 = DefaultChecksumAlgorithm.of("SHA1");

    private DefaultChecksumAlgorithm() {
    }

    private static ChecksumAlgorithm of(String name) {
        return ChecksumAlgorithmsCache.put(name);
    }

    private static final class ChecksumAlgorithmsCache {
        private static final ConcurrentHashMap<String, ChecksumAlgorithm> VALUES = new ConcurrentHashMap();

        private ChecksumAlgorithmsCache() {
        }

        private static ChecksumAlgorithm put(String value) {
            return VALUES.computeIfAbsent(value, v -> () -> v);
        }
    }
}

