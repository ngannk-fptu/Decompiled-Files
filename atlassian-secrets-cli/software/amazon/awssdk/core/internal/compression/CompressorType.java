/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.compression;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.compression.Compressor;
import software.amazon.awssdk.core.internal.compression.GzipCompressor;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class CompressorType {
    public static final CompressorType GZIP = CompressorType.of("gzip");
    private static Map<String, Compressor> compressorMap = new HashMap<String, Compressor>(){
        {
            this.put("gzip", new GzipCompressor());
        }
    };
    private final String id;

    private CompressorType(String id) {
        this.id = id;
    }

    public static CompressorType of(String value) {
        Validate.paramNotBlank(value, "compressionType");
        return CompressorTypeCache.put(value);
    }

    public static Set<String> compressorTypes() {
        return compressorMap.keySet();
    }

    public static boolean isSupported(String compressionType) {
        return CompressorType.compressorTypes().contains(compressionType);
    }

    public Compressor newCompressor() {
        Compressor compressor = compressorMap.getOrDefault(this.id, null);
        if (compressor == null) {
            throw new UnsupportedOperationException("The compression type " + this.id + " does not have an implementation of Compressor");
        }
        return compressor;
    }

    public String toString() {
        return this.id;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) return false;
        if (this.getClass() != o.getClass()) {
            return false;
        }
        CompressorType that = (CompressorType)o;
        if (!Objects.equals(this.id, that.id)) return false;
        if (!Objects.equals(compressorMap, compressorMap)) return false;
        return true;
    }

    public int hashCode() {
        int result = this.id != null ? this.id.hashCode() : 0;
        result = 31 * result + (compressorMap != null ? compressorMap.hashCode() : 0);
        return result;
    }

    private static class CompressorTypeCache {
        private static final ConcurrentHashMap<String, CompressorType> VALUES = new ConcurrentHashMap();

        private CompressorTypeCache() {
        }

        private static CompressorType put(String value) {
            return VALUES.computeIfAbsent(value, v -> new CompressorType(value));
        }
    }
}

