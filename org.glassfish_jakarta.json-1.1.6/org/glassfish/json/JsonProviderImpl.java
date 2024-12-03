/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonMergePatch;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;
import javax.json.JsonPointer;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import org.glassfish.json.BufferPoolImpl;
import org.glassfish.json.JsonArrayBuilderImpl;
import org.glassfish.json.JsonBuilderFactoryImpl;
import org.glassfish.json.JsonGeneratorFactoryImpl;
import org.glassfish.json.JsonGeneratorImpl;
import org.glassfish.json.JsonMergePatchImpl;
import org.glassfish.json.JsonNumberImpl;
import org.glassfish.json.JsonObjectBuilderImpl;
import org.glassfish.json.JsonParserFactoryImpl;
import org.glassfish.json.JsonParserImpl;
import org.glassfish.json.JsonPatchBuilderImpl;
import org.glassfish.json.JsonPatchImpl;
import org.glassfish.json.JsonPointerImpl;
import org.glassfish.json.JsonReaderFactoryImpl;
import org.glassfish.json.JsonReaderImpl;
import org.glassfish.json.JsonStringImpl;
import org.glassfish.json.JsonWriterFactoryImpl;
import org.glassfish.json.JsonWriterImpl;
import org.glassfish.json.api.BufferPool;

