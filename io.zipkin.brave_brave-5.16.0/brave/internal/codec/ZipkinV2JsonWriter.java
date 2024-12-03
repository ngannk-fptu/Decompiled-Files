/*
 * Decompiled with CFR 0.152.
 */
package brave.internal.codec;

import brave.Tag;
import brave.handler.MutableSpan;
import brave.internal.Nullable;
import brave.internal.codec.IpLiteral;
import brave.internal.codec.JsonEscaper;
import brave.internal.codec.WriteBuffer;

public final class ZipkinV2JsonWriter
implements WriteBuffer.Writer<MutableSpan> {
    final Tag<Throwable> errorTag;

    public ZipkinV2JsonWriter(Tag<Throwable> errorTag) {
        if (errorTag == null) {
            throw new NullPointerException("errorTag == null");
        }
        this.errorTag = errorTag;
    }

    @Override
    public int sizeInBytes(MutableSpan span) {
        int annotationCount;
        int remoteEndpointSizeInBytes;
        int localEndpointSizeInBytes;
        int sizeInBytes = 1;
        if (span.traceId() != null) {
            sizeInBytes += 12;
            sizeInBytes += span.traceId().length();
        }
        if (span.parentId() != null) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 29;
        }
        if (span.id() != null) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 23;
        }
        if (span.kind() != null) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 9;
            sizeInBytes += span.kind().name().length();
        }
        if (span.name() != null) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 9;
            sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(span.name());
        }
        if (span.startTimestamp() != 0L) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 12;
            sizeInBytes += WriteBuffer.asciiSizeInBytes(span.startTimestamp());
            if (span.finishTimestamp() != 0L) {
                sizeInBytes += 12;
                sizeInBytes += WriteBuffer.asciiSizeInBytes(span.finishTimestamp() - span.startTimestamp());
            }
        }
        if ((localEndpointSizeInBytes = ZipkinV2JsonWriter.endpointSizeInBytes(span.localServiceName(), span.localIp(), span.localPort())) > 0) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 16 + localEndpointSizeInBytes;
        }
        if ((remoteEndpointSizeInBytes = ZipkinV2JsonWriter.endpointSizeInBytes(span.remoteServiceName(), span.remoteIp(), span.remotePort())) > 0) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 17 + remoteEndpointSizeInBytes;
        }
        if ((annotationCount = span.annotationCount()) > 0) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 16;
            if (annotationCount > 1) {
                sizeInBytes += annotationCount - 1;
            }
            for (int i = 0; i < annotationCount; ++i) {
                long timestamp = span.annotationTimestampAt(i);
                String value = span.annotationValueAt(i);
                sizeInBytes += ZipkinV2JsonWriter.annotationSizeInBytes(timestamp, value);
            }
        }
        int tagCount = span.tagCount();
        String errorValue = this.errorTag.value(span.error(), null);
        if (tagCount > 0 || errorValue != null) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 9;
            boolean foundError = false;
            for (int i = 0; i < tagCount; ++i) {
                String key = span.tagKeyAt(i);
                if (!foundError && key.equals("error")) {
                    foundError = true;
                }
                String value = span.tagValueAt(i);
                sizeInBytes += ZipkinV2JsonWriter.tagSizeInBytes(key, value);
            }
            if (errorValue != null && !foundError) {
                ++tagCount;
                sizeInBytes += ZipkinV2JsonWriter.tagSizeInBytes(this.errorTag.key(), errorValue);
            }
            if (tagCount > 1) {
                sizeInBytes += tagCount - 1;
            }
        }
        if (Boolean.TRUE.equals(span.debug())) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 12;
        }
        if (Boolean.TRUE.equals(span.shared())) {
            if (sizeInBytes > 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 13;
        }
        return sizeInBytes + 1;
    }

    @Override
    public void write(MutableSpan span, WriteBuffer b) {
        int annotationLength;
        b.writeByte(123);
        boolean wroteField = false;
        if (span.traceId() != null) {
            wroteField = this.writeFieldBegin(b, "traceId", wroteField);
            b.writeByte(34);
            b.writeAscii(span.traceId());
            b.writeByte(34);
        }
        if (span.parentId() != null) {
            wroteField = this.writeFieldBegin(b, "parentId", wroteField);
            b.writeByte(34);
            b.writeAscii(span.parentId());
            b.writeByte(34);
        }
        if (span.id() != null) {
            wroteField = this.writeFieldBegin(b, "id", wroteField);
            b.writeByte(34);
            b.writeAscii(span.id());
            b.writeByte(34);
        }
        if (span.kind() != null) {
            wroteField = this.writeFieldBegin(b, "kind", wroteField);
            b.writeByte(34);
            b.writeAscii(span.kind().toString());
            b.writeByte(34);
        }
        if (span.name() != null) {
            wroteField = this.writeFieldBegin(b, "name", wroteField);
            b.writeByte(34);
            JsonEscaper.jsonEscape(span.name(), b);
            b.writeByte(34);
        }
        long startTimestamp = span.startTimestamp();
        long finishTimestamp = span.finishTimestamp();
        if (startTimestamp != 0L) {
            wroteField = this.writeFieldBegin(b, "timestamp", wroteField);
            b.writeAscii(startTimestamp);
            if (finishTimestamp != 0L) {
                wroteField = this.writeFieldBegin(b, "duration", wroteField);
                b.writeAscii(finishTimestamp - startTimestamp);
            }
        }
        if (span.localServiceName() != null || span.localIp() != null) {
            wroteField = this.writeFieldBegin(b, "localEndpoint", wroteField);
            ZipkinV2JsonWriter.writeEndpoint(b, span.localServiceName(), span.localIp(), span.localPort());
        }
        if (span.remoteServiceName() != null || span.remoteIp() != null) {
            wroteField = this.writeFieldBegin(b, "remoteEndpoint", wroteField);
            ZipkinV2JsonWriter.writeEndpoint(b, span.remoteServiceName(), span.remoteIp(), span.remotePort());
        }
        if ((annotationLength = span.annotationCount()) > 0) {
            wroteField = this.writeFieldBegin(b, "annotations", wroteField);
            b.writeByte(91);
            int i = 0;
            while (i < annotationLength) {
                long timestamp = span.annotationTimestampAt(i);
                String value = span.annotationValueAt(i);
                ZipkinV2JsonWriter.writeAnnotation(timestamp, value, b);
                if (++i >= annotationLength) continue;
                b.writeByte(44);
            }
            b.writeByte(93);
        }
        int tagCount = span.tagCount();
        String errorValue = this.errorTag.value(span.error(), null);
        if (tagCount > 0 || errorValue != null) {
            wroteField = this.writeFieldBegin(b, "tags", wroteField);
            b.writeByte(123);
            boolean foundError = false;
            int i = 0;
            while (i < tagCount) {
                String key = span.tagKeyAt(i);
                if (!foundError && key.equals("error")) {
                    foundError = true;
                }
                String value = span.tagValueAt(i);
                ZipkinV2JsonWriter.writeKeyValue(b, key, value);
                if (++i >= tagCount) continue;
                b.writeByte(44);
            }
            if (errorValue != null && !foundError) {
                if (tagCount > 0) {
                    b.writeByte(44);
                }
                ZipkinV2JsonWriter.writeKeyValue(b, this.errorTag.key(), errorValue);
            }
            b.writeByte(125);
        }
        if (Boolean.TRUE.equals(span.debug())) {
            wroteField = this.writeFieldBegin(b, "debug", wroteField);
            b.writeAscii("true");
        }
        if (Boolean.TRUE.equals(span.shared())) {
            this.writeFieldBegin(b, "shared", wroteField);
            b.writeAscii("true");
        }
        b.writeByte(125);
    }

    static int endpointSizeInBytes(@Nullable String serviceName, @Nullable String ip, int port) {
        int sizeInBytes = 0;
        if (serviceName != null) {
            sizeInBytes += 16;
            sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(serviceName);
        }
        if (ip != null) {
            if (sizeInBytes > 0) {
                ++sizeInBytes;
            }
            sizeInBytes += 9;
            sizeInBytes += ip.length();
            if (port != 0) {
                if (sizeInBytes != 1) {
                    ++sizeInBytes;
                }
                sizeInBytes += 7;
                sizeInBytes += WriteBuffer.asciiSizeInBytes(port);
            }
        }
        return sizeInBytes == 0 ? 0 : sizeInBytes + 2;
    }

    static int annotationSizeInBytes(long timestamp, String value) {
        int sizeInBytes = 25;
        sizeInBytes += WriteBuffer.asciiSizeInBytes(timestamp);
        return sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value);
    }

    static int tagSizeInBytes(String key, String value) {
        int sizeInBytes = 5;
        sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(key);
        return sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value);
    }

    boolean writeFieldBegin(WriteBuffer b, String fieldName, boolean wroteField) {
        if (wroteField) {
            b.writeByte(44);
        }
        wroteField = true;
        b.writeByte(34);
        b.writeAscii(fieldName);
        b.writeByte(34);
        b.writeByte(58);
        return wroteField;
    }

    static void writeEndpoint(WriteBuffer b, @Nullable String serviceName, @Nullable String ip, int port) {
        b.writeByte(123);
        boolean wroteField = false;
        if (serviceName != null) {
            b.writeAscii("\"serviceName\":\"");
            JsonEscaper.jsonEscape(serviceName, b);
            b.writeByte(34);
            wroteField = true;
        }
        if (ip != null) {
            if (wroteField) {
                b.writeByte(44);
            }
            if (IpLiteral.detectFamily(ip) == IpLiteral.IpFamily.IPv4) {
                b.writeAscii("\"ipv4\":\"");
            } else {
                b.writeAscii("\"ipv6\":\"");
            }
            b.writeAscii(ip);
            b.writeByte(34);
            wroteField = true;
        }
        if (port != 0) {
            if (wroteField) {
                b.writeByte(44);
            }
            b.writeAscii("\"port\":");
            b.writeAscii(port);
        }
        b.writeByte(125);
    }

    static void writeAnnotation(long timestamp, String value, WriteBuffer b) {
        b.writeAscii("{\"timestamp\":");
        b.writeAscii(timestamp);
        b.writeAscii(",\"value\":\"");
        JsonEscaper.jsonEscape(value, b);
        b.writeByte(34);
        b.writeByte(125);
    }

    static void writeKeyValue(WriteBuffer b, String key, String value) {
        b.writeByte(34);
        JsonEscaper.jsonEscape(key, b);
        b.writeAscii("\":\"");
        JsonEscaper.jsonEscape(value, b);
        b.writeByte(34);
    }
}

