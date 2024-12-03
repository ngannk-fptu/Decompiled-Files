/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Checksum;
import org.xerial.snappy.PureJavaCrc32C;

final class SnappyFramed {
    public static final int COMPRESSED_DATA_FLAG = 0;
    public static final int UNCOMPRESSED_DATA_FLAG = 1;
    public static final int STREAM_IDENTIFIER_FLAG = 255;
    private static final int MASK_DELTA = -1568478504;
    private static final Supplier<Checksum> CHECKSUM_SUPPLIER;
    public static final byte[] HEADER_BYTES;

    SnappyFramed() {
    }

    public static Checksum getCRC32C() {
        return CHECKSUM_SUPPLIER.get();
    }

    public static int maskedCrc32c(Checksum checksum, byte[] byArray, int n, int n2) {
        checksum.reset();
        checksum.update(byArray, n, n2);
        return SnappyFramed.mask((int)checksum.getValue());
    }

    public static int mask(int n) {
        return (n >>> 15 | n << 17) + -1568478504;
    }

    static final int readBytes(ReadableByteChannel readableByteChannel, ByteBuffer byteBuffer) throws IOException {
        int n;
        int n2 = byteBuffer.remaining();
        int n3 = 0;
        n3 = n = readableByteChannel.read(byteBuffer);
        if (n < n2) {
            while (byteBuffer.remaining() != 0 && n != -1) {
                n = readableByteChannel.read(byteBuffer);
                if (n == -1) continue;
                n3 += n;
            }
        }
        if (n3 > 0) {
            byteBuffer.limit(byteBuffer.position());
        } else {
            byteBuffer.position(byteBuffer.limit());
        }
        return n3;
    }

    static int skip(ReadableByteChannel readableByteChannel, int n, ByteBuffer byteBuffer) throws IOException {
        if (n <= 0) {
            return 0;
        }
        int n2 = n;
        int n3 = 0;
        while (n2 > 0 && n3 != -1) {
            byteBuffer.clear();
            if (n2 < byteBuffer.capacity()) {
                byteBuffer.limit(n2);
            }
            if ((n3 = readableByteChannel.read(byteBuffer)) <= 0) continue;
            n2 -= n3;
        }
        byteBuffer.clear();
        return n - n2;
    }

    static {
        Supplier<Checksum> supplier = null;
        try {
            Class<?> clazz = Class.forName("java.util.zip.CRC32C");
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            MethodHandle methodHandle = lookup.findConstructor(clazz, MethodType.methodType(Void.TYPE)).asType(MethodType.methodType(Checksum.class));
            supplier = () -> {
                try {
                    return methodHandle.invokeExact();
                }
                catch (Throwable throwable) {
                    throw new IllegalStateException(throwable);
                }
            };
        }
        catch (Throwable throwable) {
            Logger.getLogger(SnappyFramed.class.getName()).log(Level.FINE, "java.util.zip.CRC32C not loaded, using PureJavaCrc32C", throwable);
            supplier = null;
        }
        CHECKSUM_SUPPLIER = supplier != null ? supplier : PureJavaCrc32C::new;
        HEADER_BYTES = new byte[]{-1, 6, 0, 0, 115, 78, 97, 80, 112, 89};
    }
}

