/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Iterables
 *  io.atlassian.fugue.Iterables
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.marketplace.client.impl;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.impl.EntityEncoding;
import com.atlassian.marketplace.client.impl.SchemaViolationException;
import com.atlassian.marketplace.client.impl.TypeAdapters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.IOUtils;

public class JsonEntityEncoding
implements EntityEncoding {
    private final Gson gsonWithReadOnlyFields = this.makeGson(true);
    private final Gson gsonWithoutReadOnlyFields = this.makeGson(false);

    private Gson makeGson(boolean includeReadOnlyFields) {
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithModifiers(128).disableHtmlEscaping();
        for (Map.Entry<Class<?>, Object> e : TypeAdapters.all().entrySet()) {
            builder.registerTypeAdapter(e.getKey(), e.getValue());
        }
        return builder.registerTypeAdapterFactory(new BaseTypeAdapterFactory(includeReadOnlyFields)).create();
    }

    @Override
    public <T> T decode(InputStream stream, Class<T> type) throws MpacException {
        try {
            return this.gsonWithReadOnlyFields.fromJson((Reader)new InputStreamReader(stream), type);
        }
        catch (JsonParseException e) {
            throw JsonEntityEncoding.toMpacException(e);
        }
    }

    private static MpacException toMpacException(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }
        if (e instanceof MpacException) {
            return (MpacException)e;
        }
        if (e instanceof SchemaViolationException) {
            return new MpacException.InvalidResponseError(((SchemaViolationException)e).getSchemaViolations());
        }
        return new MpacException.InvalidResponseError(e.getMessage(), e);
    }

    @Override
    public <T> void encode(OutputStream stream, T entity, boolean includeReadOnlyFields) throws MpacException {
        Gson gson = includeReadOnlyFields ? this.gsonWithReadOnlyFields : this.gsonWithoutReadOnlyFields;
        OutputStreamWriter w = new OutputStreamWriter(stream);
        try {
            gson.toJson(entity, (Appendable)w);
        }
        catch (JsonIOException e) {
            throw new MpacException(e);
        }
        finally {
            IOUtils.closeQuietly((Writer)w);
        }
    }

    @Override
    public <T> void encodeChanges(OutputStream stream, T original, T updated) throws MpacException {
        JsonObject jOrig = this.gsonWithoutReadOnlyFields.toJsonTree(original).getAsJsonObject();
        JsonObject jUpdated = this.gsonWithoutReadOnlyFields.toJsonTree(updated).getAsJsonObject();
        JsonArray jResult = new JsonArray();
        for (JsonElement n : this.makeJsonPatch(jOrig, jUpdated, "")) {
            jResult.add(n);
        }
        OutputStreamWriter w = new OutputStreamWriter(stream);
        try {
            this.gsonWithoutReadOnlyFields.toJson((JsonElement)jResult, (Appendable)w);
        }
        catch (JsonIOException e) {
            throw new MpacException(e);
        }
        finally {
            IOUtils.closeQuietly((Writer)w);
        }
    }

    private Iterable<JsonElement> makeJsonPatch(JsonObject jOrig, JsonObject jUpdated, String basePath) {
        Iterable addsAndReplaces = io.atlassian.fugue.Iterables.flatMap(jUpdated.entrySet(), e -> {
            String name = (String)e.getKey();
            JsonElement n1 = (JsonElement)e.getValue();
            if (!n1.isJsonNull()) {
                String subPath = basePath + "/" + name;
                JsonElement n0 = jOrig.get(name);
                if (n0 != null && !n0.isJsonNull()) {
                    if (n0.isJsonObject() && n1.isJsonObject()) {
                        return this.makeJsonPatch(n0.getAsJsonObject(), n1.getAsJsonObject(), subPath);
                    }
                    if (!n0.equals(n1)) {
                        JsonObject op = new JsonObject();
                        op.addProperty("op", "replace");
                        op.addProperty("path", subPath);
                        op.add("value", n1);
                        return ImmutableList.of((Object)op);
                    }
                    return ImmutableList.of();
                }
                JsonObject op = new JsonObject();
                op.addProperty("op", "add");
                op.addProperty("path", subPath);
                op.add("value", n1);
                return ImmutableList.of((Object)op);
            }
            return ImmutableList.of();
        });
        Iterable removes = io.atlassian.fugue.Iterables.flatMap((Iterable)ImmutableList.copyOf(jOrig.entrySet()), e -> {
            JsonElement n1;
            String name = (String)e.getKey();
            JsonElement n0 = (JsonElement)e.getValue();
            if (!n0.isJsonNull() && ((n1 = jUpdated.get(name)) == null || n1.isJsonNull())) {
                JsonObject op = new JsonObject();
                op.addProperty("op", "remove");
                op.addProperty("path", basePath + "/" + name);
                return ImmutableList.of((Object)op);
            }
            return ImmutableList.of();
        });
        return Iterables.concat((Iterable)addsAndReplaces, (Iterable)removes);
    }

    static class BaseTypeAdapterFactory
    implements TypeAdapterFactory {
        private static final Set<Class<?>> TYPES_WITH_DEFAULT_SERIALIZATION = ImmutableSet.of(String.class, Boolean.class, Integer.class, Long.class, Float.class, Double.class, (Object[])new Class[0]);
        private final boolean includeReadOnlyFields;

        BaseTypeAdapterFactory(boolean includeReadOnlyFields) {
            this.includeReadOnlyFields = includeReadOnlyFields;
        }

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            Class<T> rawType = typeToken.getRawType();
            if (rawType.isPrimitive() || TYPES_WITH_DEFAULT_SERIALIZATION.contains(rawType) || Map.class.isAssignableFrom(rawType) || Collection.class.isAssignableFrom(rawType) || TypeAdapters.all().keySet().contains(rawType)) {
                return null;
            }
            if (rawType.isEnum()) {
                return TypeAdapters.enumTypeAdapter(rawType);
            }
            return TypeAdapters.objectTypeAdapter(gson, typeToken, this.includeReadOnlyFields);
        }
    }
}

