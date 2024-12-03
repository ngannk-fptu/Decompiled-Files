/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import zipkin2.Endpoint;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.ThriftCodec;
import zipkin2.internal.ThriftEndpointCodec;
import zipkin2.internal.ThriftField;
import zipkin2.internal.V1ThriftSpanWriter;
import zipkin2.v1.V1Span;

public final class V1ThriftSpanReader {
    static final String ONE = Character.toString('\u0001');
    V1Span.Builder builder = V1Span.newBuilder();

    public static V1ThriftSpanReader create() {
        return new V1ThriftSpanReader();
    }

    public V1Span read(ReadBuffer buffer) {
        if (this.builder == null) {
            this.builder = V1Span.newBuilder();
        } else {
            this.builder.clear();
        }
        block0: while (true) {
            int i;
            int length;
            ThriftField thriftField = ThriftField.read(buffer);
            if (thriftField.type == 0) break;
            if (thriftField.isEqualTo(V1ThriftSpanWriter.TRACE_ID_HIGH)) {
                this.builder.traceIdHigh(buffer.readLong());
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.TRACE_ID)) {
                this.builder.traceId(buffer.readLong());
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.NAME)) {
                this.builder.name(buffer.readUtf8(buffer.readInt()));
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.ID)) {
                this.builder.id(buffer.readLong());
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.PARENT_ID)) {
                this.builder.parentId(buffer.readLong());
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.ANNOTATIONS)) {
                length = ThriftCodec.readListLength(buffer);
                i = 0;
                while (true) {
                    if (i >= length) continue block0;
                    AnnotationReader.read(buffer, this.builder);
                    ++i;
                }
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.BINARY_ANNOTATIONS)) {
                length = ThriftCodec.readListLength(buffer);
                i = 0;
                while (true) {
                    if (i >= length) continue block0;
                    BinaryAnnotationReader.read(buffer, this.builder);
                    ++i;
                }
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.DEBUG)) {
                this.builder.debug(buffer.readByte() == 1);
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.TIMESTAMP)) {
                this.builder.timestamp(buffer.readLong());
                continue;
            }
            if (thriftField.isEqualTo(V1ThriftSpanWriter.DURATION)) {
                this.builder.duration(buffer.readLong());
                continue;
            }
            ThriftCodec.skip(buffer, thriftField.type);
        }
        return this.builder.build();
    }

    V1ThriftSpanReader() {
    }

    static final class BinaryAnnotationReader {
        static final ThriftField KEY = new ThriftField(11, 1);
        static final ThriftField VALUE = new ThriftField(11, 2);
        static final ThriftField TYPE = new ThriftField(8, 3);
        static final ThriftField ENDPOINT = new ThriftField(12, 4);

        BinaryAnnotationReader() {
        }

        static void read(ReadBuffer buffer, V1Span.Builder builder) {
            String key = null;
            String value = null;
            Endpoint endpoint = null;
            boolean isBoolean = false;
            boolean isString = false;
            while (true) {
                ThriftField thriftField = ThriftField.read(buffer);
                if (thriftField.type == 0) break;
                if (thriftField.isEqualTo(KEY)) {
                    key = buffer.readUtf8(buffer.readInt());
                    continue;
                }
                if (thriftField.isEqualTo(VALUE)) {
                    value = buffer.readUtf8(buffer.readInt());
                    continue;
                }
                if (thriftField.isEqualTo(TYPE)) {
                    switch (buffer.readInt()) {
                        case 0: {
                            isBoolean = true;
                            break;
                        }
                        case 6: {
                            isString = true;
                        }
                    }
                    continue;
                }
                if (thriftField.isEqualTo(ENDPOINT)) {
                    endpoint = ThriftEndpointCodec.read(buffer);
                    continue;
                }
                ThriftCodec.skip(buffer, thriftField.type);
            }
            if (key == null || value == null) {
                return;
            }
            if (isString) {
                builder.addBinaryAnnotation(key, value, endpoint);
            } else if (isBoolean && ONE.equals(value) && endpoint != null && (key.equals("sa") || key.equals("ca") || key.equals("ma"))) {
                builder.addBinaryAnnotation(key, endpoint);
            }
        }
    }

    static final class AnnotationReader {
        static final ThriftField TIMESTAMP = new ThriftField(10, 1);
        static final ThriftField VALUE = new ThriftField(11, 2);
        static final ThriftField ENDPOINT = new ThriftField(12, 3);

        AnnotationReader() {
        }

        static void read(ReadBuffer buffer, V1Span.Builder builder) {
            long timestamp = 0L;
            String value = null;
            Endpoint endpoint = null;
            while (true) {
                ThriftField thriftField = ThriftField.read(buffer);
                if (thriftField.type == 0) break;
                if (thriftField.isEqualTo(TIMESTAMP)) {
                    timestamp = buffer.readLong();
                    continue;
                }
                if (thriftField.isEqualTo(VALUE)) {
                    value = buffer.readUtf8(buffer.readInt());
                    continue;
                }
                if (thriftField.isEqualTo(ENDPOINT)) {
                    endpoint = ThriftEndpointCodec.read(buffer);
                    continue;
                }
                ThriftCodec.skip(buffer, thriftField.type);
            }
            if (timestamp == 0L || value == null) {
                return;
            }
            builder.addAnnotation(timestamp, value, endpoint);
        }
    }
}

