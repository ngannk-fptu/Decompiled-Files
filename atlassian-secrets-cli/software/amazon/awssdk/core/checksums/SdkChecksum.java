/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.checksums;

import java.nio.ByteBuffer;
import java.util.zip.Checksum;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.Crc32CChecksum;
import software.amazon.awssdk.core.checksums.Crc32Checksum;
import software.amazon.awssdk.core.checksums.Sha1Checksum;
import software.amazon.awssdk.core.checksums.Sha256Checksum;

@SdkPublicApi
public interface SdkChecksum
extends Checksum {
    public byte[] getChecksumBytes();

    public void mark(int var1);

    public static SdkChecksum forAlgorithm(Algorithm algorithm) {
        switch (algorithm) {
            case SHA256: {
                return new Sha256Checksum();
            }
            case SHA1: {
                return new Sha1Checksum();
            }
            case CRC32: {
                return new Crc32Checksum();
            }
            case CRC32C: {
                return new Crc32CChecksum();
            }
        }
        throw new UnsupportedOperationException("Checksum not supported for " + (Object)((Object)algorithm));
    }

    @Override
    default public void update(byte[] b) {
        this.update(b, 0, b.length);
    }

    @Override
    default public void update(ByteBuffer buffer) {
        int pos = buffer.position();
        int limit = buffer.limit();
        int rem = limit - pos;
        if (rem <= 0) {
            return;
        }
        if (buffer.hasArray()) {
            this.update(buffer.array(), pos + buffer.arrayOffset(), rem);
        } else {
            byte[] b = new byte[Math.min(buffer.remaining(), 4096)];
            while (buffer.hasRemaining()) {
                int length = Math.min(buffer.remaining(), b.length);
                buffer.get(b, 0, length);
                this.update(b, 0, length);
            }
        }
        buffer.position(limit);
    }
}

