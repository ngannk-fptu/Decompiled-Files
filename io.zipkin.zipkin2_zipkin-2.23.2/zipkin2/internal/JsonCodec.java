/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zipkin2.internal.Nullable;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.WriteBuffer;
import zipkin2.internal.gson.stream.JsonToken;

public final class JsonCodec {
    static final Charset UTF_8 = Charset.forName("UTF-8");

    public static <T> boolean read(JsonReaderAdapter<T> adapter, ReadBuffer buffer, Collection<T> out) {
        if (buffer.available() == 0) {
            return false;
        }
        try {
            out.add(adapter.fromJson(new JsonReader(buffer)));
            return true;
        }
        catch (Exception e) {
            throw JsonCodec.exceptionReading(adapter.toString(), e);
        }
    }

    @Nullable
    public static <T> T readOne(JsonReaderAdapter<T> adapter, ReadBuffer buffer) {
        ArrayList out = new ArrayList(1);
        if (!JsonCodec.read(adapter, buffer, out)) {
            return null;
        }
        return (T)out.get(0);
    }

    public static <T> boolean readList(JsonReaderAdapter<T> adapter, ReadBuffer buffer, Collection<T> out) {
        if (buffer.available() == 0) {
            return false;
        }
        JsonReader reader = new JsonReader(buffer);
        try {
            reader.beginArray();
            if (!reader.hasNext()) {
                return false;
            }
            while (reader.hasNext()) {
                out.add(adapter.fromJson(reader));
            }
            reader.endArray();
            return true;
        }
        catch (Exception e) {
            throw JsonCodec.exceptionReading("List<" + adapter + ">", e);
        }
    }

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
            AssertionError error = new AssertionError((Object)message);
            ((Throwable)((Object)error)).initCause(e);
            throw error;
        }
        return result;
    }

    public static <T> byte[] writeList(WriteBuffer.Writer<T> writer, List<T> value) {
        if (value.isEmpty()) {
            return new byte[]{91, 93};
        }
        byte[] result = new byte[JsonCodec.sizeInBytes(writer, value)];
        JsonCodec.writeList(writer, value, WriteBuffer.wrap(result));
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
        JsonCodec.writeList(writer, value, result);
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

    static IllegalArgumentException exceptionReading(String type, Exception e) {
        String cause;
        String string = cause = e.getMessage() == null ? "Error" : e.getMessage();
        if (cause.indexOf("Expected BEGIN_OBJECT") != -1 || cause.indexOf("malformed") != -1) {
            cause = "Malformed";
        }
        String message = String.format("%s reading %s from json", cause, type);
        throw new IllegalArgumentException(message, e);
    }

    public static interface JsonReaderAdapter<T> {
        public T fromJson(JsonReader var1) throws IOException;
    }

    public static final class JsonReader {
        final zipkin2.internal.gson.stream.JsonReader delegate;

        JsonReader(ReadBuffer buffer) {
            this.delegate = new zipkin2.internal.gson.stream.JsonReader(new InputStreamReader((InputStream)buffer, UTF_8));
        }

        public void beginArray() throws IOException {
            this.delegate.beginArray();
        }

        public boolean hasNext() throws IOException {
            return this.delegate.hasNext();
        }

        public void endArray() throws IOException {
            this.delegate.endArray();
        }

        public void beginObject() throws IOException {
            this.delegate.beginObject();
        }

        public void endObject() throws IOException {
            this.delegate.endObject();
        }

        public String nextName() throws IOException {
            return this.delegate.nextName();
        }

        public String nextString() throws IOException {
            return this.delegate.nextString();
        }

        public void skipValue() throws IOException {
            this.delegate.skipValue();
        }

        public long nextLong() throws IOException {
            return this.delegate.nextLong();
        }

        public String getPath() {
            return this.delegate.getPath();
        }

        public boolean nextBoolean() throws IOException {
            return this.delegate.nextBoolean();
        }

        public int nextInt() throws IOException {
            return this.delegate.nextInt();
        }

        public boolean peekString() throws IOException {
            return this.delegate.peek() == JsonToken.STRING;
        }

        public boolean peekBoolean() throws IOException {
            return this.delegate.peek() == JsonToken.BOOLEAN;
        }

        public boolean peekNull() throws IOException {
            return this.delegate.peek() == JsonToken.NULL;
        }

        public String toString() {
            return this.delegate.toString();
        }
    }
}

