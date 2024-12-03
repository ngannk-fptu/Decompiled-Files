/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.ContextualDeserializer;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerFactory;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.StdDeserializers;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.std.AtomicReferenceDeserializer;
import org.codehaus.jackson.map.deser.std.CollectionDeserializer;
import org.codehaus.jackson.map.deser.std.EnumDeserializer;
import org.codehaus.jackson.map.deser.std.EnumMapDeserializer;
import org.codehaus.jackson.map.deser.std.EnumSetDeserializer;
import org.codehaus.jackson.map.deser.std.JsonNodeDeserializer;
import org.codehaus.jackson.map.deser.std.MapDeserializer;
import org.codehaus.jackson.map.deser.std.ObjectArrayDeserializer;
import org.codehaus.jackson.map.deser.std.PrimitiveArrayDeserializers;
import org.codehaus.jackson.map.deser.std.StdKeyDeserializers;
import org.codehaus.jackson.map.deser.std.StringCollectionDeserializer;
import org.codehaus.jackson.map.ext.OptionalHandlerFactory;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.EnumResolver;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BasicDeserializerFactory
extends DeserializerFactory {
    static final HashMap<ClassKey, JsonDeserializer<Object>> _simpleDeserializers = StdDeserializers.constructAll();
    static final HashMap<JavaType, KeyDeserializer> _keyDeserializers = StdKeyDeserializers.constructAll();
    static final HashMap<String, Class<? extends Map>> _mapFallbacks = new HashMap();
    static final HashMap<String, Class<? extends Collection>> _collectionFallbacks;
    protected static final HashMap<JavaType, JsonDeserializer<Object>> _arrayDeserializers;
    protected OptionalHandlerFactory optionalHandlers = OptionalHandlerFactory.instance;

    protected BasicDeserializerFactory() {
    }

    @Override
    public abstract DeserializerFactory withConfig(DeserializerFactory.Config var1);

    protected abstract JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType var1, DeserializationConfig var2, DeserializerProvider var3, BeanProperty var4, TypeDeserializer var5, JsonDeserializer<?> var6) throws JsonMappingException;

    protected abstract JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType var1, DeserializationConfig var2, DeserializerProvider var3, BasicBeanDescription var4, BeanProperty var5, TypeDeserializer var6, JsonDeserializer<?> var7) throws JsonMappingException;

    protected abstract JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType var1, DeserializationConfig var2, DeserializerProvider var3, BasicBeanDescription var4, BeanProperty var5, TypeDeserializer var6, JsonDeserializer<?> var7) throws JsonMappingException;

    protected abstract JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> var1, DeserializationConfig var2, BasicBeanDescription var3, BeanProperty var4) throws JsonMappingException;

    protected abstract JsonDeserializer<?> _findCustomMapDeserializer(MapType var1, DeserializationConfig var2, DeserializerProvider var3, BasicBeanDescription var4, BeanProperty var5, KeyDeserializer var6, TypeDeserializer var7, JsonDeserializer<?> var8) throws JsonMappingException;

    protected abstract JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType var1, DeserializationConfig var2, DeserializerProvider var3, BasicBeanDescription var4, BeanProperty var5, KeyDeserializer var6, TypeDeserializer var7, JsonDeserializer<?> var8) throws JsonMappingException;

    protected abstract JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> var1, DeserializationConfig var2, BeanProperty var3) throws JsonMappingException;

    @Override
    public abstract ValueInstantiator findValueInstantiator(DeserializationConfig var1, BasicBeanDescription var2) throws JsonMappingException;

    @Override
    public abstract JavaType mapAbstractType(DeserializationConfig var1, JavaType var2) throws JsonMappingException;

    @Override
    public JsonDeserializer<?> createArrayDeserializer(DeserializationConfig config, DeserializerProvider p, ArrayType type, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> custom;
        TypeDeserializer elemTypeDeser;
        JavaType elemType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)elemType.getValueHandler();
        if (contentDeser == null) {
            JsonDeserializer<Object> deser = _arrayDeserializers.get(elemType);
            if (deser != null) {
                JsonDeserializer<?> custom2 = this._findCustomArrayDeserializer(type, config, p, property, null, null);
                if (custom2 != null) {
                    return custom2;
                }
                return deser;
            }
            if (elemType.isPrimitive()) {
                throw new IllegalArgumentException("Internal error: primitive type (" + type + ") passed, no array deserializer found");
            }
        }
        if ((elemTypeDeser = (TypeDeserializer)elemType.getTypeHandler()) == null) {
            elemTypeDeser = this.findTypeDeserializer(config, elemType, property);
        }
        if ((custom = this._findCustomArrayDeserializer(type, config, p, property, elemTypeDeser, contentDeser)) != null) {
            return custom;
        }
        if (contentDeser == null) {
            contentDeser = p.findValueDeserializer(config, elemType, property);
        }
        return new ObjectArrayDeserializer(type, contentDeser, elemTypeDeser);
    }

    @Override
    public JsonDeserializer<?> createCollectionDeserializer(DeserializationConfig config, DeserializerProvider p, CollectionType type, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<?> custom;
        type = (CollectionType)this.mapAbstractType(config, type);
        Class<Object> collectionClass = type.getRawClass();
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectForCreation(type);
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(config, beanDesc.getClassInfo(), property);
        if (deser != null) {
            return deser;
        }
        type = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), type, null);
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType, property);
        }
        if ((custom = this._findCustomCollectionDeserializer(type, config, p, beanDesc, property, contentTypeDeser, contentDeser)) != null) {
            return custom;
        }
        if (contentDeser == null) {
            if (EnumSet.class.isAssignableFrom(collectionClass)) {
                return new EnumSetDeserializer(contentType.getRawClass(), this.createEnumDeserializer(config, p, contentType, property));
            }
            contentDeser = p.findValueDeserializer(config, contentType, property);
        }
        if (type.isInterface() || type.isAbstract()) {
            Class<? extends Collection> fallback = _collectionFallbacks.get(collectionClass.getName());
            if (fallback == null) {
                throw new IllegalArgumentException("Can not find a deserializer for non-concrete Collection type " + type);
            }
            collectionClass = fallback;
            type = (CollectionType)config.constructSpecializedType(type, collectionClass);
            beanDesc = (BasicBeanDescription)config.introspectForCreation(type);
        }
        ValueInstantiator inst = this.findValueInstantiator(config, beanDesc);
        if (contentType.getRawClass() == String.class) {
            return new StringCollectionDeserializer(type, contentDeser, inst);
        }
        return new CollectionDeserializer((JavaType)type, contentDeser, contentTypeDeser, inst);
    }

    @Override
    public JsonDeserializer<?> createCollectionLikeDeserializer(DeserializationConfig config, DeserializerProvider p, CollectionLikeType type, BeanProperty property) throws JsonMappingException {
        Class<?> collectionClass = (type = (CollectionLikeType)this.mapAbstractType(config, type)).getRawClass();
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectClassAnnotations(collectionClass);
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(config, beanDesc.getClassInfo(), property);
        if (deser != null) {
            return deser;
        }
        type = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), type, null);
        JavaType contentType = type.getContentType();
        JsonDeserializer contentDeser = (JsonDeserializer)contentType.getValueHandler();
        TypeDeserializer contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler();
        if (contentTypeDeser == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType, property);
        }
        return this._findCustomCollectionLikeDeserializer(type, config, p, beanDesc, property, contentTypeDeser, contentDeser);
    }

    @Override
    public JsonDeserializer<?> createMapDeserializer(DeserializationConfig config, DeserializerProvider p, MapType type, BeanProperty property) throws JsonMappingException {
        Class<Object> mapClass;
        JsonDeserializer<?> custom;
        TypeDeserializer contentTypeDeser;
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectForCreation(type = (MapType)this.mapAbstractType(config, type));
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(config, beanDesc.getClassInfo(), property);
        if (deser != null) {
            return deser;
        }
        type = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), type, null);
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        JsonDeserializer<Object> contentDeser = (JsonDeserializer<Object>)contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
        if (keyDes == null) {
            keyDes = p.findKeyDeserializer(config, keyType, property);
        }
        if ((contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler()) == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType, property);
        }
        if ((custom = this._findCustomMapDeserializer(type, config, p, beanDesc, property, keyDes, contentTypeDeser, contentDeser)) != null) {
            return custom;
        }
        if (contentDeser == null) {
            contentDeser = p.findValueDeserializer(config, contentType, property);
        }
        if (EnumMap.class.isAssignableFrom(mapClass = type.getRawClass())) {
            Class<?> kt = keyType.getRawClass();
            if (kt == null || !kt.isEnum()) {
                throw new IllegalArgumentException("Can not construct EnumMap; generic (key) type not available");
            }
            return new EnumMapDeserializer(keyType.getRawClass(), this.createEnumDeserializer(config, p, keyType, property), contentDeser);
        }
        if (type.isInterface() || type.isAbstract()) {
            Class<? extends Map> fallback = _mapFallbacks.get(mapClass.getName());
            if (fallback == null) {
                throw new IllegalArgumentException("Can not find a deserializer for non-concrete Map type " + type);
            }
            mapClass = fallback;
            type = (MapType)config.constructSpecializedType(type, mapClass);
            beanDesc = (BasicBeanDescription)config.introspectForCreation(type);
        }
        ValueInstantiator inst = this.findValueInstantiator(config, beanDesc);
        MapDeserializer md = new MapDeserializer((JavaType)type, inst, keyDes, contentDeser, contentTypeDeser);
        md.setIgnorableProperties(config.getAnnotationIntrospector().findPropertiesToIgnore(beanDesc.getClassInfo()));
        return md;
    }

    @Override
    public JsonDeserializer<?> createMapLikeDeserializer(DeserializationConfig config, DeserializerProvider p, MapLikeType type, BeanProperty property) throws JsonMappingException {
        TypeDeserializer contentTypeDeser;
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectForCreation(type = (MapLikeType)this.mapAbstractType(config, type));
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(config, beanDesc.getClassInfo(), property);
        if (deser != null) {
            return deser;
        }
        type = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), type, null);
        JavaType keyType = type.getKeyType();
        JavaType contentType = type.getContentType();
        JsonDeserializer contentDeser = (JsonDeserializer)contentType.getValueHandler();
        KeyDeserializer keyDes = (KeyDeserializer)keyType.getValueHandler();
        if (keyDes == null) {
            keyDes = p.findKeyDeserializer(config, keyType, property);
        }
        if ((contentTypeDeser = (TypeDeserializer)contentType.getTypeHandler()) == null) {
            contentTypeDeser = this.findTypeDeserializer(config, contentType, property);
        }
        return this._findCustomMapLikeDeserializer(type, config, p, beanDesc, property, keyDes, contentTypeDeser, contentDeser);
    }

    @Override
    public JsonDeserializer<?> createEnumDeserializer(DeserializationConfig config, DeserializerProvider p, JavaType type, BeanProperty property) throws JsonMappingException {
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectForCreation(type);
        JsonDeserializer<Object> des = this.findDeserializerFromAnnotation(config, beanDesc.getClassInfo(), property);
        if (des != null) {
            return des;
        }
        Class<?> enumClass = type.getRawClass();
        JsonDeserializer<?> custom = this._findCustomEnumDeserializer(enumClass, config, beanDesc, property);
        if (custom != null) {
            return custom;
        }
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            Class<?> returnType;
            if (!config.getAnnotationIntrospector().hasCreatorAnnotation(factory)) continue;
            int argCount = factory.getParameterCount();
            if (argCount == 1 && (returnType = factory.getRawType()).isAssignableFrom(enumClass)) {
                return EnumDeserializer.deserializerForCreator(config, enumClass, factory);
            }
            throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
        }
        return new EnumDeserializer(this.constructEnumResolver(enumClass, config));
    }

    @Override
    public JsonDeserializer<?> createTreeDeserializer(DeserializationConfig config, DeserializerProvider p, JavaType nodeType, BeanProperty property) throws JsonMappingException {
        Class<?> nodeClass = nodeType.getRawClass();
        JsonDeserializer<?> custom = this._findCustomTreeNodeDeserializer(nodeClass, config, property);
        if (custom != null) {
            return custom;
        }
        return JsonNodeDeserializer.getDeserializer(nodeClass);
    }

    protected JsonDeserializer<Object> findStdBeanDeserializer(DeserializationConfig config, DeserializerProvider p, JavaType type, BeanProperty property) throws JsonMappingException {
        Class<?> cls = type.getRawClass();
        JsonDeserializer<Object> deser = _simpleDeserializers.get(new ClassKey(cls));
        if (deser != null) {
            return deser;
        }
        if (AtomicReference.class.isAssignableFrom(cls)) {
            TypeFactory tf = config.getTypeFactory();
            JavaType[] params = tf.findTypeParameters(type, AtomicReference.class);
            JavaType referencedType = params == null || params.length < 1 ? TypeFactory.unknownType() : params[0];
            AtomicReferenceDeserializer d2 = new AtomicReferenceDeserializer(referencedType, property);
            return d2;
        }
        JsonDeserializer<Object> d = this.optionalHandlers.findDeserializer(type, config, p);
        if (d != null) {
            return d;
        }
        return null;
    }

    @Override
    public TypeDeserializer findTypeDeserializer(DeserializationConfig config, JavaType baseType, BeanProperty property) throws JsonMappingException {
        JavaType defaultType;
        Class<?> cls = baseType.getRawClass();
        BasicBeanDescription bean = (BasicBeanDescription)config.introspectClassAnnotations(cls);
        AnnotatedClass ac = bean.getClassInfo();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
            if (b == null) {
                return null;
            }
        } else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(ac, config, ai);
        }
        if (b.getDefaultImpl() == null && baseType.isAbstract() && (defaultType = this.mapAbstractType(config, baseType)) != null && defaultType.getRawClass() != baseType.getRawClass()) {
            b = b.defaultImpl(defaultType.getRawClass());
        }
        return b.buildTypeDeserializer(config, baseType, subtypes, property);
    }

    public TypeDeserializer findPropertyTypeDeserializer(DeserializationConfig config, JavaType baseType, AnnotatedMember annotated, BeanProperty property) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, annotated, baseType);
        if (b == null) {
            return this.findTypeDeserializer(config, baseType, property);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(annotated, config, ai);
        return b.buildTypeDeserializer(config, baseType, subtypes, property);
    }

    public TypeDeserializer findPropertyContentTypeDeserializer(DeserializationConfig config, JavaType containerType, AnnotatedMember propertyEntity, BeanProperty property) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, propertyEntity, containerType);
        JavaType contentType = containerType.getContentType();
        if (b == null) {
            return this.findTypeDeserializer(config, contentType, property);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(propertyEntity, config, ai);
        return b.buildTypeDeserializer(config, contentType, subtypes, property);
    }

    protected JsonDeserializer<Object> findDeserializerFromAnnotation(DeserializationConfig config, Annotated ann, BeanProperty property) throws JsonMappingException {
        Object deserDef = config.getAnnotationIntrospector().findDeserializer(ann);
        if (deserDef != null) {
            return this._constructDeserializer(config, ann, property, deserDef);
        }
        return null;
    }

    JsonDeserializer<Object> _constructDeserializer(DeserializationConfig config, Annotated ann, BeanProperty property, Object deserDef) throws JsonMappingException {
        if (deserDef instanceof JsonDeserializer) {
            JsonDeserializer deser = (JsonDeserializer)deserDef;
            if (deser instanceof ContextualDeserializer) {
                deser = ((ContextualDeserializer)((Object)deser)).createContextual(config, property);
            }
            return deser;
        }
        if (!(deserDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned deserializer definition of type " + deserDef.getClass().getName() + "; expected type JsonDeserializer or Class<JsonDeserializer> instead");
        }
        Class deserClass = (Class)deserDef;
        if (!JsonDeserializer.class.isAssignableFrom(deserClass)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<JsonDeserializer>");
        }
        JsonDeserializer<Object> deser = config.deserializerInstance(ann, deserClass);
        if (deser instanceof ContextualDeserializer) {
            deser = ((ContextualDeserializer)((Object)deser)).createContextual(config, property);
        }
        return deser;
    }

    protected <T extends JavaType> T modifyTypeByAnnotation(DeserializationConfig config, Annotated a, T type, String propName) throws JsonMappingException {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        Class<?> subclass = intr.findDeserializationType(a, (JavaType)type, propName);
        if (subclass != null) {
            try {
                type = ((JavaType)type).narrowBy(subclass);
            }
            catch (IllegalArgumentException iae) {
                throw new JsonMappingException("Failed to narrow type " + type + " with concrete-type annotation (value " + subclass.getName() + "), method '" + a.getName() + "': " + iae.getMessage(), null, iae);
            }
        }
        if (((JavaType)type).isContainerType()) {
            Class<? extends JsonDeserializer<?>> cdClass;
            JavaType contentType;
            Class<?> cc;
            Class<? extends KeyDeserializer> kdClass;
            JavaType keyType;
            Class<?> keyClass = intr.findDeserializationKeyType(a, ((JavaType)type).getKeyType(), propName);
            if (keyClass != null) {
                if (!(type instanceof MapLikeType)) {
                    throw new JsonMappingException("Illegal key-type annotation: type " + type + " is not a Map(-like) type");
                }
                try {
                    type = ((MapLikeType)type).narrowKey(keyClass);
                }
                catch (IllegalArgumentException iae) {
                    throw new JsonMappingException("Failed to narrow key type " + type + " with key-type annotation (" + keyClass.getName() + "): " + iae.getMessage(), null, iae);
                }
            }
            if ((keyType = ((JavaType)type).getKeyType()) != null && keyType.getValueHandler() == null && (kdClass = intr.findKeyDeserializer(a)) != null && kdClass != KeyDeserializer.None.class) {
                KeyDeserializer kd = config.keyDeserializerInstance(a, kdClass);
                keyType.setValueHandler(kd);
            }
            if ((cc = intr.findDeserializationContentType(a, ((JavaType)type).getContentType(), propName)) != null) {
                try {
                    type = ((JavaType)type).narrowContentsBy(cc);
                }
                catch (IllegalArgumentException iae) {
                    throw new JsonMappingException("Failed to narrow content type " + type + " with content-type annotation (" + cc.getName() + "): " + iae.getMessage(), null, iae);
                }
            }
            if ((contentType = ((JavaType)type).getContentType()).getValueHandler() == null && (cdClass = intr.findContentDeserializer(a)) != null && cdClass != JsonDeserializer.None.class) {
                JsonDeserializer<Object> cd = config.deserializerInstance(a, cdClass);
                ((JavaType)type).getContentType().setValueHandler(cd);
            }
        }
        return (T)type;
    }

    protected JavaType resolveType(DeserializationConfig config, BasicBeanDescription beanDesc, JavaType type, AnnotatedMember member, BeanProperty property) throws JsonMappingException {
        TypeDeserializer valueTypeDeser;
        if (type.isContainerType()) {
            TypeDeserializer contentTypeDeser;
            Class<? extends JsonDeserializer<?>> cdClass;
            Class<? extends KeyDeserializer> kdClass;
            AnnotationIntrospector intr = config.getAnnotationIntrospector();
            JavaType keyType = type.getKeyType();
            if (keyType != null && (kdClass = intr.findKeyDeserializer(member)) != null && kdClass != KeyDeserializer.None.class) {
                KeyDeserializer kd = config.keyDeserializerInstance(member, kdClass);
                keyType.setValueHandler(kd);
            }
            if ((cdClass = intr.findContentDeserializer(member)) != null && cdClass != JsonDeserializer.None.class) {
                JsonDeserializer<Object> cd = config.deserializerInstance(member, cdClass);
                type.getContentType().setValueHandler(cd);
            }
            if (member instanceof AnnotatedMember && (contentTypeDeser = this.findPropertyContentTypeDeserializer(config, type, member, property)) != null) {
                type = type.withContentTypeHandler(contentTypeDeser);
            }
        }
        if ((valueTypeDeser = member instanceof AnnotatedMember ? this.findPropertyTypeDeserializer(config, type, member, property) : this.findTypeDeserializer(config, type, null)) != null) {
            type = type.withTypeHandler(valueTypeDeser);
        }
        return type;
    }

    protected EnumResolver<?> constructEnumResolver(Class<?> enumClass, DeserializationConfig config) {
        if (config.isEnabled(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING)) {
            return EnumResolver.constructUnsafeUsingToString(enumClass);
        }
        return EnumResolver.constructUnsafe(enumClass, config.getAnnotationIntrospector());
    }

    static {
        _mapFallbacks.put(Map.class.getName(), LinkedHashMap.class);
        _mapFallbacks.put(ConcurrentMap.class.getName(), ConcurrentHashMap.class);
        _mapFallbacks.put(SortedMap.class.getName(), TreeMap.class);
        _mapFallbacks.put("java.util.NavigableMap", TreeMap.class);
        try {
            Class<?> value;
            Class<?> key = Class.forName("java.util.concurrent.ConcurrentNavigableMap");
            Class<?> mapValue = value = Class.forName("java.util.concurrent.ConcurrentSkipListMap");
            _mapFallbacks.put(key.getName(), mapValue);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        _collectionFallbacks = new HashMap();
        _collectionFallbacks.put(Collection.class.getName(), ArrayList.class);
        _collectionFallbacks.put(List.class.getName(), ArrayList.class);
        _collectionFallbacks.put(Set.class.getName(), HashSet.class);
        _collectionFallbacks.put(SortedSet.class.getName(), TreeSet.class);
        _collectionFallbacks.put(Queue.class.getName(), LinkedList.class);
        _collectionFallbacks.put("java.util.Deque", LinkedList.class);
        _collectionFallbacks.put("java.util.NavigableSet", TreeSet.class);
        _arrayDeserializers = PrimitiveArrayDeserializers.getAll();
    }
}

