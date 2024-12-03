/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

import brave.internal.Platform;
import brave.internal.codec.WriteBuffer;
import java.nio.charset.Charset;
import java.util.List;

public final class JsonWriter {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    static <T> int sizeInBytes(WriteBuffer.Writer<T> writer, List<T> value) {
        int length = value.size();
        int sizeInBytes = 2;
        if (length > 1) {
            sizeInBytes += length - 1;
        }
        for (int i = 0; i < length; ++i) {
            sizeInBytes += writer.sizeInBytes(value.get(i));
        }
        return sizeInBytes;
    }

    public static <T> byte[] write(WriteBuffer.Writer<T> writer, T value) {
        byte[] result = new byte[writer.sizeInBytes(value)];
        WriteBuffer b = WriteBuffer.wrap(result);
        try {
            writer.write(value, b);
        }
        catch (RuntimeException e) {
            int lengthWritten = result.length;
            for (int i = 0; i < result.length; ++i) {
                if (result[i] != 0) continue;
                lengthWritten = i;
                break;
            }
            String message = String.format("Bug found using %s to write %s as json. Wrote %s/%s bytes: %s", writer.getClass().getSimpleName(), value.getClass().getSimpleName(), lengthWritten, result.length, new String(result, 0, lengthWritten, UTF_8));
            throw Platform.get().assertionError(message, e);
        }
        return result;
    }

    public static <T> byte[] writeList(WriteBuffer.Writer<T> writer, List<T> value) {
        if (value.isEmpty()) {
            return new byte[]{91, 93};
        }
        byte[] result = new byte[JsonWriter.sizeInBytes(writer, value)];
        JsonWriter.writeList(writer, value, WriteBuffer.wrap(result));
        return result;
    }

    public static <T> int writeList(WriteBuffer.Writer<T> writer, List<T> value, byte[] out, int pos) {
        if (value.isEmpty()) {
            out[pos++] = 91;
            out[pos++] = 93;
            return 2;
        }
        int initialPos = pos;
        WriteBuffer result = WriteBuffer.wrap(out, pos);
        JsonWriter.writeList(writer, value, result);
        return result.pos() - initialPos;
    }

    public static <T> void writeList(WriteBuffer.Writer<T> writer, List<T> value, WriteBuffer b) {
        b.writeByte(91);
        int i = 0;
        int length = value.size();
        while (i < length) {
            writer.write(value.get(i++), b);
            if (i >= length) continue;
            b.writeByte(44);
        }
        b.writeByte(93);
    }
}

