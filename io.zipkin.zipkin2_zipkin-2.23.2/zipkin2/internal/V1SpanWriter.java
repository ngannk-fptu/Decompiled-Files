/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.Endpoint;
import zipkin2.internal.JsonEscaper;
import zipkin2.internal.Nullable;
import zipkin2.internal.V2SpanWriter;
import zipkin2.internal.WriteBuffer;
import zipkin2.v1.V1Annotation;
import zipkin2.v1.V1BinaryAnnotation;
import zipkin2.v1.V1Span;

public final class V1SpanWriter
implements WriteBuffer.Writer<V1Span> {
    @Override
    public int sizeInBytes(V1Span value) {
        int binaryAnnotationCount;
        int sizeInBytes = 29;
        if (value.traceIdHigh() != 0L) {
            sizeInBytes += 16;
        }
        if (value.parentId() != 0L) {
            sizeInBytes += 30;
        }
        sizeInBytes += 24;
        sizeInBytes += 10;
        if (value.name() != null) {
            sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value.name());
        }
        if (value.timestamp() != 0L) {
            sizeInBytes += 13;
            sizeInBytes += WriteBuffer.asciiSizeInBytes(value.timestamp());
        }
        if (value.duration() != 0L) {
            sizeInBytes += 12;
            sizeInBytes += WriteBuffer.asciiSizeInBytes(value.duration());
        }
        int annotationCount = value.annotations().size();
        Endpoint lastEndpoint = null;
        int lastEndpointSize = 0;
        if (annotationCount > 0) {
            sizeInBytes += 17;
            if (annotationCount > 1) {
                sizeInBytes += annotationCount - 1;
            }
            for (int i = 0; i < annotationCount; ++i) {
                int endpointSize;
                V1Annotation a = value.annotations().get(i);
                Endpoint endpoint = a.endpoint();
                if (endpoint == null) {
                    endpointSize = 0;
                } else if (endpoint.equals(lastEndpoint)) {
                    endpointSize = lastEndpointSize;
                } else {
                    lastEndpoint = endpoint;
                    endpointSize = lastEndpointSize = V2SpanWriter.endpointSizeInBytes(endpoint, true);
                }
                sizeInBytes += V2SpanWriter.annotationSizeInBytes(a.timestamp(), a.value(), endpointSize);
            }
        }
        if ((binaryAnnotationCount = value.binaryAnnotations().size()) > 0) {
            sizeInBytes += 23;
            if (binaryAnnotationCount > 1) {
                sizeInBytes += binaryAnnotationCount - 1;
            }
            int i = 0;
            while (i < binaryAnnotationCount) {
                int endpointSize;
                V1BinaryAnnotation a = value.binaryAnnotations().get(i++);
                Endpoint endpoint = a.endpoint();
                if (endpoint == null) {
                    endpointSize = 0;
                } else if (endpoint.equals(lastEndpoint)) {
                    endpointSize = lastEndpointSize;
                } else {
                    lastEndpoint = endpoint;
                    endpointSize = lastEndpointSize = V2SpanWriter.endpointSizeInBytes(endpoint, true);
                }
                if (a.stringValue() != null) {
                    sizeInBytes += V1SpanWriter.binaryAnnotationSizeInBytes(a.key(), a.stringValue(), endpointSize);
                    continue;
                }
                sizeInBytes += 37;
                sizeInBytes += endpointSize;
            }
        }
        if (Boolean.TRUE.equals(value.debug())) {
            sizeInBytes += 13;
        }
        return ++sizeInBytes;
    }

    @Override
    public void write(V1Span value, WriteBuffer b) {
        int binaryAnnotationCount;
        b.writeAscii("{\"traceId\":\"");
        if (value.traceIdHigh() != 0L) {
            b.writeLongHex(value.traceIdHigh());
        }
        b.writeLongHex(value.traceId());
        b.writeByte(34);
        if (value.parentId() != 0L) {
            b.writeAscii(",\"parentId\":\"");
            b.writeLongHex(value.parentId());
            b.writeByte(34);
        }
        b.writeAscii(",\"id\":\"");
        b.writeLongHex(value.id());
        b.writeByte(34);
        b.writeAscii(",\"name\":\"");
        if (value.name() != null) {
            b.writeUtf8(JsonEscaper.jsonEscape(value.name()));
        }
        b.writeByte(34);
        if (value.timestamp() != 0L) {
            b.writeAscii(",\"timestamp\":");
            b.writeAscii(value.timestamp());
        }
        if (value.duration() != 0L) {
            b.writeAscii(",\"duration\":");
            b.writeAscii(value.duration());
        }
        int annotationCount = value.annotations().size();
        Endpoint lastEndpoint = null;
        byte[] lastEndpointBytes = null;
        if (annotationCount > 0) {
            b.writeAscii(",\"annotations\":[");
            int i = 0;
            while (i < annotationCount) {
                byte[] endpointBytes;
                V1Annotation a = value.annotations().get(i++);
                Endpoint endpoint = a.endpoint();
                if (endpoint == null) {
                    endpointBytes = null;
                } else if (endpoint.equals(lastEndpoint)) {
                    endpointBytes = lastEndpointBytes;
                } else {
                    lastEndpoint = endpoint;
                    endpointBytes = lastEndpointBytes = V1SpanWriter.legacyEndpointBytes(endpoint);
                }
                V2SpanWriter.writeAnnotation(a.timestamp(), a.value(), endpointBytes, b);
                if (i >= annotationCount) continue;
                b.writeByte(44);
            }
            b.writeByte(93);
        }
        if ((binaryAnnotationCount = value.binaryAnnotations().size()) > 0) {
            b.writeAscii(",\"binaryAnnotations\":[");
            int i = 0;
            while (i < binaryAnnotationCount) {
                byte[] endpointBytes;
                V1BinaryAnnotation a = value.binaryAnnotations().get(i++);
                Endpoint endpoint = a.endpoint();
                if (endpoint == null) {
                    endpointBytes = null;
                } else if (endpoint.equals(lastEndpoint)) {
                    endpointBytes = lastEndpointBytes;
                } else {
                    lastEndpoint = endpoint;
                    endpointBytes = lastEndpointBytes = V1SpanWriter.legacyEndpointBytes(endpoint);
                }
                if (a.stringValue() != null) {
                    V1SpanWriter.writeBinaryAnnotation(a.key(), a.stringValue(), endpointBytes, b);
                } else {
                    b.writeAscii("{\"key\":\"");
                    b.writeAscii(a.key());
                    b.writeAscii("\",\"value\":true,\"endpoint\":");
                    b.write(endpointBytes);
                    b.writeByte(125);
                }
                if (i >= binaryAnnotationCount) continue;
                b.writeByte(44);
            }
            b.writeByte(93);
        }
        if (Boolean.TRUE.equals(value.debug())) {
            b.writeAscii(",\"debug\":true");
        }
        b.writeByte(125);
    }

    public String toString() {
        return "Span";
    }

    static byte[] legacyEndpointBytes(@Nullable Endpoint localEndpoint) {
        if (localEndpoint == null) {
            return null;
        }
        byte[] result = new byte[V2SpanWriter.endpointSizeInBytes(localEndpoint, true)];
        V2SpanWriter.writeEndpoint(localEndpoint, WriteBuffer.wrap(result), true);
        return result;
    }

    static int binaryAnnotationSizeInBytes(String key, String value, int endpointSize) {
        int sizeInBytes = 21;
        sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(key);
        sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value);
        if (endpointSize != 0) {
            sizeInBytes += 12;
            sizeInBytes += endpointSize;
        }
        return sizeInBytes;
    }

    static void writeBinaryAnnotation(String key, String value, @Nullable byte[] endpoint, WriteBuffer b) {
        b.writeAscii("{\"key\":\"");
        b.writeUtf8(JsonEscaper.jsonEscape(key));
        b.writeAscii("\",\"value\":\"");
        b.writeUtf8(JsonEscaper.jsonEscape(value));
        b.writeByte(34);
        if (endpoint != null) {
            b.writeAscii(",\"endpoint\":");
            b.write(endpoint);
        }
        b.writeAscii("}");
    }
}

