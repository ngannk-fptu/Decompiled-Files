/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.data;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.ResolvingDecoder;
import org.apache.avro.util.internal.JacksonUtils;

public class Json {
    static final JsonFactory FACTORY = new JsonFactory();
    static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);
    public static final Schema SCHEMA;

    private Json() {
    }

    public static Object parseJson(String s) {
        try {
            return JacksonUtils.toObject((JsonNode)MAPPER.readTree(FACTORY.createParser(s)));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toString(Object datum) {
        return JacksonUtils.toJsonNode(datum).toString();
    }

    private static void write(JsonNode node, Encoder out) throws IOException {
        switch (node.asToken()) {
            case VALUE_NUMBER_INT: {
                out.writeIndex(JsonType.LONG.ordinal());
                out.writeLong(node.longValue());
                break;
            }
            case VALUE_NUMBER_FLOAT: {
                out.writeIndex(JsonType.DOUBLE.ordinal());
                out.writeDouble(node.doubleValue());
                break;
            }
            case VALUE_STRING: {
                out.writeIndex(JsonType.STRING.ordinal());
                out.writeString(node.textValue());
                break;
            }
            case VALUE_TRUE: {
                out.writeIndex(JsonType.BOOLEAN.ordinal());
                out.writeBoolean(true);
                break;
            }
            case VALUE_FALSE: {
                out.writeIndex(JsonType.BOOLEAN.ordinal());
                out.writeBoolean(false);
                break;
            }
            case VALUE_NULL: {
                out.writeIndex(JsonType.NULL.ordinal());
                out.writeNull();
                break;
            }
            case START_ARRAY: {
                out.writeIndex(JsonType.ARRAY.ordinal());
                out.writeArrayStart();
                out.setItemCount(node.size());
                for (JsonNode element : node) {
                    out.startItem();
                    Json.write(element, out);
                }
                out.writeArrayEnd();
                break;
            }
            case START_OBJECT: {
                out.writeIndex(JsonType.OBJECT.ordinal());
                out.writeMapStart();
                out.setItemCount(node.size());
                Iterator<String> i = node.fieldNames();
                while (i.hasNext()) {
                    out.startItem();
                    String name = i.next();
                    out.writeString(name);
                    Json.write(node.get(name), out);
                }
                out.writeMapEnd();
                break;
            }
            default: {
                throw new AvroRuntimeException((Object)((Object)node.asToken()) + " unexpected: " + node);
            }
        }
    }

    private static JsonNode read(Decoder in) throws IOException {
        switch (JsonType.values()[in.readIndex()]) {
            case LONG: {
                return new LongNode(in.readLong());
            }
            case DOUBLE: {
                return new DoubleNode(in.readDouble());
            }
            case STRING: {
                return new TextNode(in.readString());
            }
            case BOOLEAN: {
                return in.readBoolean() ? BooleanNode.TRUE : BooleanNode.FALSE;
            }
            case NULL: {
                in.readNull();
                return NullNode.getInstance();
            }
            case ARRAY: {
                ArrayNode array = JsonNodeFactory.instance.arrayNode();
                long l = in.readArrayStart();
                while (l > 0L) {
                    for (long i = 0L; i < l; ++i) {
                        array.add(Json.read(in));
                    }
                    l = in.arrayNext();
                }
                return array;
            }
            case OBJECT: {
                ObjectNode object = JsonNodeFactory.instance.objectNode();
                long l = in.readMapStart();
                while (l > 0L) {
                    for (long i = 0L; i < l; ++i) {
                        object.set(in.readString(), Json.read(in));
                    }
                    l = in.mapNext();
                }
                return object;
            }
        }
        throw new AvroRuntimeException("Unexpected Json node type");
    }

    private static void writeObject(Object datum, Encoder out) throws IOException {
        Json.write(JacksonUtils.toJsonNode(datum), out);
    }

    private static Object readObject(Decoder in) throws IOException {
        return JacksonUtils.toObject(Json.read(in));
    }

    static {
        try (InputStream in = Json.class.getResourceAsStream("/org/apache/avro/data/Json.avsc");){
            SCHEMA = new Schema.Parser().parse(in);
        }
        catch (IOException e) {
            throw new AvroRuntimeException(e);
        }
    }

    private static enum JsonType {
        LONG,
        DOUBLE,
        STRING,
        BOOLEAN,
        NULL,
        ARRAY,
        OBJECT;

    }

    public static class ObjectReader
    implements DatumReader<Object> {
        private Schema written;
        private ResolvingDecoder resolver;

        @Override
        public void setSchema(Schema schema) {
            this.written = SCHEMA.equals(this.written) ? null : schema;
        }

        @Override
        public Object read(Object reuse, Decoder in) throws IOException {
            if (this.written == null) {
                return Json.readObject(in);
            }
            if (this.resolver == null) {
                this.resolver = DecoderFactory.get().resolvingDecoder(this.written, SCHEMA, null);
            }
            this.resolver.configure(in);
            Object result = Json.readObject(this.resolver);
            this.resolver.drain();
            return result;
        }
    }

    public static class ObjectWriter
    implements DatumWriter<Object> {
        @Override
        public void setSchema(Schema schema) {
            if (!SCHEMA.equals(schema)) {
                throw new RuntimeException("Not the Json schema: " + schema);
            }
        }

        @Override
        public void write(Object datum, Encoder out) throws IOException {
            Json.writeObject(datum, out);
        }
    }
}

