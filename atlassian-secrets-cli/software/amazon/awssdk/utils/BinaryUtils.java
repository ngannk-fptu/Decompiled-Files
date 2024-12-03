/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.internal.Base16Lower;

@SdkProtectedApi
public final class BinaryUtils {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private BinaryUtils() {
    }

    public static String toHex(byte[] data) {
        return Base16Lower.encodeAsString(data);
    }

    public static byte[] fromHex(String hexData) {
        return Base16Lower.decode(hexData);
    }

    public static String toBase64(byte[] data) {
        return data == null ? null : new String(BinaryUtils.toBase64Bytes(data), StandardCharsets.UTF_8);
    }

    public static byte[] toBase64Bytes(byte[] data) {
        return data == null ? null : Base64.getEncoder().encode(data);
    }

    public static byte[] fromBase64(String b64Data) {
        return b64Data == null ? null : Base64.getDecoder().decode(b64Data);
    }

    public static byte[] fromBase64Bytes(byte[] b64Data) {
        return b64Data == null ? null : Base64.getDecoder().decode(b64Data);
    }

    public static ByteArrayInputStream toStream(ByteBuffer byteBuffer) {
        if (byteBuffer == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        return new ByteArrayInputStream(BinaryUtils.copyBytesFrom(byteBuffer));
    }

    public static ByteBuffer immutableCopyOf(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        ByteBuffer readOnlyCopy = bb.asReadOnlyBuffer();
        readOnlyCopy.rewind();
        ByteBuffer cloned = ByteBuffer.allocate(readOnlyCopy.capacity()).put(readOnlyCopy);
        cloned.position(bb.position());
        cloned.limit(bb.limit());
        return cloned.asReadOnlyBuffer();
    }

    public static ByteBuffer immutableCopyOfRemaining(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        ByteBuffer readOnlyCopy = bb.asReadOnlyBuffer();
        ByteBuffer cloned = ByteBuffer.allocate(readOnlyCopy.remaining()).put(readOnlyCopy);
        cloned.flip();
        return cloned.asReadOnlyBuffer();
    }

    public static ByteBuffer toNonDirectBuffer(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        if (!bb.isDirect()) {
            throw new IllegalArgumentException("Provided ByteBuffer is already non-direct");
        }
        int sourceBufferPosition = bb.position();
        ByteBuffer readOnlyCopy = bb.asReadOnlyBuffer();
        readOnlyCopy.rewind();
        ByteBuffer cloned = ByteBuffer.allocate(bb.capacity()).put(readOnlyCopy);
        cloned.rewind();
        cloned.position(sourceBufferPosition);
        if (bb.isReadOnly()) {
            return cloned.asReadOnlyBuffer();
        }
        return cloned;
    }

    public static byte[] copyAllBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        if (bb.hasArray()) {
            return Arrays.copyOfRange(bb.array(), bb.arrayOffset(), bb.arrayOffset() + bb.limit());
        }
        ByteBuffer copy = bb.asReadOnlyBuffer();
        copy.rewind();
        byte[] dst = new byte[copy.remaining()];
        copy.get(dst);
        return dst;
    }

    public static byte[] copyRemainingBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        if (!bb.hasRemaining()) {
            return EMPTY_BYTE_ARRAY;
        }
        if (bb.hasArray()) {
            int endIdx = bb.arrayOffset() + bb.limit();
            int startIdx = endIdx - bb.remaining();
            return Arrays.copyOfRange(bb.array(), startIdx, endIdx);
        }
        ByteBuffer copy = bb.asReadOnlyBuffer();
        byte[] dst = new byte[copy.remaining()];
        copy.get(dst);
        return dst;
    }

    public static byte[] copyBytesFrom(ByteBuffer bb) {
        if (bb == null) {
            return null;
        }
        if (bb.hasArray()) {
            return Arrays.copyOfRange(bb.array(), bb.arrayOffset() + bb.position(), bb.arrayOffset() + bb.limit());
        }
        byte[] dst = new byte[bb.remaining()];
        bb.asReadOnlyBuffer().get(dst);
        return dst;
    }

    public static byte[] copyBytesFrom(ByteBuffer bb, int readLimit) {
        if (bb == null) {
            return null;
        }
        int numBytesToRead = Math.min(readLimit, bb.limit() - bb.position());
        if (bb.hasArray()) {
            return Arrays.copyOfRange(bb.array(), bb.arrayOffset() + bb.position(), bb.arrayOffset() + bb.position() + numBytesToRead);
        }
        byte[] dst = new byte[numBytesToRead];
        bb.asReadOnlyBuffer().get(dst);
        return dst;
    }
}

