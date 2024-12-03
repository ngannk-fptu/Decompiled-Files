/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.AnonymousAndLocalClassExclusionStrategy;
import com.google.gson.DefaultTypeAdapters;
import com.google.gson.DisjunctionExclusionStrategy;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldNamingStrategy2;
import com.google.gson.JavaFieldNamingPolicy;
import com.google.gson.JsonDeserializationContextDefault;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContextDefault;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.MappedObjectConstructor;
import com.google.gson.ModifierBasedExclusionStrategy;
import com.google.gson.ObjectNavigator;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.Primitives;
import com.google.gson.SerializedNameAnnotationInterceptingNamingPolicy;
import com.google.gson.Streams;
import com.google.gson.SyntheticFieldExclusionStrategy;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.LinkedList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Gson {
    static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
    static final AnonymousAndLocalClassExclusionStrategy DEFAULT_ANON_LOCAL_CLASS_EXCLUSION_STRATEGY = new AnonymousAndLocalClassExclusionStrategy();
    static final SyntheticFieldExclusionStrategy DEFAULT_SYNTHETIC_FIELD_EXCLUSION_STRATEGY = new SyntheticFieldExclusionStrategy(true);
    static final ModifierBasedExclusionStrategy DEFAULT_MODIFIER_BASED_EXCLUSION_STRATEGY = new ModifierBasedExclusionStrategy(128, 8);
    static final FieldNamingStrategy2 DEFAULT_NAMING_POLICY = new SerializedNameAnnotationInterceptingNamingPolicy(new JavaFieldNamingPolicy());
    private static final ExclusionStrategy DEFAULT_EXCLUSION_STRATEGY = Gson.createExclusionStrategy();
    private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
    private final ExclusionStrategy deserializationExclusionStrategy;
    private final ExclusionStrategy serializationExclusionStrategy;
    private final FieldNamingStrategy2 fieldNamingPolicy;
    private final MappedObjectConstructor objectConstructor;
    private final ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers;
    private final ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers;
    private final boolean serializeNulls;
    private final boolean htmlSafe;
    private final boolean generateNonExecutableJson;
    private final boolean prettyPrinting;

    public Gson() {
        this(DEFAULT_EXCLUSION_STRATEGY, DEFAULT_EXCLUSION_STRATEGY, DEFAULT_NAMING_POLICY, new MappedObjectConstructor(DefaultTypeAdapters.getDefaultInstanceCreators()), false, DefaultTypeAdapters.getAllDefaultSerializers(), DefaultTypeAdapters.getAllDefaultDeserializers(), false, true, false);
    }

    Gson(ExclusionStrategy deserializationExclusionStrategy, ExclusionStrategy serializationExclusionStrategy, FieldNamingStrategy2 fieldNamingPolicy, MappedObjectConstructor objectConstructor, boolean serializeNulls, ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers, ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers, boolean generateNonExecutableGson, boolean htmlSafe, boolean prettyPrinting) {
        this.deserializationExclusionStrategy = deserializationExclusionStrategy;
        this.serializationExclusionStrategy = serializationExclusionStrategy;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.objectConstructor = objectConstructor;
        this.serializeNulls = serializeNulls;
        this.serializers = serializers;
        this.deserializers = deserializers;
        this.generateNonExecutableJson = generateNonExecutableGson;
        this.htmlSafe = htmlSafe;
        this.prettyPrinting = prettyPrinting;
    }

    private static ExclusionStrategy createExclusionStrategy() {
        LinkedList<ExclusionStrategy> strategies = new LinkedList<ExclusionStrategy>();
        strategies.add(DEFAULT_ANON_LOCAL_CLASS_EXCLUSION_STRATEGY);
        strategies.add(DEFAULT_SYNTHETIC_FIELD_EXCLUSION_STRATEGY);
        strategies.add(DEFAULT_MODIFIER_BASED_EXCLUSION_STRATEGY);
        return new DisjunctionExclusionStrategy(strategies);
    }

    public JsonElement toJsonTree(Object src) {
        if (src == null) {
            return JsonNull.createJsonNull();
        }
        return this.toJsonTree(src, src.getClass());
    }

    public JsonElement toJsonTree(Object src, Type typeOfSrc) {
        JsonSerializationContextDefault context = new JsonSerializationContextDefault(new ObjectNavigator(this.serializationExclusionStrategy), this.fieldNamingPolicy, this.serializeNulls, this.serializers);
        return context.serialize(src, typeOfSrc);
    }

    public String toJson(Object src) {
        if (src == null) {
            return this.toJson(JsonNull.createJsonNull());
        }
        return this.toJson(src, src.getClass());
    }

    public String toJson(Object src, Type typeOfSrc) {
        StringWriter writer = new StringWriter();
        this.toJson(this.toJsonTree(src, typeOfSrc), (Appendable)writer);
        return writer.toString();
    }

    public void toJson(Object src, Appendable writer) throws JsonIOException {
        if (src != null) {
            this.toJson(src, src.getClass(), writer);
        } else {
            this.toJson((JsonElement)JsonNull.createJsonNull(), writer);
        }
    }

    public void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException {
        JsonElement jsonElement = this.toJsonTree(src, typeOfSrc);
        this.toJson(jsonElement, writer);
    }

    public void toJson(Object src, Type typeOfSrc, JsonWriter writer) throws JsonIOException {
        this.toJson(this.toJsonTree(src, typeOfSrc), writer);
    }

    public String toJson(JsonElement jsonElement) {
        StringWriter writer = new StringWriter();
        this.toJson(jsonElement, (Appendable)writer);
        return writer.toString();
    }

    public void toJson(JsonElement jsonElement, Appendable writer) throws JsonIOException {
        try {
            if (this.generateNonExecutableJson) {
                writer.append(JSON_NON_EXECUTABLE_PREFIX);
            }
            JsonWriter jsonWriter = new JsonWriter(Streams.writerForAppendable(writer));
            if (this.prettyPrinting) {
                jsonWriter.setIndent("  ");
            }
            this.toJson(jsonElement, jsonWriter);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void toJson(JsonElement jsonElement, JsonWriter writer) throws JsonIOException {
        boolean oldLenient = writer.isLenient();
        writer.setLenient(true);
        boolean oldHtmlSafe = writer.isHtmlSafe();
        writer.setHtmlSafe(this.htmlSafe);
        try {
            Streams.write(jsonElement, this.serializeNulls, writer);
        }
        catch (IOException e) {
            throw new JsonIOException(e);
        }
        finally {
            writer.setLenient(oldLenient);
            writer.setHtmlSafe(oldHtmlSafe);
        }
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        T object = this.fromJson(json, (Type)classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    public <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }
        StringReader reader = new StringReader(json);
        T target = this.fromJson((Reader)reader, typeOfT);
        return target;
    }

    public <T> T fromJson(Reader json, Class<T> classOfT) throws JsonSyntaxException, JsonIOException {
        JsonReader jsonReader = new JsonReader(json);
        T object = this.fromJson(jsonReader, classOfT);
        Gson.assertFullConsumption(object, jsonReader);
        return Primitives.wrap(classOfT).cast(object);
    }

    public <T> T fromJson(Reader json, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        JsonReader jsonReader = new JsonReader(json);
        T object = this.fromJson(jsonReader, typeOfT);
        Gson.assertFullConsumption(object, jsonReader);
        return object;
    }

    private static void assertFullConsumption(Object obj, JsonReader reader) {
        try {
            if (obj != null && reader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
        }
        catch (MalformedJsonException e) {
            throw new JsonSyntaxException(e);
        }
        catch (IOException e) {
            throw new JsonIOException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T fromJson(JsonReader reader, Type typeOfT) throws JsonIOException, JsonSyntaxException {
        boolean oldLenient = reader.isLenient();
        reader.setLenient(true);
        try {
            JsonElement root = Streams.parse(reader);
            T t = this.fromJson(root, typeOfT);
            return t;
        }
        finally {
            reader.setLenient(oldLenient);
        }
    }

    public <T> T fromJson(JsonElement json, Class<T> classOfT) throws JsonSyntaxException {
        T object = this.fromJson(json, (Type)classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

    public <T> T fromJson(JsonElement json, Type typeOfT) throws JsonSyntaxException {
        if (json == null) {
            return null;
        }
        JsonDeserializationContextDefault context = new JsonDeserializationContextDefault(new ObjectNavigator(this.deserializationExclusionStrategy), this.fieldNamingPolicy, this.deserializers, this.objectConstructor);
        Object target = context.deserialize(json, typeOfT);
        return target;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("{").append("serializeNulls:").append(this.serializeNulls).append(",serializers:").append(this.serializers).append(",deserializers:").append(this.deserializers).append(",instanceCreators:").append(this.objectConstructor).append("}");
        return sb.toString();
    }
}

