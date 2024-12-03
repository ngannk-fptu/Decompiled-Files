/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapterRuntimeTypeWrapper;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory
implements TypeAdapterFactory {
    private final ConstructorConstructor constructorConstructor;
    private final FieldNamingStrategy fieldNamingPolicy;
    private final Excluder excluder;
    private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
    private final List<ReflectionAccessFilter> reflectionFilters;

    public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder, JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory, List<ReflectionAccessFilter> reflectionFilters) {
        this.constructorConstructor = constructorConstructor;
        this.fieldNamingPolicy = fieldNamingPolicy;
        this.excluder = excluder;
        this.jsonAdapterFactory = jsonAdapterFactory;
        this.reflectionFilters = reflectionFilters;
    }

    private boolean includeField(Field f, boolean serialize) {
        return !this.excluder.excludeClass(f.getType(), serialize) && !this.excluder.excludeField(f, serialize);
    }

    private List<String> getFieldNames(Field f) {
        SerializedName annotation = f.getAnnotation(SerializedName.class);
        if (annotation == null) {
            String name = this.fieldNamingPolicy.translateName(f);
            return Collections.singletonList(name);
        }
        String serializedName = annotation.value();
        String[] alternates = annotation.alternate();
        if (alternates.length == 0) {
            return Collections.singletonList(serializedName);
        }
        ArrayList<String> fieldNames = new ArrayList<String>(alternates.length + 1);
        fieldNames.add(serializedName);
        for (String alternate : alternates) {
            fieldNames.add(alternate);
        }
        return fieldNames;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> raw = type.getRawType();
        if (!Object.class.isAssignableFrom(raw)) {
            return null;
        }
        ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
        if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
            throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + raw + ". Register a TypeAdapter for this type or adjust the access filter.");
        }
        boolean blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
        ObjectConstructor<T> constructor = this.constructorConstructor.get(type);
        return new Adapter<T>(constructor, this.getBoundFields(gson, type, raw, blockInaccessible));
    }

    private static void checkAccessible(Object object, Field field) {
        if (!ReflectionAccessFilterHelper.canAccess(field, Modifier.isStatic(field.getModifiers()) ? null : object)) {
            throw new JsonIOException("Field '" + field.getDeclaringClass().getName() + "#" + field.getName() + "' is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type or adjust the access filter.");
        }
    }

    private BoundField createBoundField(final Gson context, final Field field, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize, final boolean blockInaccessible) {
        boolean jsonAdapterPresent;
        final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
        JsonAdapter annotation = field.getAnnotation(JsonAdapter.class);
        TypeAdapter<?> mapped = null;
        if (annotation != null) {
            mapped = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, context, fieldType, annotation);
        }
        boolean bl = jsonAdapterPresent = mapped != null;
        if (mapped == null) {
            mapped = context.getAdapter(fieldType);
        }
        final TypeAdapter<?> typeAdapter = mapped;
        return new BoundField(name, serialize, deserialize){

            @Override
            void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
                Object fieldValue;
                if (!this.serialized) {
                    return;
                }
                if (blockInaccessible) {
                    ReflectiveTypeAdapterFactory.checkAccessible(value, field);
                }
                if ((fieldValue = field.get(value)) == value) {
                    return;
                }
                writer.name(this.name);
                TypeAdapter t = jsonAdapterPresent ? typeAdapter : new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
                t.write(writer, fieldValue);
            }

            @Override
            void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
                Object fieldValue = typeAdapter.read(reader);
                if (fieldValue != null || !isPrimitive) {
                    if (blockInaccessible) {
                        ReflectiveTypeAdapterFactory.checkAccessible(value, field);
                    }
                    field.set(value, fieldValue);
                }
            }
        };
    }

    private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw, boolean blockInaccessible) {
        LinkedHashMap<String, BoundField> result = new LinkedHashMap<String, BoundField>();
        if (raw.isInterface()) {
            return result;
        }
        Type declaredType = type.getType();
        Class<?> originalRaw = raw;
        while (raw != Object.class) {
            Field[] fields = raw.getDeclaredFields();
            if (raw != originalRaw && fields.length > 0) {
                ReflectionAccessFilter.FilterResult filterResult = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, raw);
                if (filterResult == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
                    throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + raw + " (supertype of " + originalRaw + "). Register a TypeAdapter for this type or adjust the access filter.");
                }
                blockInaccessible = filterResult == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            }
            for (Field field : fields) {
                boolean serialize = this.includeField(field, true);
                boolean deserialize = this.includeField(field, false);
                if (!serialize && !deserialize) continue;
                if (!blockInaccessible) {
                    ReflectionHelper.makeAccessible(field);
                }
                Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
                List<String> fieldNames = this.getFieldNames(field);
                BoundField previous = null;
                int size = fieldNames.size();
                for (int i = 0; i < size; ++i) {
                    String name = fieldNames.get(i);
                    if (i != 0) {
                        serialize = false;
                    }
                    BoundField boundField = this.createBoundField(context, field, name, TypeToken.get(fieldType), serialize, deserialize, blockInaccessible);
                    BoundField replaced = result.put(name, boundField);
                    if (previous != null) continue;
                    previous = replaced;
                }
                if (previous == null) continue;
                throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
            }
            type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
            raw = type.getRawType();
        }
        return result;
    }

    public static final class Adapter<T>
    extends TypeAdapter<T> {
        private final ObjectConstructor<T> constructor;
        private final Map<String, BoundField> boundFields;

        Adapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields) {
            this.constructor = constructor;
            this.boundFields = boundFields;
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            T instance = this.constructor.construct();
            try {
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    BoundField field = this.boundFields.get(name);
                    if (field == null || !field.deserialized) {
                        in.skipValue();
                        continue;
                    }
                    field.read(in, instance);
                }
            }
            catch (IllegalStateException e) {
                throw new JsonSyntaxException(e);
            }
            catch (IllegalAccessException e) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
            }
            in.endObject();
            return instance;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.beginObject();
            try {
                for (BoundField boundField : this.boundFields.values()) {
                    boundField.write(out, value);
                }
            }
            catch (IllegalAccessException e) {
                throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(e);
            }
            out.endObject();
        }
    }

    static abstract class BoundField {
        final String name;
        final boolean serialized;
        final boolean deserialized;

        protected BoundField(String name, boolean serialized, boolean deserialized) {
            this.name = name;
            this.serialized = serialized;
            this.deserialized = deserialized;
        }

        abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

        abstract void read(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
    }
}

