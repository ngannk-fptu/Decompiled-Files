/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.util;

import java.io.IOException;
import java.io.InputStream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.Crc32MismatchException;
import software.amazon.awssdk.core.internal.util.Crc32ChecksumCalculatingInputStream;
import software.amazon.awssdk.core.io.SdkFilterInputStream;

@SdkInternalApi
public class Crc32ChecksumValidatingInputStream
extends SdkFilterInputStream {
    private final long expectedChecksum;

    public Crc32ChecksumValidatingInputStream(InputStream in, long expectedChecksum) {
        super(new Crc32ChecksumCalculatingInputStream(in));
        this.expectedChecksum = expectedChecksum;
    }

    @Override
    public void close() throws IOException {
        try {
            this.validateChecksum();
        }
        finally {
            super.close();
        }
    }

    private void validateChecksum() throws Crc32MismatchException {
        long actualChecksum = ((Crc32ChecksumCalculatingInputStream)this.in).getCrc32Checksum();
        if (this.expectedChecksum != actualChecksum) {
            throw Crc32MismatchException.builder().message(String.format("Expected %d as the Crc32 checksum but the actual calculated checksum was %d", this.expectedChecksum, actualChecksum)).build();
        }
    }
}

