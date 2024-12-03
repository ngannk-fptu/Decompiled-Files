/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.internal;

import java.io.IOException;
import zipkin2.Endpoint;
import zipkin2.Span;
import zipkin2.internal.JsonCodec;

public final class V2SpanReader
implements JsonCodec.JsonReaderAdapter<Span> {
    Span.Builder builder;
    static final JsonCodec.JsonReaderAdapter<Endpoint> ENDPOINT_READER = new JsonCodec.JsonReaderAdapter<Endpoint>(){

        @Override
        public Endpoint fromJson(JsonCodec.JsonReader reader) throws IOException {
            Endpoint.Builder result = Endpoint.newBuilder();
            reader.beginObject();
            boolean readField = false;
            while (reader.hasNext()) {
                String nextName = reader.nextName();
                if (reader.peekNull()) {
                    reader.skipValue();
                    continue;
                }
                if (nextName.equals("serviceName")) {
                    result.serviceName(reader.nextString());
                    readField = true;
                    continue;
                }
                if (nextName.equals("ipv4") || nextName.equals("ipv6")) {
                    result.parseIp(reader.nextString());
                    readField = true;
                    continue;
                }
                if (nextName.equals("port")) {
                    result.port(reader.nextInt());
                    readField = true;
                    continue;
                }
                reader.skipValue();
            }
            reader.endObject();
            return readField ? result.build() : null;
        }

        public String toString() {
            return "Endpoint";
        }
    };

    @Override
    public Span fromJson(JsonCodec.JsonReader reader) throws IOException {
        if (this.builder == null) {
            this.builder = Span.newBuilder();
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
            if (nextName.equals("parentId")) {
                this.builder.parentId(reader.nextString());
                continue;
            }
            if (nextName.equals("kind")) {
                this.builder.kind(Span.Kind.valueOf(reader.nextString()));
                continue;
            }
            if (nextName.equals("name")) {
                this.builder.name(reader.nextString());
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
            if (nextName.equals("localEndpoint")) {
                this.builder.localEndpoint(ENDPOINT_READER.fromJson(reader));
                continue;
            }
            if (nextName.equals("remoteEndpoint")) {
                this.builder.remoteEndpoint(ENDPOINT_READER.fromJson(reader));
                continue;
            }
            if (nextName.equals("annotations")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    Long timestamp = null;
                    String value = null;
                    while (reader.hasNext()) {
                        nextName = reader.nextName();
                        if (nextName.equals("timestamp")) {
                            timestamp = reader.nextLong();
                            continue;
                        }
                        if (nextName.equals("value")) {
                            value = reader.nextString();
                            continue;
                        }
                        reader.skipValue();
                    }
                    if (timestamp == null || value == null) {
                        throw new IllegalArgumentException("Incomplete annotation at " + reader.getPath());
                    }
                    reader.endObject();
                    this.builder.addAnnotation(timestamp, value);
                }
                reader.endArray();
                continue;
            }
            if (nextName.equals("tags")) {
                reader.beginObject();
                while (reader.hasNext()) {
                    String key = reader.nextName();
                    if (reader.peekNull()) {
                        throw new IllegalArgumentException("No value at " + reader.getPath());
                    }
                    this.builder.putTag(key, reader.nextString());
                }
                reader.endObject();
                continue;
            }
            if (nextName.equals("debug")) {
                if (!reader.nextBoolean()) continue;
                this.builder.debug(true);
                continue;
            }
            if (nextName.equals("shared")) {
                if (!reader.nextBoolean()) continue;
                this.builder.shared(true);
                continue;
            }
            reader.skipValue();
        }
        reader.endObject();
        return this.builder.build();
    }

    public String toString() {
        return "Span";
    }
}

