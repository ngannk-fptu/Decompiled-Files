/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.DefaultTypeAdapters;
import com.google.gson.DisjunctionExclusionStrategy;
import com.google.gson.ExclusionStrategy;
import com.google.gson.ExposeAnnotationDeserializationExclusionStrategy;
import com.google.gson.ExposeAnnotationSerializationExclusionStrategy;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.FieldNamingStrategy2;
import com.google.gson.FieldNamingStrategy2Adapter;
import com.google.gson.Gson;
import com.google.gson.InnerClassExclusionStrategy;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializerExceptionWrapper;
import com.google.gson.JsonSerializer;
import com.google.gson.LongSerializationPolicy;
import com.google.gson.MapAsArrayTypeAdapter;
import com.google.gson.MappedObjectConstructor;
import com.google.gson.ModifierBasedExclusionStrategy;
import com.google.gson.ParameterizedTypeHandlerMap;
import com.google.gson.SerializedNameAnnotationInterceptingNamingPolicy;
import com.google.gson.VersionExclusionStrategy;
import com.google.gson.internal.$Gson$Preconditions;
import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GsonBuilder {
    private static final MapAsArrayTypeAdapter COMPLEX_KEY_MAP_TYPE_ADAPTER = new MapAsArrayTypeAdapter();
    private static final InnerClassExclusionStrategy innerClassExclusionStrategy = new InnerClassExclusionStrategy();
    private static final ExposeAnnotationDeserializationExclusionStrategy exposeAnnotationDeserializationExclusionStrategy = new ExposeAnnotationDeserializationExclusionStrategy();
    private static final ExposeAnnotationSerializationExclusionStrategy exposeAnnotationSerializationExclusionStrategy = new ExposeAnnotationSerializationExclusionStrategy();
    private final Set<ExclusionStrategy> serializeExclusionStrategies = new HashSet<ExclusionStrategy>();
    private final Set<ExclusionStrategy> deserializeExclusionStrategies = new HashSet<ExclusionStrategy>();
    private double ignoreVersionsAfter;
    private ModifierBasedExclusionStrategy modifierBasedExclusionStrategy;
    private boolean serializeInnerClasses;
    private boolean excludeFieldsWithoutExposeAnnotation;
    private LongSerializationPolicy longSerializationPolicy;
    private FieldNamingStrategy2 fieldNamingPolicy;
    private final ParameterizedTypeHandlerMap<InstanceCreator<?>> instanceCreators;
    private final ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers;
    private final ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers;
    private boolean serializeNulls;
    private String datePattern;
    private int dateStyle;
    private int timeStyle;
    private boolean serializeSpecialFloatingPointValues;
    private boolean escapeHtmlChars;
    private boolean prettyPrinting;
    private boolean generateNonExecutableJson;

    public GsonBuilder() {
        this.deserializeExclusionStrategies.add(Gson.DEFAULT_ANON_LOCAL_CLASS_EXCLUSION_STRATEGY);
        this.deserializeExclusionStrategies.add(Gson.DEFAULT_SYNTHETIC_FIELD_EXCLUSION_STRATEGY);
        this.serializeExclusionStrategies.add(Gson.DEFAULT_ANON_LOCAL_CLASS_EXCLUSION_STRATEGY);
        this.serializeExclusionStrategies.add(Gson.DEFAULT_SYNTHETIC_FIELD_EXCLUSION_STRATEGY);
        this.ignoreVersionsAfter = -1.0;
        this.serializeInnerClasses = true;
        this.prettyPrinting = false;
        this.escapeHtmlChars = true;
        this.modifierBasedExclusionStrategy = Gson.DEFAULT_MODIFIER_BASED_EXCLUSION_STRATEGY;
        this.excludeFieldsWithoutExposeAnnotation = false;
        this.longSerializationPolicy = LongSerializationPolicy.DEFAULT;
        this.fieldNamingPolicy = Gson.DEFAULT_NAMING_POLICY;
        this.instanceCreators = new ParameterizedTypeHandlerMap();
        this.serializers = new ParameterizedTypeHandlerMap();
        this.deserializers = new ParameterizedTypeHandlerMap();
        this.serializeNulls = false;
        this.dateStyle = 2;
        this.timeStyle = 2;
        this.serializeSpecialFloatingPointValues = false;
        this.generateNonExecutableJson = false;
    }

    public GsonBuilder setVersion(double ignoreVersionsAfter) {
        this.ignoreVersionsAfter = ignoreVersionsAfter;
        return this;
    }

    public GsonBuilder excludeFieldsWithModifiers(int ... modifiers) {
        this.modifierBasedExclusionStrategy = new ModifierBasedExclusionStrategy(modifiers);
        return this;
    }

    public GsonBuilder generateNonExecutableJson() {
        this.generateNonExecutableJson = true;
        return this;
    }

    public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
        this.excludeFieldsWithoutExposeAnnotation = true;
        return this;
    }

    public GsonBuilder serializeNulls() {
        this.serializeNulls = true;
        return this;
    }

    public GsonBuilder enableComplexMapKeySerialization() {
        this.registerTypeHierarchyAdapter(Map.class, COMPLEX_KEY_MAP_TYPE_ADAPTER);
        return this;
    }

    public GsonBuilder disableInnerClassSerialization() {
        this.serializeInnerClasses = false;
        return this;
    }

    public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy) {
        this.longSerializationPolicy = serializationPolicy;
        return this;
    }

    public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention) {
        return this.setFieldNamingStrategy(namingConvention.getFieldNamingPolicy());
    }

    public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy) {
        return this.setFieldNamingStrategy(new FieldNamingStrategy2Adapter(fieldNamingStrategy));
    }

    GsonBuilder setFieldNamingStrategy(FieldNamingStrategy2 fieldNamingStrategy) {
        this.fieldNamingPolicy = new SerializedNameAnnotationInterceptingNamingPolicy(fieldNamingStrategy);
        return this;
    }

    public GsonBuilder setExclusionStrategies(ExclusionStrategy ... strategies) {
        List<ExclusionStrategy> strategyList = Arrays.asList(strategies);
        this.serializeExclusionStrategies.addAll(strategyList);
        this.deserializeExclusionStrategies.addAll(strategyList);
        return this;
    }

    public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy) {
        this.serializeExclusionStrategies.add(strategy);
        return this;
    }

    public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy) {
        this.deserializeExclusionStrategies.add(strategy);
        return this;
    }

    public GsonBuilder setPrettyPrinting() {
        this.prettyPrinting = true;
        return this;
    }

    public GsonBuilder disableHtmlEscaping() {
        this.escapeHtmlChars = false;
        return this;
    }

    public GsonBuilder setDateFormat(String pattern) {
        this.datePattern = pattern;
        return this;
    }

    public GsonBuilder setDateFormat(int style) {
        this.dateStyle = style;
        this.datePattern = null;
        return this;
    }

    public GsonBuilder setDateFormat(int dateStyle, int timeStyle) {
        this.dateStyle = dateStyle;
        this.timeStyle = timeStyle;
        this.datePattern = null;
        return this;
    }

    public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter) {
        $Gson$Preconditions.checkArgument(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof InstanceCreator);
        if (typeAdapter instanceof InstanceCreator) {
            this.registerInstanceCreator(type, (InstanceCreator)typeAdapter);
        }
        if (typeAdapter instanceof JsonSerializer) {
            this.registerSerializer(type, (JsonSerializer)typeAdapter);
        }
        if (typeAdapter instanceof JsonDeserializer) {
            this.registerDeserializer(type, (JsonDeserializer)typeAdapter);
        }
        return this;
    }

    private <T> GsonBuilder registerInstanceCreator(Type typeOfT, InstanceCreator<? extends T> instanceCreator) {
        this.instanceCreators.register(typeOfT, instanceCreator);
        return this;
    }

    private <T> GsonBuilder registerSerializer(Type typeOfT, JsonSerializer<T> serializer) {
        this.serializers.register(typeOfT, serializer);
        return this;
    }

    private <T> GsonBuilder registerDeserializer(Type typeOfT, JsonDeserializer<T> deserializer) {
        this.deserializers.register(typeOfT, new JsonDeserializerExceptionWrapper<T>(deserializer));
        return this;
    }

    public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter) {
        $Gson$Preconditions.checkArgument(typeAdapter instanceof JsonSerializer || typeAdapter instanceof JsonDeserializer || typeAdapter instanceof InstanceCreator);
        if (typeAdapter instanceof InstanceCreator) {
            this.registerInstanceCreatorForTypeHierarchy(baseType, (InstanceCreator)typeAdapter);
        }
        if (typeAdapter instanceof JsonSerializer) {
            this.registerSerializerForTypeHierarchy(baseType, (JsonSerializer)typeAdapter);
        }
        if (typeAdapter instanceof JsonDeserializer) {
            this.registerDeserializerForTypeHierarchy(baseType, (JsonDeserializer)typeAdapter);
        }
        return this;
    }

    private <T> GsonBuilder registerInstanceCreatorForTypeHierarchy(Class<?> classOfT, InstanceCreator<? extends T> instanceCreator) {
        this.instanceCreators.registerForTypeHierarchy(classOfT, instanceCreator);
        return this;
    }

    private <T> GsonBuilder registerSerializerForTypeHierarchy(Class<?> classOfT, JsonSerializer<T> serializer) {
        this.serializers.registerForTypeHierarchy(classOfT, serializer);
        return this;
    }

    private <T> GsonBuilder registerDeserializerForTypeHierarchy(Class<?> classOfT, JsonDeserializer<T> deserializer) {
        this.deserializers.registerForTypeHierarchy(classOfT, new JsonDeserializerExceptionWrapper<T>(deserializer));
        return this;
    }

    public GsonBuilder serializeSpecialFloatingPointValues() {
        this.serializeSpecialFloatingPointValues = true;
        return this;
    }

    public Gson create() {
        LinkedList<ExclusionStrategy> deserializationStrategies = new LinkedList<ExclusionStrategy>(this.deserializeExclusionStrategies);
        LinkedList<ExclusionStrategy> serializationStrategies = new LinkedList<ExclusionStrategy>(this.serializeExclusionStrategies);
        deserializationStrategies.add(this.modifierBasedExclusionStrategy);
        serializationStrategies.add(this.modifierBasedExclusionStrategy);
        if (!this.serializeInnerClasses) {
            deserializationStrategies.add(innerClassExclusionStrategy);
            serializationStrategies.add(innerClassExclusionStrategy);
        }
        if (this.ignoreVersionsAfter != -1.0) {
            VersionExclusionStrategy versionExclusionStrategy = new VersionExclusionStrategy(this.ignoreVersionsAfter);
            deserializationStrategies.add(versionExclusionStrategy);
            serializationStrategies.add(versionExclusionStrategy);
        }
        if (this.excludeFieldsWithoutExposeAnnotation) {
            deserializationStrategies.add(exposeAnnotationDeserializationExclusionStrategy);
            serializationStrategies.add(exposeAnnotationSerializationExclusionStrategy);
        }
        ParameterizedTypeHandlerMap<JsonSerializer<?>> customSerializers = DefaultTypeAdapters.DEFAULT_HIERARCHY_SERIALIZERS.copyOf();
        customSerializers.register(this.serializers.copyOf());
        ParameterizedTypeHandlerMap<JsonDeserializer<?>> customDeserializers = DefaultTypeAdapters.DEFAULT_HIERARCHY_DESERIALIZERS.copyOf();
        customDeserializers.register(this.deserializers.copyOf());
        GsonBuilder.addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, customSerializers, customDeserializers);
        customSerializers.registerIfAbsent(DefaultTypeAdapters.getDefaultSerializers(this.serializeSpecialFloatingPointValues, this.longSerializationPolicy));
        customDeserializers.registerIfAbsent(DefaultTypeAdapters.getDefaultDeserializers());
        ParameterizedTypeHandlerMap<InstanceCreator<?>> customInstanceCreators = this.instanceCreators.copyOf();
        customInstanceCreators.registerIfAbsent(DefaultTypeAdapters.getDefaultInstanceCreators());
        customSerializers.makeUnmodifiable();
        customDeserializers.makeUnmodifiable();
        this.instanceCreators.makeUnmodifiable();
        MappedObjectConstructor objConstructor = new MappedObjectConstructor(customInstanceCreators);
        Gson gson = new Gson(new DisjunctionExclusionStrategy(deserializationStrategies), new DisjunctionExclusionStrategy(serializationStrategies), this.fieldNamingPolicy, objConstructor, this.serializeNulls, customSerializers, customDeserializers, this.generateNonExecutableJson, this.escapeHtmlChars, this.prettyPrinting);
        return gson;
    }

    private static void addTypeAdaptersForDate(String datePattern, int dateStyle, int timeStyle, ParameterizedTypeHandlerMap<JsonSerializer<?>> serializers, ParameterizedTypeHandlerMap<JsonDeserializer<?>> deserializers) {
        DefaultTypeAdapters.DefaultDateTypeAdapter dateTypeAdapter = null;
        if (datePattern != null && !"".equals(datePattern.trim())) {
            dateTypeAdapter = new DefaultTypeAdapters.DefaultDateTypeAdapter(datePattern);
        } else if (dateStyle != 2 && timeStyle != 2) {
            dateTypeAdapter = new DefaultTypeAdapters.DefaultDateTypeAdapter(dateStyle, timeStyle);
        }
        if (dateTypeAdapter != null) {
            GsonBuilder.registerIfAbsent(java.util.Date.class, serializers, dateTypeAdapter);
            GsonBuilder.registerIfAbsent(java.util.Date.class, deserializers, dateTypeAdapter);
            GsonBuilder.registerIfAbsent(Timestamp.class, serializers, dateTypeAdapter);
            GsonBuilder.registerIfAbsent(Timestamp.class, deserializers, dateTypeAdapter);
            GsonBuilder.registerIfAbsent(Date.class, serializers, dateTypeAdapter);
            GsonBuilder.registerIfAbsent(Date.class, deserializers, dateTypeAdapter);
        }
    }

    private static <T> void registerIfAbsent(Class<?> type, ParameterizedTypeHandlerMap<T> adapters, T adapter) {
        if (!adapters.hasSpecificHandlerFor(type)) {
            adapters.register(type, adapter);
        }
    }
}

