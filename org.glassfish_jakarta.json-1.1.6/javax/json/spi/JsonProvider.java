/*
 * Decompiled with CFR 0.152.
 */
package javax.json.spi;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonException;
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
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

public abstract class JsonProvider {
    private static final String DEFAULT_PROVIDER = "org.glassfish.json.JsonProviderImpl";

    protected JsonProvider() {
    }

    public static JsonProvider provider() {
        ServiceLoader<JsonProvider> loader = ServiceLoader.load(JsonProvider.class);
        Iterator<JsonProvider> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        try {
            Class<?> clazz = Class.forName(DEFAULT_PROVIDER);
            return (JsonProvider)clazz.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new JsonException("Provider org.glassfish.json.JsonProviderImpl not found", x);
        }
        catch (Exception x) {
            throw new JsonException("Provider org.glassfish.json.JsonProviderImpl could not be instantiated: " + x, x);
        }
    }

    public abstract JsonParser createParser(Reader var1);

    public abstract JsonParser createParser(InputStream var1);

    public abstract JsonParserFactory createParserFactory(Map<String, ?> var1);

    public abstract JsonGenerator createGenerator(Writer var1);

    public abstract JsonGenerator createGenerator(OutputStream var1);

    public abstract JsonGeneratorFactory createGeneratorFactory(Map<String, ?> var1);

    public abstract JsonReader createReader(Reader var1);

    public abstract JsonReader createReader(InputStream var1);

    public abstract JsonWriter createWriter(Writer var1);

    public abstract JsonWriter createWriter(OutputStream var1);

    public abstract JsonWriterFactory createWriterFactory(Map<String, ?> var1);

    public abstract JsonReaderFactory createReaderFactory(Map<String, ?> var1);

    public abstract JsonObjectBuilder createObjectBuilder();

    public JsonObjectBuilder createObjectBuilder(JsonObject object) {
        throw new UnsupportedOperationException();
    }

    public JsonObjectBuilder createObjectBuilder(Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

    public abstract JsonArrayBuilder createArrayBuilder();

    public JsonArrayBuilder createArrayBuilder(JsonArray array) {
        throw new UnsupportedOperationException();
    }

    public JsonPointer createPointer(String jsonPointer) {
        throw new UnsupportedOperationException();
    }

    public JsonPatchBuilder createPatchBuilder() {
        throw new UnsupportedOperationException();
    }

    public JsonPatchBuilder createPatchBuilder(JsonArray array) {
        throw new UnsupportedOperationException();
    }

    public JsonPatch createPatch(JsonArray array) {
        throw new UnsupportedOperationException();
    }

    public JsonPatch createDiff(JsonStructure source, JsonStructure target) {
        throw new UnsupportedOperationException();
    }

    public JsonMergePatch createMergePatch(JsonValue patch) {
        throw new UnsupportedOperationException();
    }

    public JsonMergePatch createMergeDiff(JsonValue source, JsonValue target) {
        throw new UnsupportedOperationException();
    }

    public JsonArrayBuilder createArrayBuilder(Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    public abstract JsonBuilderFactory createBuilderFactory(Map<String, ?> var1);

    public JsonString createValue(String value) {
        throw new UnsupportedOperationException();
    }

    public JsonNumber createValue(int value) {
        throw new UnsupportedOperationException();
    }

    public JsonNumber createValue(long value) {
        throw new UnsupportedOperationException();
    }

    public JsonNumber createValue(double value) {
        throw new UnsupportedOperationException();
    }

    public JsonNumber createValue(BigDecimal value) {
        throw new UnsupportedOperationException();
    }

    public JsonNumber createValue(BigInteger value) {
        throw new UnsupportedOperationException();
    }
}

