/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.io.SerializedString;
import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.ContextualDeserializer;
import org.codehaus.jackson.map.ContextualKeyDeserializer;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerFactory;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.KeyDeserializers;
import org.codehaus.jackson.map.ResolvableDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerFactory;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.RootNameLookup;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdDeserializerProvider
extends DeserializerProvider {
    protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _cachedDeserializers = new ConcurrentHashMap(64, 0.75f, 2);
    protected final HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = new HashMap(8);
    protected final RootNameLookup _rootNames;
    protected DeserializerFactory _factory;

    public StdDeserializerProvider() {
        this(BeanDeserializerFactory.instance);
    }

    public StdDeserializerProvider(DeserializerFactory f) {
        this._factory = f;
        this._rootNames = new RootNameLookup();
    }

    @Override
    public DeserializerProvider withAdditionalDeserializers(Deserializers d) {
        return this.withFactory(this._factory.withAdditionalDeserializers(d));
    }

    @Override
    public DeserializerProvider withAdditionalKeyDeserializers(KeyDeserializers d) {
        return this.withFactory(this._factory.withAdditionalKeyDeserializers(d));
    }

    @Override
    public DeserializerProvider withDeserializerModifier(BeanDeserializerModifier modifier) {
        return this.withFactory(this._factory.withDeserializerModifier(modifier));
    }

    @Override
    public DeserializerProvider withAbstractTypeResolver(AbstractTypeResolver resolver) {
        return this.withFactory(this._factory.withAbstractTypeResolver(resolver));
    }

    @Override
    public DeserializerProvider withValueInstantiators(ValueInstantiators instantiators) {
        return this.withFactory(this._factory.withValueInstantiators(instantiators));
    }

    @Override
    public StdDeserializerProvider withFactory(DeserializerFactory factory) {
        if (this.getClass() != StdDeserializerProvider.class) {
            throw new IllegalStateException("DeserializerProvider of type " + this.getClass().getName() + " does not override 'withFactory()' method");
        }
        return new StdDeserializerProvider(factory);
    }

    @Override
    public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
        return this._factory.mapAbstractType(config, type);
    }

    @Override
    public SerializedString findExpectedRootName(DeserializationConfig config, JavaType type) throws JsonMappingException {
        return this._rootNames.findRootName(type, config);
    }

    @Override
    public JsonDeserializer<Object> findValueDeserializer(DeserializationConfig config, JavaType propertyType, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> deser = this._findCachedDeserializer(propertyType);
        if (deser != null) {
            if (deser instanceof ContextualDeserializer) {
                JsonDeserializer d = ((ContextualDeserializer)((Object)deser)).createContextual(config, property);
                deser = d;
            }
            return deser;
        }
        deser = this._createAndCacheValueDeserializer(config, propertyType, property);
        if (deser == null) {
            deser = this._handleUnknownValueDeserializer(propertyType);
        }
        if (deser instanceof ContextualDeserializer) {
            JsonDeserializer d = ((ContextualDeserializer)((Object)deser)).createContextual(config, property);
            deser = d;
        }
        return deser;
    }

    @Override
    public JsonDeserializer<Object> findTypedValueDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> deser = this.findValueDeserializer(config, type, property);
        TypeDeserializer typeDeser = this._factory.findTypeDeserializer(config, type, property);
        if (typeDeser != null) {
            return new WrappedDeserializer(typeDeser, deser);
        }
        return deser;
    }

    @Override
    public KeyDeserializer findKeyDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        KeyDeserializer kd = this._factory.createKeyDeserializer(config, type, property);
        if (kd instanceof ContextualKeyDeserializer) {
            kd = ((ContextualKeyDeserializer)((Object)kd)).createContextual(config, property);
        }
        if (kd == null) {
            return this._handleUnknownKeyDeserializer(type);
        }
        return kd;
    }

    @Override
    public boolean hasValueDeserializerFor(DeserializationConfig config, JavaType type) {
        JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
        if (deser == null) {
            try {
                deser = this._createAndCacheValueDeserializer(config, type, null);
            }
            catch (Exception e) {
                return false;
            }
        }
        return deser != null;
    }

    @Override
    public int cachedDeserializersCount() {
        return this._cachedDeserializers.size();
    }

    @Override
    public void flushCachedDeserializers() {
        this._cachedDeserializers.clear();
    }

    protected JsonDeserializer<Object> _findCachedDeserializer(JavaType type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        return this._cachedDeserializers.get(type);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected JsonDeserializer<Object> _createAndCacheValueDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        HashMap<JavaType, JsonDeserializer<Object>> hashMap = this._incompleteDeserializers;
        synchronized (hashMap) {
            JsonDeserializer<Object> jsonDeserializer;
            block9: {
                JsonDeserializer<Object> deser = this._findCachedDeserializer(type);
                if (deser != null) {
                    return deser;
                }
                int count = this._incompleteDeserializers.size();
                if (count > 0 && (deser = this._incompleteDeserializers.get(type)) != null) {
                    return deser;
                }
                try {
                    jsonDeserializer = this._createAndCache2(config, type, property);
                    if (count != 0 || this._incompleteDeserializers.size() <= 0) break block9;
                    this._incompleteDeserializers.clear();
                }
                catch (Throwable throwable) {
                    if (count == 0 && this._incompleteDeserializers.size() > 0) {
                        this._incompleteDeserializers.clear();
                    }
                    throw throwable;
                }
            }
            return jsonDeserializer;
        }
    }

    protected JsonDeserializer<Object> _createAndCache2(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        AnnotatedClass ac;
        AnnotationIntrospector aintr;
        Boolean cacheAnn;
        boolean addToCache;
        JsonDeserializer<Object> deser;
        try {
            deser = this._createDeserializer(config, type, property);
        }
        catch (IllegalArgumentException iae) {
            throw new JsonMappingException(iae.getMessage(), null, iae);
        }
        if (deser == null) {
            return null;
        }
        boolean isResolvable = deser instanceof ResolvableDeserializer;
        boolean bl = addToCache = deser.getClass() == BeanDeserializer.class;
        if (!addToCache && config.isEnabled(DeserializationConfig.Feature.USE_ANNOTATIONS) && (cacheAnn = (aintr = config.getAnnotationIntrospector()).findCachability(ac = AnnotatedClass.construct(deser.getClass(), aintr, null))) != null) {
            addToCache = cacheAnn;
        }
        if (isResolvable) {
            this._incompleteDeserializers.put(type, deser);
            this._resolveDeserializer(config, (ResolvableDeserializer)((Object)deser));
            this._incompleteDeserializers.remove(type);
        }
        if (addToCache) {
            this._cachedDeserializers.put(type, deser);
        }
        return deser;
    }

    protected JsonDeserializer<Object> _createDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        if (type.isEnumType()) {
            return this._factory.createEnumDeserializer(config, this, type, property);
        }
        if (type.isContainerType()) {
            if (type.isArrayType()) {
                return this._factory.createArrayDeserializer(config, this, (ArrayType)type, property);
            }
            if (type.isMapLikeType()) {
                MapLikeType mlt = (MapLikeType)type;
                if (mlt.isTrueMapType()) {
                    return this._factory.createMapDeserializer(config, this, (MapType)mlt, property);
                }
                return this._factory.createMapLikeDeserializer(config, this, mlt, property);
            }
            if (type.isCollectionLikeType()) {
                CollectionLikeType clt = (CollectionLikeType)type;
                if (clt.isTrueCollectionType()) {
                    return this._factory.createCollectionDeserializer(config, this, (CollectionType)clt, property);
                }
                return this._factory.createCollectionLikeDeserializer(config, this, clt, property);
            }
        }
        if (JsonNode.class.isAssignableFrom(type.getRawClass())) {
            return this._factory.createTreeDeserializer(config, this, type, property);
        }
        return this._factory.createBeanDeserializer(config, this, type, property);
    }

    protected void _resolveDeserializer(DeserializationConfig config, ResolvableDeserializer ser) throws JsonMappingException {
        ser.resolve(config, this);
    }

    protected JsonDeserializer<Object> _handleUnknownValueDeserializer(JavaType type) throws JsonMappingException {
        Class<?> rawClass = type.getRawClass();
        if (!ClassUtil.isConcrete(rawClass)) {
            throw new JsonMappingException("Can not find a Value deserializer for abstract type " + type);
        }
        throw new JsonMappingException("Can not find a Value deserializer for type " + type);
    }

    protected KeyDeserializer _handleUnknownKeyDeserializer(JavaType type) throws JsonMappingException {
        throw new JsonMappingException("Can not find a (Map) Key deserializer for type " + type);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static final class WrappedDeserializer
    extends JsonDeserializer<Object> {
        final TypeDeserializer _typeDeserializer;
        final JsonDeserializer<Object> _deserializer;

        public WrappedDeserializer(TypeDeserializer typeDeser, JsonDeserializer<Object> deser) {
            this._typeDeserializer = typeDeser;
            this._deserializer = deser;
        }

        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return this._deserializer.deserializeWithType(jp, ctxt, this._typeDeserializer);
        }

        @Override
        public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
            throw new IllegalStateException("Type-wrapped deserializer's deserializeWithType should never get called");
        }

        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctxt, Object intoValue) throws IOException, JsonProcessingException {
            return this._deserializer.deserialize(jp, ctxt, intoValue);
        }
    }
}

