/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.io.IOException;
import java.util.Collection;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.JsonCodec;
import zipkin2.internal.ReadBuffer;
import zipkin2.internal.V2SpanReader;
import zipkin2.v1.V1Span;
import zipkin2.v1.V1SpanConverter;

public final class V1JsonSpanReader
implements JsonCodec.JsonReaderAdapter<V1Span> {
    V1Span.Builder builder;

    public boolean readList(ReadBuffer buffer, Collection<Span> out) {
        if (buffer.available() == 0) {
            return false;
        }
        V1SpanConverter converter = V1SpanConverter.create();
        JsonCodec.JsonReader reader = new JsonCodec.JsonReader(buffer);
        try {
            reader.beginArray();
            if (!reader.hasNext()) {
                return false;
            }
            while (reader.hasNext()) {
                V1Span result = this.fromJson(reader);
                converter.convert(result, out);
            }
            reader.endArray();
            return true;
        }
        catch (Exception e) {
            throw JsonCodec.exceptionReading("List<Span>", e);
        }
    }

    @Override
    public V1Span fromJson(JsonCodec.JsonReader reader) throws IOException {
        if (this.builder == null) {
            this.builder = V1Span.newBuilder();
        } else {
            this.builder.clear();
        }
        reader.beginObject();
        while (reader.hasNext()) {
            String nextName = reader.nextName();
            if (nextName.equals("traceId")) {
                this.builder.traceId(reader.nextString());
                continue;
            }
            if (nextName.equals("id")) {
                this.builder.id(reader.nextString());
                continue;
            }
            if (reader.peekNull()) {
                reader.skipValue();
                continue;
            }
            if (nextName.equals("name")) {
                this.builder.name(reader.nextString());
                continue;
            }
            if (nextName.equals("parentId")) {
                this.builder.parentId(reader.nextString());
                continue;
            }
            if (nextName.equals("timestamp")) {
                this.builder.timestamp(reader.nextLong());
                continue;
            }
            if (nextName.equals("duration")) {
                this.builder.duration(reader.nextLong());
                continue;
            }
            if (nextName.equals("annotations")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    this.readAnnotation(reader);
                }
                reader.endArray();
                continue;
            }
            if (nextName.equals("binaryAnnotations")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    this.readBinaryAnnotation(reader);
                }
                reader.endArray();
                continue;
            }
            if (nextName.equals("debug")) {
                if (!reader.nextBoolean()) continue;
                this.builder.debug(true);
                continue;
            }
            reader.skipValue();
        }
        reader.endObject();
        return this.builder.build();
    }

    void readAnnotation(JsonCodec.JsonReader reader) throws IOException {
        reader.beginObject();
        Long timestamp = null;
        String value = null;
        Endpoint endpoint = null;
        while (reader.hasNext()) {
            String nextName = reader.nextName();
            if (nextName.equals("timestamp")) {
                timestamp = reader.nextLong();
                continue;
            }
            if (nextName.equals("value")) {
                value = reader.nextString();
                continue;
            }
            if (nextName.equals("endpoint") && !reader.peekNull()) {
                endpoint = V2SpanReader.ENDPOINT_READER.fromJson(reader);
                continue;
            }
            reader.skipValue();
        }
        if (timestamp == null || value == null) {
            throw new IllegalArgumentException("Incomplete annotation at " + reader.getPath());
        }
        reader.endObject();
        this.builder.addAnnotation(timestamp, value, endpoint);
    }

    public String toString() {
        return "Span";
    }

    void readBinaryAnnotation(JsonCodec.JsonReader reader) throws IOException {
        String key = null;
        Endpoint endpoint = null;
        Boolean booleanValue = null;
        String stringValue = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String nextName = reader.nextName();
            if (reader.peekNull()) {
                reader.skipValue();
                continue;
            }
            if (nextName.equals("key")) {
                key = reader.nextString();
                continue;
            }
            if (nextName.equals("value")) {
                if (reader.peekString()) {
                    stringValue = reader.nextString();
                    continue;
                }
                if (reader.peekBoolean()) {
                    booleanValue = reader.nextBoolean();
                    continue;
                }
                reader.skipValue();
                continue;
            }
            if (nextName.equals("endpoint")) {
                endpoint = V2SpanReader.ENDPOINT_READER.fromJson(reader);
                continue;
            }
            reader.skipValue();
        }
        if (key == null) {
            throw new IllegalArgumentException("No key at " + reader.getPath());
        }
        reader.endObject();
        if (stringValue != null) {
            this.builder.addBinaryAnnotation(key, stringValue, endpoint);
        } else if (booleanValue != null && booleanValue.booleanValue() && endpoint != null && (key.equals("sa") || key.equals("ca") || key.equals("ma"))) {
            this.builder.addBinaryAnnotation(key, endpoint);
        }
    }
}

