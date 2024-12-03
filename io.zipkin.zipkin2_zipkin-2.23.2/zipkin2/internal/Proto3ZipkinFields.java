/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import zipkin2.Annotation;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.Proto3Fields;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.WriteBuffer;

final class Proto3ZipkinFields {
    static final Logger LOG = Logger.getLogger(Proto3ZipkinFields.class.getName());
    static final SpanField SPAN = new SpanField();

    Proto3ZipkinFields() {
    }

    static void logAndSkip(ReadBuffer buffer, int nextKey) {
        int nextWireType = Proto3Fields.Field.wireType(nextKey, buffer.pos());
        if (LOG.isLoggable(Level.FINE)) {
            int nextFieldNumber = Proto3Fields.Field.fieldNumber(nextKey, buffer.pos());
            LOG.fine(String.format("Skipping field: byte=%s, fieldNumber=%s, wireType=%s", buffer.pos(), nextFieldNumber, nextWireType));
        }
        Proto3Fields.Field.skipValue(buffer, nextWireType);
    }

    static class SpanField
    extends Proto3Fields.LengthDelimitedField<Span> {
        static final int TRACE_ID_KEY = 10;
        static final int PARENT_ID_KEY = 18;
        static final int ID_KEY = 26;
        static final int KIND_KEY = 32;
        static final int NAME_KEY = 42;
        static final int TIMESTAMP_KEY = 49;
        static final int DURATION_KEY = 56;
        static final int LOCAL_ENDPOINT_KEY = 66;
        static final int REMOTE_ENDPOINT_KEY = 74;
        static final int ANNOTATION_KEY = 82;
        static final int TAG_KEY = 90;
        static final int DEBUG_KEY = 96;
        static final int SHARED_KEY = 104;
        static final Proto3Fields.HexField TRACE_ID = new Proto3Fields.HexField(10);
        static final Proto3Fields.HexField PARENT_ID = new Proto3Fields.HexField(18);
        static final Proto3Fields.HexField ID = new Proto3Fields.HexField(26);
        static final Proto3Fields.VarintField KIND = new Proto3Fields.VarintField(32);
        static final Proto3Fields.Utf8Field NAME = new Proto3Fields.Utf8Field(42);
        static final Proto3Fields.Fixed64Field TIMESTAMP = new Proto3Fields.Fixed64Field(49);
        static final Proto3Fields.VarintField DURATION = new Proto3Fields.VarintField(56);
        static final EndpointField LOCAL_ENDPOINT = new EndpointField(66);
        static final EndpointField REMOTE_ENDPOINT = new EndpointField(74);
        static final AnnotationField ANNOTATION = new AnnotationField(82);
        static final TagField TAG = new TagField(90);
        static final Proto3Fields.BooleanField DEBUG = new Proto3Fields.BooleanField(96);
        static final Proto3Fields.BooleanField SHARED = new Proto3Fields.BooleanField(104);

        SpanField() {
            super(10);
        }

        @Override
        int sizeOfValue(Span span) {
            int sizeOfSpan = TRACE_ID.sizeInBytes(span.traceId());
            sizeOfSpan += PARENT_ID.sizeInBytes(span.parentId());
            sizeOfSpan += ID.sizeInBytes(span.id());
            sizeOfSpan += KIND.sizeInBytes(span.kind() != null ? 1 : 0);
            sizeOfSpan += NAME.sizeInBytes(span.name());
            sizeOfSpan += TIMESTAMP.sizeInBytes(span.timestampAsLong());
            sizeOfSpan += DURATION.sizeInBytes(span.durationAsLong());
            sizeOfSpan += LOCAL_ENDPOINT.sizeInBytes(span.localEndpoint());
            sizeOfSpan += REMOTE_ENDPOINT.sizeInBytes(span.remoteEndpoint());
            List<Annotation> annotations = span.annotations();
            int annotationCount = annotations.size();
            for (int i = 0; i < annotationCount; ++i) {
                sizeOfSpan += ANNOTATION.sizeInBytes(annotations.get(i));
            }
            Map<String, String> tags = span.tags();
            int tagCount = tags.size();
            if (tagCount > 0) {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    sizeOfSpan += TAG.sizeInBytes(entry);
                }
            }
            sizeOfSpan += DEBUG.sizeInBytes(Boolean.TRUE.equals(span.debug()));
            return sizeOfSpan += SHARED.sizeInBytes(Boolean.TRUE.equals(span.shared()));
        }

