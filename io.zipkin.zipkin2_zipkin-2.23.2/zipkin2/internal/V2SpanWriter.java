/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.Iterator;
import java.util.Map;
import zipkin2.Annotation;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.JsonEscaper;
import zipkin2.internal.Nullable;
import zipkin2.internal.WriteBuffer;

public final class V2SpanWriter
implements WriteBuffer.Writer<Span> {
    @Override
    public int sizeInBytes(Span value) {
        int sizeInBytes = 13;
        sizeInBytes += value.traceId().length();
        if (value.parentId() != null) {
            sizeInBytes += 30;
        }
        sizeInBytes += 24;
        if (value.kind() != null) {
            sizeInBytes += 10;
            sizeInBytes += value.kind().name().length();
        }
        if (value.name() != null) {
            sizeInBytes += 10;
            sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value.name());
        }
        if (value.timestampAsLong() != 0L) {
            sizeInBytes += 13;
            sizeInBytes += WriteBuffer.asciiSizeInBytes(value.timestampAsLong());
        }
        if (value.durationAsLong() != 0L) {
            sizeInBytes += 12;
            sizeInBytes += WriteBuffer.asciiSizeInBytes(value.durationAsLong());
        }
        if (value.localEndpoint() != null) {
            sizeInBytes += 17;
            sizeInBytes += V2SpanWriter.endpointSizeInBytes(value.localEndpoint(), false);
        }
        if (value.remoteEndpoint() != null) {
            sizeInBytes += 18;
            sizeInBytes += V2SpanWriter.endpointSizeInBytes(value.remoteEndpoint(), false);
        }
        if (!value.annotations().isEmpty()) {
            sizeInBytes += 17;
            int length = value.annotations().size();
            if (length > 1) {
                sizeInBytes += length - 1;
            }
            for (int i = 0; i < length; ++i) {
                Annotation a = value.annotations().get(i);
                sizeInBytes += V2SpanWriter.annotationSizeInBytes(a.timestamp(), a.value(), 0);
            }
        }
        if (!value.tags().isEmpty()) {
            sizeInBytes += 10;
            int tagCount = value.tags().size();
            if (tagCount > 1) {
                sizeInBytes += tagCount - 1;
            }
            for (Map.Entry<String, String> entry : value.tags().entrySet()) {
                sizeInBytes += 5;
                sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(entry.getKey());
                sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(entry.getValue());
            }
        }
        if (Boolean.TRUE.equals(value.debug())) {
            sizeInBytes += 13;
        }
        if (Boolean.TRUE.equals(value.shared())) {
            sizeInBytes += 14;
        }
        return ++sizeInBytes;
    }

    @Override
    public void write(Span value, WriteBuffer b) {
        b.writeAscii("{\"traceId\":\"");
        b.writeAscii(value.traceId());
        b.writeByte(34);
        if (value.parentId() != null) {
            b.writeAscii(",\"parentId\":\"");
            b.writeAscii(value.parentId());
            b.writeByte(34);
        }
        b.writeAscii(",\"id\":\"");
        b.writeAscii(value.id());
        b.writeByte(34);
        if (value.kind() != null) {
            b.writeAscii(",\"kind\":\"");
            b.writeAscii(value.kind().toString());
            b.writeByte(34);
        }
        if (value.name() != null) {
            b.writeAscii(",\"name\":\"");
            b.writeUtf8(JsonEscaper.jsonEscape(value.name()));
            b.writeByte(34);
        }
        if (value.timestampAsLong() != 0L) {
            b.writeAscii(",\"timestamp\":");
            b.writeAscii(value.timestampAsLong());
        }
        if (value.durationAsLong() != 0L) {
            b.writeAscii(",\"duration\":");
            b.writeAscii(value.durationAsLong());
        }
        if (value.localEndpoint() != null) {
            b.writeAscii(",\"localEndpoint\":");
            V2SpanWriter.writeEndpoint(value.localEndpoint(), b, false);
        }
        if (value.remoteEndpoint() != null) {
            b.writeAscii(",\"remoteEndpoint\":");
            V2SpanWriter.writeEndpoint(value.remoteEndpoint(), b, false);
        }
        if (!value.annotations().isEmpty()) {
            b.writeAscii(",\"annotations\":");
            b.writeByte(91);
            int i = 0;
            int length = value.annotations().size();
            while (i < length) {
                Annotation a = value.annotations().get(i++);
                V2SpanWriter.writeAnnotation(a.timestamp(), a.value(), null, b);
                if (i >= length) continue;
                b.writeByte(44);
            }
            b.writeByte(93);
        }
        if (!value.tags().isEmpty()) {
            b.writeAscii(",\"tags\":{");
            Iterator<Map.Entry<String, String>> i = value.tags().entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> entry = i.next();
                b.writeByte(34);
                b.writeUtf8(JsonEscaper.jsonEscape(entry.getKey()));
                b.writeAscii("\":\"");
                b.writeUtf8(JsonEscaper.jsonEscape(entry.getValue()));
                b.writeByte(34);
                if (!i.hasNext()) continue;
                b.writeByte(44);
            }
            b.writeByte(125);
        }
        if (Boolean.TRUE.equals(value.debug())) {
            b.writeAscii(",\"debug\":true");
        }
        if (Boolean.TRUE.equals(value.shared())) {
            b.writeAscii(",\"shared\":true");
        }
        b.writeByte(125);
    }

    public String toString() {
        return "Span";
    }

    static int endpointSizeInBytes(Endpoint value, boolean writeEmptyServiceName) {
        int port;
        int sizeInBytes = 1;
        String serviceName = value.serviceName();
        if (serviceName == null && writeEmptyServiceName) {
            serviceName = "";
        }
        if (serviceName != null) {
            sizeInBytes += 16;
            sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(serviceName);
        }
        if (value.ipv4() != null) {
            if (sizeInBytes != 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 9;
            sizeInBytes += value.ipv4().length();
        }
        if (value.ipv6() != null) {
            if (sizeInBytes != 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 9;
            sizeInBytes += value.ipv6().length();
        }
        if ((port = value.portAsInt()) != 0) {
            if (sizeInBytes != 1) {
                ++sizeInBytes;
            }
            sizeInBytes += 7;
            sizeInBytes += WriteBuffer.asciiSizeInBytes(port);
        }
        return ++sizeInBytes;
    }

    static void writeEndpoint(Endpoint value, WriteBuffer b, boolean writeEmptyServiceName) {
        int port;
        b.writeByte(123);
        boolean wroteField = false;
        String serviceName = value.serviceName();
        if (serviceName == null && writeEmptyServiceName) {
            serviceName = "";
        }
        if (serviceName != null) {
            b.writeAscii("\"serviceName\":\"");
            b.writeUtf8(JsonEscaper.jsonEscape(serviceName));
            b.writeByte(34);
            wroteField = true;
        }
        if (value.ipv4() != null) {
            if (wroteField) {
                b.writeByte(44);
            }
            b.writeAscii("\"ipv4\":\"");
            b.writeAscii(value.ipv4());
            b.writeByte(34);
            wroteField = true;
        }
        if (value.ipv6() != null) {
            if (wroteField) {
                b.writeByte(44);
            }
            b.writeAscii("\"ipv6\":\"");
            b.writeAscii(value.ipv6());
            b.writeByte(34);
            wroteField = true;
        }
        if ((port = value.portAsInt()) != 0) {
            if (wroteField) {
                b.writeByte(44);
            }
            b.writeAscii("\"port\":");
            b.writeAscii(port);
        }
        b.writeByte(125);
    }

    static int annotationSizeInBytes(long timestamp, String value, int endpointSizeInBytes) {
        int sizeInBytes = 25;
        sizeInBytes += WriteBuffer.asciiSizeInBytes(timestamp);
        sizeInBytes += JsonEscaper.jsonEscapedSizeInBytes(value);
        if (endpointSizeInBytes != 0) {
            sizeInBytes += 12;
            sizeInBytes += endpointSizeInBytes;
        }
        return sizeInBytes;
    }

    static void writeAnnotation(long timestamp, String value, @Nullable byte[] endpoint, WriteBuffer b) {
        b.writeAscii("{\"timestamp\":");
        b.writeAscii(timestamp);
        b.writeAscii(",\"value\":\"");
        b.writeUtf8(JsonEscaper.jsonEscape(value));
        b.writeByte(34);
        if (endpoint != null) {
            b.writeAscii(",\"endpoint\":");
            b.write(endpoint);
        }
        b.writeByte(125);
    }
}

