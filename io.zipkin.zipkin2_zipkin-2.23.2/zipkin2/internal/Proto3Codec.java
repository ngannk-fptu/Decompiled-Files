/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.Collection;
import java.util.List;
import zipkin2.Span;
import zipkin2.internal.Nullable;
import zipkin2.internal.Proto3SpanWriter;
import zipkin2.internal.Proto3ZipkinFields;
import zipkin2.internal.ReadBuffer;

public final class Proto3Codec {
    final Proto3SpanWriter writer = new Proto3SpanWriter();

    public int sizeInBytes(Span input) {
        return this.writer.sizeInBytes(input);
    }

    public byte[] write(Span span) {
        return this.writer.write(span);
    }

    public byte[] writeList(List<Span> spans) {
        return this.writer.writeList(spans);
    }

    public int writeList(List<Span> spans, byte[] out, int pos) {
        return this.writer.writeList(spans, out, pos);
    }

    public static boolean read(ReadBuffer buffer, Collection<Span> out) {
        if (buffer.available() == 0) {
            return false;
        }
        try {
            Span span = Proto3ZipkinFields.SPAN.read(buffer);
            if (span == null) {
                return false;
            }
            out.add(span);
            return true;
        }
        catch (RuntimeException e) {
            throw Proto3Codec.exceptionReading("Span", e);
        }
    }

    @Nullable
    public static Span readOne(ReadBuffer buffer) {
        try {
            return Proto3ZipkinFields.SPAN.read(buffer);
        }
        catch (RuntimeException e) {
            throw Proto3Codec.exceptionReading("Span", e);
        }
    }

    public static boolean readList(ReadBuffer buffer, Collection<Span> out) {
        int length = buffer.available();
        if (length == 0) {
            return false;
        }
        try {
            while (buffer.pos() < length) {
                Span span = Proto3ZipkinFields.SPAN.read(buffer);
                if (span == null) {
                    return false;
                }
                out.add(span);
            }
        }
        catch (RuntimeException e) {
            throw Proto3Codec.exceptionReading("List<Span>", e);
        }
        return true;
    }

    static IllegalArgumentException exceptionReading(String type, Exception e) {
        String cause;
        String string = cause = e.getMessage() == null ? "Error" : e.getMessage();
        if (cause.indexOf("Malformed") != -1) {
            cause = "Malformed";
        }
        String message = String.format("%s reading %s from proto3", cause, type);
        throw new IllegalArgumentException(message, e);
    }
}