        @Override
        void writeValue(WriteBuffer b, Span value) {
            TRACE_ID.write(b, value.traceId());
            PARENT_ID.write(b, value.parentId());
            ID.write(b, value.id());
            KIND.write(b, this.toByte(value.kind()));
            NAME.write(b, value.name());
            TIMESTAMP.write(b, value.timestampAsLong());
            DURATION.write(b, value.durationAsLong());
            LOCAL_ENDPOINT.write(b, value.localEndpoint());
            REMOTE_ENDPOINT.write(b, value.remoteEndpoint());
            List<Annotation> annotations = value.annotations();
            int annotationLength = annotations.size();
            for (int i = 0; i < annotationLength; ++i) {
                ANNOTATION.write(b, annotations.get(i));
            }
            Map<String, String> tags = value.tags();
            if (!tags.isEmpty()) {
                for (Map.Entry<String, String> entry : tags.entrySet()) {
                    TAG.write(b, entry);
                }
            }
            DEBUG.write(b, Boolean.TRUE.equals(value.debug()));
            SHARED.write(b, Boolean.TRUE.equals(value.shared()));
        }

        int toByte(Span.Kind kind) {
            return kind != null ? kind.ordinal() + 1 : 0;
        }

        public Span read(ReadBuffer buffer) {
            buffer.readVarint32();
            return (Span)this.readLengthPrefixAndValue(buffer);
        }

