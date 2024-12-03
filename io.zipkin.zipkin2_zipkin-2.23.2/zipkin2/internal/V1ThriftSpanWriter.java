/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.List;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.Nullable;
import zipkin2.internal.ThriftCodec;
import zipkin2.internal.ThriftEndpointCodec;
import zipkin2.internal.ThriftField;
import zipkin2.internal.WriteBuffer;
import zipkin2.v1.V1Annotation;
import zipkin2.v1.V1BinaryAnnotation;
import zipkin2.v1.V1Span;
import zipkin2.v1.V2SpanConverter;

public final class V1ThriftSpanWriter
implements WriteBuffer.Writer<Span> {
    static final ThriftField TRACE_ID = new ThriftField(10, 1);
    static final ThriftField TRACE_ID_HIGH = new ThriftField(10, 12);
    static final ThriftField NAME = new ThriftField(11, 3);
    static final ThriftField ID = new ThriftField(10, 4);
    static final ThriftField PARENT_ID = new ThriftField(10, 5);
    static final ThriftField ANNOTATIONS = new ThriftField(15, 6);
    static final ThriftField BINARY_ANNOTATIONS = new ThriftField(15, 8);
    static final ThriftField DEBUG = new ThriftField(2, 9);
    static final ThriftField TIMESTAMP = new ThriftField(10, 10);
    static final ThriftField DURATION = new ThriftField(10, 11);
    static final byte[] EMPTY_ARRAY = new byte[0];
    final V2SpanConverter converter = V2SpanConverter.create();

    @Override
    public int sizeInBytes(Span value) {
        int i;
        V1Span v1Span = this.converter.convert(value);
        int endpointSize = value.localEndpoint() != null ? ThriftEndpointCodec.sizeInBytes(value.localEndpoint()) : 0;
        int sizeInBytes = 11;
        if (v1Span.traceIdHigh() != 0L) {
            sizeInBytes += 11;
        }
        if (v1Span.parentId() != 0L) {
            sizeInBytes += 11;
        }
        sizeInBytes += 11;
        sizeInBytes += 7;
        if (value.name() != null) {
            sizeInBytes += WriteBuffer.utf8SizeInBytes(value.name());
        }
        sizeInBytes += 8;
        int length = v1Span.annotations().size();
        for (i = 0; i < length; ++i) {
            int valueSize = WriteBuffer.utf8SizeInBytes(v1Span.annotations().get(i).value());
            sizeInBytes += ThriftAnnotationWriter.sizeInBytes(valueSize, endpointSize);
        }
        sizeInBytes += 8;
        length = v1Span.binaryAnnotations().size();
        for (i = 0; i < length; ++i) {
            V1BinaryAnnotation b = v1Span.binaryAnnotations().get(i);
            int keySize = WriteBuffer.utf8SizeInBytes(b.key());
            if (b.stringValue() != null) {
                int valueSize = WriteBuffer.utf8SizeInBytes(b.stringValue());
                sizeInBytes += ThriftBinaryAnnotationWriter.sizeInBytes(keySize, valueSize, endpointSize);
                continue;
            }
            int remoteEndpointSize = ThriftEndpointCodec.sizeInBytes(b.endpoint());
            sizeInBytes += ThriftBinaryAnnotationWriter.sizeInBytes(keySize, 1, remoteEndpointSize);
        }
        if (v1Span.debug() != null) {
            sizeInBytes += 4;
        }
        if (v1Span.timestamp() != 0L) {
            sizeInBytes += 11;
        }
        if (v1Span.duration() != 0L) {
            sizeInBytes += 11;
        }
        return ++sizeInBytes;
    }

    @Override
    public void write(Span value, WriteBuffer buffer) {
        V1Span v1Span = this.converter.convert(value);
        byte[] endpointBytes = V1ThriftSpanWriter.legacyEndpointBytes(value.localEndpoint());
        TRACE_ID.write(buffer);
        ThriftCodec.writeLong(buffer, v1Span.traceId());
        NAME.write(buffer);
        ThriftCodec.writeLengthPrefixed(buffer, value.name() != null ? value.name() : "");
        ID.write(buffer);
        ThriftCodec.writeLong(buffer, v1Span.id());
        if (v1Span.parentId() != 0L) {
            PARENT_ID.write(buffer);
            ThriftCodec.writeLong(buffer, v1Span.parentId());
        }
        ANNOTATIONS.write(buffer);
        V1ThriftSpanWriter.writeAnnotations(buffer, v1Span, endpointBytes);
        BINARY_ANNOTATIONS.write(buffer);
        V1ThriftSpanWriter.writeBinaryAnnotations(buffer, v1Span, endpointBytes);
        if (v1Span.debug() != null) {
            DEBUG.write(buffer);
            buffer.writeByte(v1Span.debug() != false ? 1 : 0);
        }
        if (v1Span.timestamp() != 0L) {
            TIMESTAMP.write(buffer);
            ThriftCodec.writeLong(buffer, v1Span.timestamp());
        }
        if (v1Span.duration() != 0L) {
            DURATION.write(buffer);
            ThriftCodec.writeLong(buffer, v1Span.duration());
        }
        if (v1Span.traceIdHigh() != 0L) {
            TRACE_ID_HIGH.write(buffer);
            ThriftCodec.writeLong(buffer, v1Span.traceIdHigh());
        }
        buffer.writeByte(0);
    }

    static void writeAnnotations(WriteBuffer buffer, V1Span v1Span, byte[] endpointBytes) {
        int annotationCount = v1Span.annotations().size();
        ThriftCodec.writeListBegin(buffer, annotationCount);
        for (int i = 0; i < annotationCount; ++i) {
            V1Annotation a = v1Span.annotations().get(i);
            ThriftAnnotationWriter.write(a.timestamp(), a.value(), endpointBytes, buffer);
        }
    }

    static void writeBinaryAnnotations(WriteBuffer buffer, V1Span v1Span, byte[] endpointBytes) {
        int binaryAnnotationCount = v1Span.binaryAnnotations().size();
        ThriftCodec.writeListBegin(buffer, binaryAnnotationCount);
        for (int i = 0; i < binaryAnnotationCount; ++i) {
            V1BinaryAnnotation a = v1Span.binaryAnnotations().get(i);
            byte[] ep = a.stringValue() != null ? endpointBytes : V1ThriftSpanWriter.legacyEndpointBytes(a.endpoint());
            ThriftBinaryAnnotationWriter.write(a.key(), a.stringValue(), ep, buffer);
        }
    }

    public String toString() {
        return "Span";
    }

    public byte[] writeList(List<Span> spans) {
        int lengthOfSpans = spans.size();
        if (lengthOfSpans == 0) {
            return EMPTY_ARRAY;
        }
        byte[] result = new byte[ThriftCodec.listSizeInBytes(this, spans)];
        ThriftCodec.writeList(this, spans, WriteBuffer.wrap(result));
        return result;
    }

    public byte[] write(Span onlySpan) {
        byte[] result = new byte[this.sizeInBytes(onlySpan)];
        this.write(onlySpan, WriteBuffer.wrap(result));
        return result;
    }

    public int writeList(List<Span> spans, byte[] out, int pos) {
        int lengthOfSpans = spans.size();
        if (lengthOfSpans == 0) {
            return 0;
        }
        WriteBuffer result = WriteBuffer.wrap(out, pos);
        ThriftCodec.writeList(this, spans, result);
        return result.pos() - pos;
    }

    static byte[] legacyEndpointBytes(@Nullable Endpoint localEndpoint) {
        if (localEndpoint == null) {
            return null;
        }
        byte[] result = new byte[ThriftEndpointCodec.sizeInBytes(localEndpoint)];
        ThriftEndpointCodec.write(localEndpoint, WriteBuffer.wrap(result));
        return result;
    }

    static class ThriftBinaryAnnotationWriter {
        static final ThriftField KEY = new ThriftField(11, 1);
        static final ThriftField VALUE = new ThriftField(11, 2);
        static final ThriftField TYPE = new ThriftField(8, 3);
        static final ThriftField ENDPOINT = new ThriftField(12, 4);

        ThriftBinaryAnnotationWriter() {
        }

        static int sizeInBytes(int keySize, int valueSize, int endpointSizeInBytes) {
            int sizeInBytes = 0;
            sizeInBytes += 7 + keySize;
            sizeInBytes += 7 + valueSize;
            sizeInBytes += 7;
            if (endpointSizeInBytes > 0) {
                sizeInBytes += 3 + endpointSizeInBytes;
            }
            return ++sizeInBytes;
        }

        static void write(String key, String stringValue, byte[] endpointBytes, WriteBuffer buffer) {
            KEY.write(buffer);
            ThriftCodec.writeLengthPrefixed(buffer, key);
            VALUE.write(buffer);
            int type = 0;
            if (stringValue != null) {
                type = 6;
                ThriftCodec.writeInt(buffer, WriteBuffer.utf8SizeInBytes(stringValue));
                buffer.writeUtf8(stringValue);
            } else {
                ThriftCodec.writeInt(buffer, 1);
                buffer.writeByte(1);
            }
            TYPE.write(buffer);
            ThriftCodec.writeInt(buffer, type);
            if (endpointBytes != null) {
                ENDPOINT.write(buffer);
                buffer.write(endpointBytes);
            }
            buffer.writeByte(0);
        }
    }

    static class ThriftAnnotationWriter {
        static final ThriftField TIMESTAMP = new ThriftField(10, 1);
        static final ThriftField VALUE = new ThriftField(11, 2);
        static final ThriftField ENDPOINT = new ThriftField(12, 3);

        ThriftAnnotationWriter() {
        }

        static int sizeInBytes(int valueSizeInBytes, int endpointSizeInBytes) {
            int sizeInBytes = 0;
            sizeInBytes += 11;
            sizeInBytes += 7 + valueSizeInBytes;
            if (endpointSizeInBytes > 0) {
                sizeInBytes += 3 + endpointSizeInBytes;
            }
            return ++sizeInBytes;
        }

        static void write(long timestamp, String value, byte[] endpointBytes, WriteBuffer buffer) {
            TIMESTAMP.write(buffer);
            ThriftCodec.writeLong(buffer, timestamp);
            VALUE.write(buffer);
            ThriftCodec.writeLengthPrefixed(buffer, value);
            if (endpointBytes != null) {
                ENDPOINT.write(buffer);
                buffer.write(endpointBytes);
            }
            buffer.writeByte(0);
        }
    }
}