public class JsonProviderImpl
extends JsonProvider {
    private final BufferPool bufferPool = new BufferPoolImpl();

    @Override
    public JsonGenerator createGenerator(Writer writer) {
        return new JsonGeneratorImpl(writer, this.bufferPool);
    }

    @Override
    public JsonGenerator createGenerator(OutputStream out) {
        return new JsonGeneratorImpl(out, this.bufferPool);
    }

    @Override
    public JsonParser createParser(Reader reader) {
        return new JsonParserImpl(reader, this.bufferPool);
    }

    @Override
    public JsonParser createParser(InputStream in) {
        return new JsonParserImpl(in, this.bufferPool);
    }

    @Override
    public JsonParserFactory createParserFactory(Map<String, ?> config) {
        BufferPool pool = null;
        if (config != null && config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool)config.get(BufferPool.class.getName());
        }
        if (pool == null) {
            pool = this.bufferPool;
        }
        return new JsonParserFactoryImpl(pool);
    }

    @Override
    public JsonGeneratorFactory createGeneratorFactory(Map<String, ?> config) {
        BufferPool pool;
        boolean prettyPrinting;
        Map<String, Object> providerConfig;
        if (config == null) {
            providerConfig = Collections.emptyMap();
            prettyPrinting = false;
            pool = this.bufferPool;
        } else {
            providerConfig = new HashMap();
            prettyPrinting = JsonProviderImpl.isPrettyPrintingEnabled(config);
            if (prettyPrinting) {
                providerConfig.put("javax.json.stream.JsonGenerator.prettyPrinting", true);
            }
            if ((pool = (BufferPool)config.get(BufferPool.class.getName())) != null) {
                providerConfig.put(BufferPool.class.getName(), pool);
            } else {
                pool = this.bufferPool;
            }
            providerConfig = Collections.unmodifiableMap(providerConfig);
        }
        return new JsonGeneratorFactoryImpl(providerConfig, prettyPrinting, pool);
    }

    @Override
    public JsonReader createReader(Reader reader) {
        return new JsonReaderImpl(reader, this.bufferPool);
    }

    @Override
    public JsonReader createReader(InputStream in) {
        return new JsonReaderImpl(in, this.bufferPool);
    }

    @Override
    public JsonWriter createWriter(Writer writer) {
        return new JsonWriterImpl(writer, this.bufferPool);
    }

    @Override
    public JsonWriter createWriter(OutputStream out) {
        return new JsonWriterImpl(out, this.bufferPool);
    }

    @Override
    public JsonWriterFactory createWriterFactory(Map<String, ?> config) {
        BufferPool pool;
        boolean prettyPrinting;
        Map<String, Object> providerConfig;
        if (config == null) {
            providerConfig = Collections.emptyMap();
            prettyPrinting = false;
            pool = this.bufferPool;
        } else {
            providerConfig = new HashMap();
            prettyPrinting = JsonProviderImpl.isPrettyPrintingEnabled(config);
            if (prettyPrinting) {
                providerConfig.put("javax.json.stream.JsonGenerator.prettyPrinting", true);
            }
            if ((pool = (BufferPool)config.get(BufferPool.class.getName())) != null) {
                providerConfig.put(BufferPool.class.getName(), pool);
            } else {
                pool = this.bufferPool;
            }
            providerConfig = Collections.unmodifiableMap(providerConfig);
        }
        return new JsonWriterFactoryImpl(providerConfig, prettyPrinting, pool);
    }

    @Override
    public JsonReaderFactory createReaderFactory(Map<String, ?> config) {
        BufferPool pool = null;
        if (config != null && config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool)config.get(BufferPool.class.getName());
        }
        if (pool == null) {
            pool = this.bufferPool;
        }
        return new JsonReaderFactoryImpl(pool);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder() {
        return new JsonObjectBuilderImpl(this.bufferPool);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        return new JsonObjectBuilderImpl(object, this.bufferPool);
    }

    @Override
    public JsonObjectBuilder createObjectBuilder(Map<String, Object> map) {
        return new JsonObjectBuilderImpl(map, this.bufferPool);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder() {
        return new JsonArrayBuilderImpl(this.bufferPool);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        return new JsonArrayBuilderImpl(array, this.bufferPool);
    }

    @Override
    public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        return new JsonArrayBuilderImpl(collection, this.bufferPool);
    }

    @Override
    public JsonPointer createPointer(String jsonPointer) {
        return new JsonPointerImpl(jsonPointer);
    }

    @Override
    public JsonPatchBuilder createPatchBuilder() {
        return new JsonPatchBuilderImpl();
    }

    @Override
    public JsonPatchBuilder createPatchBuilder(JsonArray array) {
        return new JsonPatchBuilderImpl(array);
    }

    @Override
    public JsonPatch createPatch(JsonArray array) {
        return new JsonPatchImpl(array);
    }

    @Override
    public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        return new JsonPatchImpl(JsonPatchImpl.diff(source, target));
    }

    @Override
    public JsonMergePatch createMergePatch(JsonValue patch) {
        return new JsonMergePatchImpl(patch);
    }

    @Override
    public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
        return new JsonMergePatchImpl(JsonMergePatchImpl.diff(source, target));
    }

    @Override
    public JsonString createValue(String value) {
        return new JsonStringImpl(value);
    }

    @Override
    public JsonNumber createValue(int value) {
        return JsonNumberImpl.getJsonNumber(value);
    }

    @Override
    public JsonNumber createValue(long value) {
        return JsonNumberImpl.getJsonNumber(value);
    }

    @Override
    public JsonNumber createValue(double value) {
        return JsonNumberImpl.getJsonNumber(value);
    }

    @Override
    public JsonNumber createValue(BigInteger value) {
        return JsonNumberImpl.getJsonNumber(value);
    }

    @Override
    public JsonNumber createValue(BigDecimal value) {
        return JsonNumberImpl.getJsonNumber(value);
    }

    @Override
    public JsonBuilderFactory createBuilderFactory(Map<String, ?> config) {
        BufferPool pool = null;
        if (config != null && config.containsKey(BufferPool.class.getName())) {
            pool = (BufferPool)config.get(BufferPool.class.getName());
        }
        if (pool == null) {
            pool = this.bufferPool;
        }
        return new JsonBuilderFactoryImpl(pool);
    }

    static boolean isPrettyPrintingEnabled(Map<String, ?> config) {
        return config.containsKey("javax.json.stream.JsonGenerator.prettyPrinting");
    }
}