        @Override
        Span readValue(ReadBuffer buffer, int length) {
            buffer.require(length);
            int endPos = buffer.pos() + length;
            Span.Builder builder = Span.newBuilder();
            block15: while (buffer.pos() < endPos) {
                int nextKey = buffer.readVarint32();
                switch (nextKey) {
                    case 10: {
                        builder.traceId((String)TRACE_ID.readLengthPrefixAndValue(buffer));
                        break;
                    }
                    case 18: {
                        builder.parentId((String)PARENT_ID.readLengthPrefixAndValue(buffer));
                        break;
                    }
                    case 26: {
                        builder.id((String)ID.readLengthPrefixAndValue(buffer));
                        break;
                    }
                    case 32: {
                        int kind = buffer.readVarint32();
                        if (kind == 0 || kind > Span.Kind.values().length) break;
                        builder.kind(Span.Kind.values()[kind - 1]);
                        break;
                    }
                    case 42: {
                        builder.name((String)NAME.readLengthPrefixAndValue(buffer));
                        break;
                    }
                    case 49: {
                        builder.timestamp(TIMESTAMP.readValue(buffer));
                        break;
                    }
                    case 56: {
                        builder.duration(buffer.readVarint64());
                        break;
                    }
                    case 66: {
                        builder.localEndpoint((Endpoint)LOCAL_ENDPOINT.readLengthPrefixAndValue(buffer));
                        break;
                    }
                    case 74: {
                        builder.remoteEndpoint((Endpoint)REMOTE_ENDPOINT.readLengthPrefixAndValue(buffer));
                        break;
                    }
                    case 82: {
                        ANNOTATION.readLengthPrefixAndValue(buffer, builder);
                        break;
                    }
                    case 90: {
                        TAG.readLengthPrefixAndValue(buffer, builder);
                        break;
                    }
                    case 96: {
                        if (!DEBUG.read(buffer)) continue block15;
                        builder.debug(true);
                        break;
                    }
                    case 104: {
                        if (!SHARED.read(buffer)) continue block15;
                        builder.shared(true);
                        break;
                    }
                    default: {
                        Proto3ZipkinFields.logAndSkip(buffer, nextKey);
                    }
                }
            }
            return builder.build();
        }
    }

    static final class TagField
    extends SpanBuilderField<Map.Entry<String, String>> {
        static final int KEY_KEY = 10;
        static final int VALUE_KEY = 18;
        static final Proto3Fields.Utf8Field KEY = new Proto3Fields.Utf8Field(10);
        static final Proto3Fields.Utf8Field VALUE = new Proto3Fields.Utf8Field(18);

        TagField(int key) {
            super(key);
        }

        @Override
        int sizeOfValue(Map.Entry<String, String> value) {
            return KEY.sizeInBytes(value.getKey()) + VALUE.sizeInBytes(value.getValue());
        }

        @Override
        void writeValue(WriteBuffer b, Map.Entry<String, String> value) {
            KEY.write(b, value.getKey());
            VALUE.write(b, value.getValue());
        }

        @Override
        boolean readLengthPrefixAndValue(ReadBuffer b, Span.Builder builder) {
            int length = b.readVarint32();
            if (length == 0) {
                return false;
            }
            int endPos = b.pos() + length;
            String key = null;
            String value = "";
            block4: while (b.pos() < endPos) {
                int nextKey = b.readVarint32();
                switch (nextKey) {
                    case 10: {
                        key = (String)KEY.readLengthPrefixAndValue(b);
                        continue block4;
                    }
                    case 18: {
                        String read = (String)VALUE.readLengthPrefixAndValue(b);
                        if (read == null) continue block4;
                        value = read;
                        continue block4;
                    }
                }
                Proto3ZipkinFields.logAndSkip(b, nextKey);
            }
            if (key == null) {
                return false;
            }
            builder.putTag(key, value);
            return true;
        }
    }

    static class AnnotationField
    extends SpanBuilderField<Annotation> {
        static final int TIMESTAMP_KEY = 9;
        static final int VALUE_KEY = 18;
        static final Proto3Fields.Fixed64Field TIMESTAMP = new Proto3Fields.Fixed64Field(9);
        static final Proto3Fields.Utf8Field VALUE = new Proto3Fields.Utf8Field(18);

        AnnotationField(int key) {
            super(key);
        }

        @Override
        int sizeOfValue(Annotation value) {
            return TIMESTAMP.sizeInBytes(value.timestamp()) + VALUE.sizeInBytes(value.value());
        }

        @Override
        void writeValue(WriteBuffer b, Annotation value) {
            TIMESTAMP.write(b, value.timestamp());
            VALUE.write(b, value.value());
        }

        @Override
        boolean readLengthPrefixAndValue(ReadBuffer b, Span.Builder builder) {
            int length = b.readVarint32();
            if (length == 0) {
                return false;
            }
            int endPos = b.pos() + length;
            long timestamp = 0L;
            String value = null;
            block4: while (b.pos() < endPos) {
                int nextKey = b.readVarint32();
                switch (nextKey) {
                    case 9: {
                        timestamp = TIMESTAMP.readValue(b);
                        continue block4;
                    }
                    case 18: {
                        value = (String)VALUE.readLengthPrefixAndValue(b);
                        continue block4;
                    }
                }
                Proto3ZipkinFields.logAndSkip(b, nextKey);
            }
            if (timestamp == 0L || value == null) {
                return false;
            }
            builder.addAnnotation(timestamp, value);
            return true;
        }
    }

    static abstract class SpanBuilderField<T>
    extends Proto3Fields.LengthDelimitedField<T> {
        SpanBuilderField(int key) {
            super(key);
        }

        @Override
        final T readValue(ReadBuffer b, int length) {
            throw new UnsupportedOperationException();
        }

        abstract boolean readLengthPrefixAndValue(ReadBuffer var1, Span.Builder var2);
    }

    static class EndpointField
    extends Proto3Fields.LengthDelimitedField<Endpoint> {
        static final int SERVICE_NAME_KEY = 10;
        static final int IPV4_KEY = 18;
        static final int IPV6_KEY = 26;
        static final int PORT_KEY = 32;
        static final Proto3Fields.Utf8Field SERVICE_NAME = new Proto3Fields.Utf8Field(10);
        static final Proto3Fields.BytesField IPV4 = new Proto3Fields.BytesField(18);
        static final Proto3Fields.BytesField IPV6 = new Proto3Fields.BytesField(26);
        static final Proto3Fields.VarintField PORT = new Proto3Fields.VarintField(32);

        EndpointField(int key) {
            super(key);
        }

        @Override
        int sizeOfValue(Endpoint value) {
            int result = 0;
            result += SERVICE_NAME.sizeInBytes(value.serviceName());
            result += IPV4.sizeInBytes(value.ipv4Bytes());
            result += IPV6.sizeInBytes(value.ipv6Bytes());
            return result += PORT.sizeInBytes(value.portAsInt());
        }

        @Override
        void writeValue(WriteBuffer b, Endpoint value) {
            SERVICE_NAME.write(b, value.serviceName());
            IPV4.write(b, value.ipv4Bytes());
            IPV6.write(b, value.ipv6Bytes());
            PORT.write(b, value.portAsInt());
        }

        @Override
        Endpoint readValue(ReadBuffer buffer, int length) {
            int endPos = buffer.pos() + length;
            Endpoint.Builder builder = Endpoint.newBuilder();
            block6: while (buffer.pos() < endPos) {
                int nextKey = buffer.readVarint32();
                switch (nextKey) {
                    case 10: {
                        builder.serviceName((String)SERVICE_NAME.readLengthPrefixAndValue(buffer));
                        continue block6;
                    }
                    case 18: {
                        builder.parseIp((byte[])IPV4.readLengthPrefixAndValue(buffer));
                        continue block6;
                    }
                    case 26: {
                        builder.parseIp((byte[])IPV6.readLengthPrefixAndValue(buffer));
                        continue block6;
                    }
                    case 32: {
                        builder.port(buffer.readVarint32());
                        continue block6;
                    }
                }
                Proto3ZipkinFields.logAndSkip(buffer, nextKey);
            }
            return builder.build();
        }
    }
}

