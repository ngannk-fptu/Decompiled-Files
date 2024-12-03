/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.io.EOFException;
import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import zipkin2.Span;
import zipkin2.internal.Nullable;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.ThriftField;
import zipkin2.internal.V1ThriftSpanReader;
import zipkin2.internal.V1ThriftSpanWriter;
import zipkin2.internal.WriteBuffer;
import zipkin2.v1.V1Span;
import zipkin2.v1.V1SpanConverter;

public final class ThriftCodec {
    static final int MAX_SKIP_DEPTH = Integer.MAX_VALUE;
    final V1ThriftSpanWriter writer = new V1ThriftSpanWriter();

    public int sizeInBytes(Span input) {
        return this.writer.sizeInBytes(input);
    }

    public byte[] write(Span span) {
        return this.writer.write(span);
    }

    static <T> int listSizeInBytes(WriteBuffer.Writer<T> writer, List<T> values) {
        int sizeInBytes = 5;
        int length = values.size();
        for (int i = 0; i < length; ++i) {
            sizeInBytes += writer.sizeInBytes(values.get(i));
        }
        return sizeInBytes;
    }

    public static boolean read(ReadBuffer buffer, Collection<Span> out) {
        if (buffer.available() == 0) {
            return false;
        }
        try {
            V1Span v1Span = new V1ThriftSpanReader().read(buffer);
            V1SpanConverter.create().convert(v1Span, out);
            return true;
        }
        catch (Exception e) {
            throw ThriftCodec.exceptionReading("Span", e);
        }
    }

    @Nullable
    public static Span readOne(ReadBuffer buffer) {
        if (buffer.available() == 0) {
            return null;
        }
        try {
            V1Span v1Span = new V1ThriftSpanReader().read(buffer);
            ArrayList<Span> out = new ArrayList<Span>(1);
            V1SpanConverter.create().convert(v1Span, out);
            return (Span)out.get(0);
        }
        catch (Exception e) {
            throw ThriftCodec.exceptionReading("Span", e);
        }
    }

    public static boolean readList(ReadBuffer buffer, Collection<Span> out) {
        int length = buffer.available();
        if (length == 0) {
            return false;
        }
        try {
            int listLength = ThriftCodec.readListLength(buffer);
            if (listLength == 0) {
                return false;
            }
            V1ThriftSpanReader reader = new V1ThriftSpanReader();
            V1SpanConverter converter = V1SpanConverter.create();
            for (int i = 0; i < listLength; ++i) {
                V1Span v1Span = reader.read(buffer);
                converter.convert(v1Span, out);
            }
        }
        catch (Exception e) {
            throw ThriftCodec.exceptionReading("List<Span>", e);
        }
        return true;
    }

    static int readListLength(ReadBuffer buffer) {
        buffer.readByte();
        return buffer.readInt();
    }

    static <T> void writeList(WriteBuffer.Writer<T> writer, List<T> value, WriteBuffer buffer) {
        int length = value.size();
        ThriftCodec.writeListBegin(buffer, length);
        for (int i = 0; i < length; ++i) {
            writer.write(value.get(i), buffer);
        }
    }

    static IllegalArgumentException exceptionReading(String type, Exception e) {
        String cause;
        String string = cause = e.getMessage() == null ? "Error" : e.getMessage();
        if (e instanceof EOFException) {
            cause = "EOF";
        }
        if (e instanceof IllegalStateException || e instanceof BufferUnderflowException) {
            cause = "Malformed";
        }
        String message = String.format("%s reading %s from TBinary", cause, type);
        throw new IllegalArgumentException(message, e);
    }

    static void skip(ReadBuffer buffer, byte type) {
        ThriftCodec.skip(buffer, type, Integer.MAX_VALUE);
    }

    static void skip(ReadBuffer buffer, byte type, int maxDepth) {
        if (maxDepth <= 0) {
            throw new IllegalStateException("Maximum skip depth exceeded");
        }
        switch (type) {
            case 2: 
            case 3: {
                buffer.skip(1L);
                break;
            }
            case 6: {
                buffer.skip(2L);
                break;
            }
            case 8: {
                buffer.skip(4L);
                break;
            }
            case 4: 
            case 10: {
                buffer.skip(8L);
                break;
            }
            case 11: {
                buffer.skip(buffer.readInt());
                break;
            }
            case 12: {
                while (true) {
                    ThriftField thriftField = ThriftField.read(buffer);
                    if (thriftField.type == 0) {
                        return;
                    }
                    ThriftCodec.skip(buffer, thriftField.type, maxDepth - 1);
                }
            }
            case 13: {
                byte keyType = buffer.readByte();
                byte valueType = buffer.readByte();
                int length = buffer.readInt();
                for (int i = 0; i < length; ++i) {
                    ThriftCodec.skip(buffer, keyType, maxDepth - 1);
                    ThriftCodec.skip(buffer, valueType, maxDepth - 1);
                }
                break;
            }
            case 14: 
            case 15: {
                byte elemType = buffer.readByte();
                int length = buffer.readInt();
                for (int i = 0; i < length; ++i) {
                    ThriftCodec.skip(buffer, elemType, maxDepth - 1);
                }
                break;
            }
        }
    }

    static void writeListBegin(WriteBuffer buffer, int size) {
        buffer.writeByte(12);
        ThriftCodec.writeInt(buffer, size);
    }

    static void writeLengthPrefixed(WriteBuffer buffer, String utf8) {
        ThriftCodec.writeInt(buffer, WriteBuffer.utf8SizeInBytes(utf8));
        buffer.writeUtf8(utf8);
    }

    static void writeInt(WriteBuffer buf, int v) {
        buf.writeByte((byte)(v >>> 24 & 0xFF));
        buf.writeByte((byte)(v >>> 16 & 0xFF));
        buf.writeByte((byte)(v >>> 8 & 0xFF));
        buf.writeByte((byte)(v & 0xFF));
    }

    static void writeLong(WriteBuffer buf, long v) {
        buf.writeByte((byte)(v >>> 56 & 0xFFL));
        buf.writeByte((byte)(v >>> 48 & 0xFFL));
        buf.writeByte((byte)(v >>> 40 & 0xFFL));
        buf.writeByte((byte)(v >>> 32 & 0xFFL));
        buf.writeByte((byte)(v >>> 24 & 0xFFL));
        buf.writeByte((byte)(v >>> 16 & 0xFFL));
        buf.writeByte((byte)(v >>> 8 & 0xFFL));
        buf.writeByte((byte)(v & 0xFFL));
    }
}

