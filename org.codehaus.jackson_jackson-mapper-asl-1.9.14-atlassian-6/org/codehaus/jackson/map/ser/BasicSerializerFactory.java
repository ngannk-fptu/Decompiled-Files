/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 *  org.codehaus.jackson.util.TokenBuffer
 */
package org.codehaus.jackson.map.ser;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.TimeZone;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.ContextualSerializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializable;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerFactory;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ext.OptionalHandlerFactory;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.ser.StdSerializers;
import org.codehaus.jackson.map.ser.std.CalendarSerializer;
import org.codehaus.jackson.map.ser.std.DateSerializer;
import org.codehaus.jackson.map.ser.std.EnumMapSerializer;
import org.codehaus.jackson.map.ser.std.EnumSerializer;
import org.codehaus.jackson.map.ser.std.IndexedStringListSerializer;
import org.codehaus.jackson.map.ser.std.InetAddressSerializer;
import org.codehaus.jackson.map.ser.std.JsonValueSerializer;
import org.codehaus.jackson.map.ser.std.MapSerializer;
import org.codehaus.jackson.map.ser.std.NullSerializer;
import org.codehaus.jackson.map.ser.std.ObjectArraySerializer;
import org.codehaus.jackson.map.ser.std.SerializableSerializer;
import org.codehaus.jackson.map.ser.std.SerializableWithTypeSerializer;
import org.codehaus.jackson.map.ser.std.StdArraySerializers;
import org.codehaus.jackson.map.ser.std.StdContainerSerializers;
import org.codehaus.jackson.map.ser.std.StdJdkSerializers;
import org.codehaus.jackson.map.ser.std.StringCollectionSerializer;
import org.codehaus.jackson.map.ser.std.StringSerializer;
import org.codehaus.jackson.map.ser.std.TimeZoneSerializer;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.codehaus.jackson.map.ser.std.TokenBufferSerializer;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.EnumValues;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.TokenBuffer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BasicSerializerFactory
extends SerializerFactory {
    protected static final HashMap<String, JsonSerializer<?>> _concrete = new HashMap();
    protected static final HashMap<String, Class<? extends JsonSerializer<?>>> _concreteLazy = new HashMap();
    protected static final HashMap<String, JsonSerializer<?>> _arraySerializers;
    protected OptionalHandlerFactory optionalHandlers = OptionalHandlerFactory.instance;

    protected BasicSerializerFactory() {
    }

    @Override
    public abstract JsonSerializer<Object> createSerializer(SerializationConfig var1, JavaType var2, BeanProperty var3) throws JsonMappingException;

    @Override
    public TypeSerializer createTypeSerializer(SerializationConfig config, JavaType baseType, BeanProperty property) {
        BasicBeanDescription bean = (BasicBeanDescription)config.introspectClassAnnotations(baseType.getRawClass());
        AnnotatedClass ac = bean.getClassInfo();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findTypeResolver(config, ac, baseType);
        Collection<NamedType> subtypes = null;
        if (b == null) {
            b = config.getDefaultTyper(baseType);
        } else {
            subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(ac, config, ai);
        }
        return b == null ? null : b.buildTypeSerializer(config, baseType, subtypes, property);
    }

    public final JsonSerializer<?> getNullSerializer() {
        return NullSerializer.instance;
    }

    protected abstract Iterable<Serializers> customSerializers();

    public final JsonSerializer<?> findSerializerByLookup(JavaType type, SerializationConfig config, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping) {
        Class raw = type.getRawClass();
        String clsName = raw.getName();
        JsonSerializer<?> ser = _concrete.get(clsName);
        if (ser != null) {
            return ser;
        }
        Class<JsonSerializer<?>> serClass = _concreteLazy.get(clsName);
        if (serClass != null) {
            try {
                return serClass.newInstance();
            }
            catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate standard serializer (of type " + serClass.getName() + "): " + e.getMessage(), e);
            }
        }
        return null;
    }

    public final JsonSerializer<?> findSerializerByPrimaryType(JavaType type, SerializationConfig config, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping) throws JsonMappingException {
        Class raw = type.getRawClass();
        if (JsonSerializable.class.isAssignableFrom(raw)) {
            if (JsonSerializableWithType.class.isAssignableFrom(raw)) {
                return SerializableWithTypeSerializer.instance;
            }
            return SerializableSerializer.instance;
        }
        AnnotatedMethod valueMethod = beanDesc.findJsonValueMethod();
        if (valueMethod != null) {
            Method m = valueMethod.getAnnotated();
            if (config.isEnabled(SerializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                ClassUtil.checkAndFixAccess(m);
            }
            JsonSerializer<Object> ser = this.findSerializerFromAnnotation(config, valueMethod, property);
            return new JsonValueSerializer(m, ser, property);
        }
        if (InetAddress.class.isAssignableFrom(raw)) {
            return InetAddressSerializer.instance;
        }
        if (TimeZone.class.isAssignableFrom(raw)) {
            return TimeZoneSerializer.instance;
        }
        if (Charset.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        JsonSerializer<?> ser = this.optionalHandlers.findSerializer(config, type);
        if (ser != null) {
            return ser;
        }
        if (Number.class.isAssignableFrom(raw)) {
            return StdSerializers.NumberSerializer.instance;
        }
        if (Enum.class.isAssignableFrom(raw)) {
            Class enumClass = raw;
            return EnumSerializer.construct(enumClass, config, beanDesc);
        }
        if (Calendar.class.isAssignableFrom(raw)) {
            return CalendarSerializer.instance;
        }
        if (Date.class.isAssignableFrom(raw)) {
            return DateSerializer.instance;
        }
        return null;
    }

    public final JsonSerializer<?> findSerializerByAddonType(SerializationConfig config, JavaType javaType, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping) throws JsonMappingException {
        Class type = javaType.getRawClass();
        if (Iterator.class.isAssignableFrom(type)) {
            return this.buildIteratorSerializer(config, javaType, beanDesc, property, staticTyping);
        }
        if (Iterable.class.isAssignableFrom(type)) {
            return this.buildIterableSerializer(config, javaType, beanDesc, property, staticTyping);
        }
        if (CharSequence.class.isAssignableFrom(type)) {
            return ToStringSerializer.instance;
        }
        return null;
    }

    protected JsonSerializer<Object> findSerializerFromAnnotation(SerializationConfig config, Annotated a, BeanProperty property) throws JsonMappingException {
        Object serDef = config.getAnnotationIntrospector().findSerializer(a);
        if (serDef == null) {
            return null;
        }
        if (serDef instanceof JsonSerializer) {
            JsonSerializer ser = (JsonSerializer)serDef;
            if (ser instanceof ContextualSerializer) {
                return ((ContextualSerializer)((Object)ser)).createContextual(config, property);
            }
            return ser;
        }
        if (!(serDef instanceof Class)) {
            throw new IllegalStateException("AnnotationIntrospector returned value of type " + serDef.getClass().getName() + "; expected type JsonSerializer or Class<JsonSerializer> instead");
        }
        Class cls = (Class)serDef;
        if (!JsonSerializer.class.isAssignableFrom(cls)) {
            throw new IllegalStateException("AnnotationIntrospector returned Class " + cls.getName() + "; expected Class<JsonSerializer>");
        }
        JsonSerializer<Object> ser = config.serializerInstance(a, cls);
        if (ser instanceof ContextualSerializer) {
            return ((ContextualSerializer)((Object)ser)).createContextual(config, property);
        }
        return ser;
    }

    public JsonSerializer<?> buildContainerSerializer(SerializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping) {
        JavaType elementType = type.getContentType();
        TypeSerializer elementTypeSerializer = this.createTypeSerializer(config, elementType, property);
        if (elementTypeSerializer != null) {
            staticTyping = false;
        } else if (!staticTyping) {
            staticTyping = this.usesStaticTyping(config, beanDesc, elementTypeSerializer, property);
        }
        JsonSerializer<Object> elementValueSerializer = BasicSerializerFactory.findContentSerializer(config, beanDesc.getClassInfo(), property);
        if (type.isMapLikeType()) {
            MapLikeType mlt = (MapLikeType)type;
            JsonSerializer<Object> keySerializer = BasicSerializerFactory.findKeySerializer(config, beanDesc.getClassInfo(), property);
            if (mlt.isTrueMapType()) {
                return this.buildMapSerializer(config, (MapType)mlt, beanDesc, property, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
            }
            return this.buildMapLikeSerializer(config, mlt, beanDesc, property, staticTyping, keySerializer, elementTypeSerializer, elementValueSerializer);
        }
        if (type.isCollectionLikeType()) {
            CollectionLikeType clt = (CollectionLikeType)type;
            if (clt.isTrueCollectionType()) {
                return this.buildCollectionSerializer(config, (CollectionType)clt, beanDesc, property, staticTyping, elementTypeSerializer, elementValueSerializer);
            }
            return this.buildCollectionLikeSerializer(config, clt, beanDesc, property, staticTyping, elementTypeSerializer, elementValueSerializer);
        }
        if (type.isArrayType()) {
            return this.buildArraySerializer(config, (ArrayType)type, beanDesc, property, staticTyping, elementTypeSerializer, elementValueSerializer);
        }
        return null;
    }

    protected JsonSerializer<?> buildCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        for (Serializers serializers : this.customSerializers()) {
            JsonSerializer<?> ser = serializers.findCollectionLikeSerializer(config, type, beanDesc, property, elementTypeSerializer, elementValueSerializer);
            if (ser == null) continue;
            return ser;
        }
        return null;
    }

    protected JsonSerializer<?> buildCollectionSerializer(SerializationConfig config, CollectionType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        for (Serializers serializers : this.customSerializers()) {
            JsonSerializer<?> ser = serializers.findCollectionSerializer(config, type, beanDesc, property, elementTypeSerializer, elementValueSerializer);
            if (ser == null) continue;
            return ser;
        }
        Class raw = type.getRawClass();
        if (EnumSet.class.isAssignableFrom(raw)) {
            return this.buildEnumSetSerializer(config, type, beanDesc, property, staticTyping, elementTypeSerializer, elementValueSerializer);
        }
        Class elementRaw = type.getContentType().getRawClass();
        if (this.isIndexedList(raw)) {
            if (elementRaw == String.class) {
                return new IndexedStringListSerializer(property, elementValueSerializer);
            }
            return StdContainerSerializers.indexedListSerializer(type.getContentType(), staticTyping, elementTypeSerializer, property, elementValueSerializer);
        }
        if (elementRaw == String.class) {
            return new StringCollectionSerializer(property, elementValueSerializer);
        }
        return StdContainerSerializers.collectionSerializer(type.getContentType(), staticTyping, elementTypeSerializer, property, elementValueSerializer);
    }

    protected JsonSerializer<?> buildEnumSetSerializer(SerializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        JavaType enumType = type.getContentType();
        if (!enumType.isEnumType()) {
            enumType = null;
        }
        return StdContainerSerializers.enumSetSerializer(enumType, property);
    }

    protected boolean isIndexedList(Class<?> cls) {
        return RandomAccess.class.isAssignableFrom(cls);
    }

    protected JsonSerializer<?> buildMapLikeSerializer(SerializationConfig config, MapLikeType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        for (Serializers serializers : this.customSerializers()) {
            JsonSerializer<?> ser = serializers.findMapLikeSerializer(config, type, beanDesc, property, keySerializer, elementTypeSerializer, elementValueSerializer);
            if (ser == null) continue;
            return ser;
        }
        return null;
    }

    protected JsonSerializer<?> buildMapSerializer(SerializationConfig config, MapType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        for (Serializers serializers : this.customSerializers()) {
            JsonSerializer<?> ser = serializers.findMapSerializer(config, type, beanDesc, property, keySerializer, elementTypeSerializer, elementValueSerializer);
            if (ser == null) continue;
            return ser;
        }
        if (EnumMap.class.isAssignableFrom(type.getRawClass())) {
            return this.buildEnumMapSerializer(config, type, beanDesc, property, staticTyping, elementTypeSerializer, elementValueSerializer);
        }
        return MapSerializer.construct(config.getAnnotationIntrospector().findPropertiesToIgnore(beanDesc.getClassInfo()), type, staticTyping, elementTypeSerializer, property, keySerializer, elementValueSerializer);
    }

    protected JsonSerializer<?> buildEnumMapSerializer(SerializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        JavaType keyType = type.getKeyType();
        EnumValues enums = null;
        if (keyType.isEnumType()) {
            Class enumClass = keyType.getRawClass();
            enums = EnumValues.construct(enumClass, config.getAnnotationIntrospector());
        }
        return new EnumMapSerializer(type.getContentType(), staticTyping, enums, elementTypeSerializer, property, elementValueSerializer);
    }

    protected JsonSerializer<?> buildArraySerializer(SerializationConfig config, ArrayType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        Class raw = type.getRawClass();
        if (String[].class == raw) {
            return new StdArraySerializers.StringArraySerializer(property);
        }
        JsonSerializer<?> ser = _arraySerializers.get(raw.getName());
        if (ser != null) {
            return ser;
        }
        return new ObjectArraySerializer(type.getContentType(), staticTyping, elementTypeSerializer, property, elementValueSerializer);
    }

    protected JsonSerializer<?> buildIteratorSerializer(SerializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping) {
        JavaType valueType = type.containedType(0);
        if (valueType == null) {
            valueType = TypeFactory.unknownType();
        }
        TypeSerializer vts = this.createTypeSerializer(config, valueType, property);
        return StdContainerSerializers.iteratorSerializer(valueType, this.usesStaticTyping(config, beanDesc, vts, property), vts, property);
    }

    protected JsonSerializer<?> buildIterableSerializer(SerializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property, boolean staticTyping) {
        JavaType valueType = type.containedType(0);
        if (valueType == null) {
            valueType = TypeFactory.unknownType();
        }
        TypeSerializer vts = this.createTypeSerializer(config, valueType, property);
        return StdContainerSerializers.iterableSerializer(valueType, this.usesStaticTyping(config, beanDesc, vts, property), vts, property);
    }

    protected <T extends JavaType> T modifyTypeByAnnotation(SerializationConfig config, Annotated a, T type) {
        Class<?> superclass = config.getAnnotationIntrospector().findSerializationType(a);
        if (superclass != null) {
            try {
                type = type.widenBy(superclass);
            }
            catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Failed to widen type " + type + " with concrete-type annotation (value " + superclass.getName() + "), method '" + a.getName() + "': " + iae.getMessage());
            }
        }
        return BasicSerializerFactory.modifySecondaryTypesByAnnotation(config, a, type);
    }

    protected static <T extends JavaType> T modifySecondaryTypesByAnnotation(SerializationConfig config, Annotated a, T type) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (type.isContainerType()) {
            Class<?> cc;
            Class<?> keyClass = intr.findSerializationKeyType(a, type.getKeyType());
            if (keyClass != null) {
                if (!(type instanceof MapType)) {
                    throw new IllegalArgumentException("Illegal key-type annotation: type " + type + " is not a Map type");
                }
                try {
                    type = ((MapType)type).widenKey(keyClass);
                }
                catch (IllegalArgumentException iae) {
                    throw new IllegalArgumentException("Failed to narrow key type " + type + " with key-type annotation (" + keyClass.getName() + "): " + iae.getMessage());
                }
            }
            if ((cc = intr.findSerializationContentType(a, type.getContentType())) != null) {
                try {
                    type = type.widenContentsBy(cc);
                }
                catch (IllegalArgumentException iae) {
                    throw new IllegalArgumentException("Failed to narrow content type " + type + " with content-type annotation (" + cc.getName() + "): " + iae.getMessage());
                }
            }
        }
        return type;
    }

    protected static JsonSerializer<Object> findKeySerializer(SerializationConfig config, Annotated a, BeanProperty property) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        Class<? extends JsonSerializer<?>> serClass = intr.findKeySerializer(a);
        if ((serClass == null || serClass == JsonSerializer.None.class) && property != null) {
            serClass = intr.findKeySerializer(property.getMember());
        }
        if (serClass != null && serClass != JsonSerializer.None.class) {
            return config.serializerInstance(a, serClass);
        }
        return null;
    }

    protected static JsonSerializer<Object> findContentSerializer(SerializationConfig config, Annotated a, BeanProperty property) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        Class<? extends JsonSerializer<?>> serClass = intr.findContentSerializer(a);
        if ((serClass == null || serClass == JsonSerializer.None.class) && property != null) {
            serClass = intr.findContentSerializer(property.getMember());
        }
        if (serClass != null && serClass != JsonSerializer.None.class) {
            return config.serializerInstance(a, serClass);
        }
        return null;
    }

    protected boolean usesStaticTyping(SerializationConfig config, BasicBeanDescription beanDesc, TypeSerializer typeSer, BeanProperty property) {
        JavaType type;
        if (typeSer != null) {
            return false;
        }
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        JsonSerialize.Typing t = intr.findSerializationTyping(beanDesc.getClassInfo());
        if (t != null ? t == JsonSerialize.Typing.STATIC : config.isEnabled(SerializationConfig.Feature.USE_STATIC_TYPING)) {
            return true;
        }
        if (property != null && (type = property.getType()).isContainerType()) {
            if (intr.findSerializationContentType(property.getMember(), property.getType()) != null) {
                return true;
            }
            if (type instanceof MapType && intr.findSerializationKeyType(property.getMember(), property.getType()) != null) {
                return true;
            }
        }
        return false;
    }

    static {
        _concrete.put(String.class.getName(), new StringSerializer());
        ToStringSerializer sls = ToStringSerializer.instance;
        _concrete.put(StringBuffer.class.getName(), sls);
        _concrete.put(StringBuilder.class.getName(), sls);
        _concrete.put(Character.class.getName(), sls);
        _concrete.put(Character.TYPE.getName(), sls);
        _concrete.put(Boolean.TYPE.getName(), new StdSerializers.BooleanSerializer(true));
        _concrete.put(Boolean.class.getName(), new StdSerializers.BooleanSerializer(false));
        StdSerializers.IntegerSerializer intS = new StdSerializers.IntegerSerializer();
        _concrete.put(Integer.class.getName(), intS);
        _concrete.put(Integer.TYPE.getName(), intS);
        _concrete.put(Long.class.getName(), StdSerializers.LongSerializer.instance);
        _concrete.put(Long.TYPE.getName(), StdSerializers.LongSerializer.instance);
        _concrete.put(Byte.class.getName(), StdSerializers.IntLikeSerializer.instance);
        _concrete.put(Byte.TYPE.getName(), StdSerializers.IntLikeSerializer.instance);
        _concrete.put(Short.class.getName(), StdSerializers.IntLikeSerializer.instance);
        _concrete.put(Short.TYPE.getName(), StdSerializers.IntLikeSerializer.instance);
        _concrete.put(Float.class.getName(), StdSerializers.FloatSerializer.instance);
        _concrete.put(Float.TYPE.getName(), StdSerializers.FloatSerializer.instance);
        _concrete.put(Double.class.getName(), StdSerializers.DoubleSerializer.instance);
        _concrete.put(Double.TYPE.getName(), StdSerializers.DoubleSerializer.instance);
        StdSerializers.NumberSerializer ns = new StdSerializers.NumberSerializer();
        _concrete.put(BigInteger.class.getName(), ns);
        _concrete.put(BigDecimal.class.getName(), ns);
        _concrete.put(Calendar.class.getName(), CalendarSerializer.instance);
        DateSerializer dateSer = DateSerializer.instance;
        _concrete.put(Date.class.getName(), dateSer);
        _concrete.put(Timestamp.class.getName(), dateSer);
        _concrete.put(java.sql.Date.class.getName(), new StdSerializers.SqlDateSerializer());
        _concrete.put(Time.class.getName(), new StdSerializers.SqlTimeSerializer());
        for (Map.Entry<Class<?>, Object> en : new StdJdkSerializers().provide()) {
            Object value = en.getValue();
            if (value instanceof JsonSerializer) {
                _concrete.put(en.getKey().getName(), (JsonSerializer)value);
                continue;
            }
            if (value instanceof Class) {
                Class cls = (Class)value;
                _concreteLazy.put(en.getKey().getName(), cls);
                continue;
            }
            throw new IllegalStateException("Internal error: unrecognized value of type " + en.getClass().getName());
        }
        _concreteLazy.put(TokenBuffer.class.getName(), TokenBufferSerializer.class);
        _arraySerializers = new HashMap();
        _arraySerializers.put(boolean[].class.getName(), new StdArraySerializers.BooleanArraySerializer());
        _arraySerializers.put(byte[].class.getName(), new StdArraySerializers.ByteArraySerializer());
        _arraySerializers.put(char[].class.getName(), new StdArraySerializers.CharArraySerializer());
        _arraySerializers.put(short[].class.getName(), new StdArraySerializers.ShortArraySerializer());
        _arraySerializers.put(int[].class.getName(), new StdArraySerializers.IntArraySerializer());
        _arraySerializers.put(long[].class.getName(), new StdArraySerializers.LongArraySerializer());
        _arraySerializers.put(float[].class.getName(), new StdArraySerializers.FloatArraySerializer());
        _arraySerializers.put(double[].class.getName(), new StdArraySerializers.DoubleArraySerializer());
    }
}

