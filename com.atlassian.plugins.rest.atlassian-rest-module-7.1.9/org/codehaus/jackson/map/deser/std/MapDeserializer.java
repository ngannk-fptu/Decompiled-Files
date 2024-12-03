/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.ResolvableDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.impl.PropertyBasedCreator;
import org.codehaus.jackson.map.deser.impl.PropertyValueBuffer;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.map.deser.std.StdValueInstantiator;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.map.util.ArrayBuilders;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class MapDeserializer
extends ContainerDeserializerBase<Map<Object, Object>>
implements ResolvableDeserializer {
    protected final JavaType _mapType;
    protected final KeyDeserializer _keyDeserializer;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final boolean _hasDefaultCreator;
    protected PropertyBasedCreator _propertyBasedCreator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected HashSet<String> _ignorableProperties;

    @Deprecated
    protected MapDeserializer(JavaType mapType, Constructor<Map<Object, Object>> defCtor, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(Map.class);
        this._mapType = mapType;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        StdValueInstantiator inst = new StdValueInstantiator(null, mapType);
        if (defCtor != null) {
            AnnotatedConstructor aCtor = new AnnotatedConstructor(defCtor, null, null);
            inst.configureFromObjectSettings(aCtor, null, null, null, null);
        }
        this._hasDefaultCreator = defCtor != null;
        this._valueInstantiator = inst;
    }

    public MapDeserializer(JavaType mapType, ValueInstantiator valueInstantiator, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser) {
        super(Map.class);
        this._mapType = mapType;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
        this._propertyBasedCreator = valueInstantiator.canCreateFromObjectWith() ? new PropertyBasedCreator(valueInstantiator) : null;
        this._hasDefaultCreator = valueInstantiator.canCreateUsingDefault();
    }

    protected MapDeserializer(MapDeserializer src) {
        super(src._valueClass);
        this._mapType = src._mapType;
        this._keyDeserializer = src._keyDeserializer;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._hasDefaultCreator = src._hasDefaultCreator;
        this._ignorableProperties = src._ignorableProperties;
    }

    public void setIgnorableProperties(String[] ignorable) {
        this._ignorableProperties = ignorable == null || ignorable.length == 0 ? null : ArrayBuilders.arrayToSet(ignorable);
    }

    @Override
    public void resolve(DeserializationConfig config, DeserializerProvider provider) throws JsonMappingException {
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            JavaType delegateType = this._valueInstantiator.getDelegateType();
            if (delegateType == null) {
                throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._mapType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
            }
            AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            BeanProperty.Std property = new BeanProperty.Std(null, delegateType, null, delegateCreator);
            this._delegateDeserializer = this.findDeserializer(config, provider, delegateType, property);
        }
        if (this._propertyBasedCreator != null) {
            for (SettableBeanProperty prop : this._propertyBasedCreator.getCreatorProperties()) {
                if (prop.hasValueDeserializer()) continue;
                this._propertyBasedCreator.assignDeserializer(prop, this.findDeserializer(config, provider, prop.getType(), prop));
            }
        }
    }

    @Override
    public JavaType getContentType() {
        return this._mapType.getContentType();
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingCreator(jp, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return (Map)this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (!this._hasDefaultCreator) {
            throw ctxt.instantiationException(this.getMapClass(), "No default constructor found");
        }
        JsonToken t = jp.getCurrentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            if (t == JsonToken.VALUE_STRING) {
                return (Map)this._valueInstantiator.createFromString(jp.getText());
            }
            throw ctxt.mappingException(this.getMapClass());
        }
        Map result = (Map)this._valueInstantiator.createUsingDefault();
        this._readAndBind(jp, ctxt, result);
        return result;
    }

    @Override
    public Map<Object, Object> deserialize(JsonParser jp, DeserializationContext ctxt, Map<Object, Object> result) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            throw ctxt.mappingException(this.getMapClass());
        }
        this._readAndBind(jp, ctxt, result);
        return result;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }

    public final Class<?> getMapClass() {
        return this._mapType.getRawClass();
    }

    @Override
    public JavaType getValueType() {
        return this._mapType;
    }

    protected final void _readAndBind(JsonParser jp, DeserializationContext ctxt, Map<Object, Object> result) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        KeyDeserializer keyDes = this._keyDeserializer;
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        while (t == JsonToken.FIELD_NAME) {
            String fieldName = jp.getCurrentName();
            Object key = keyDes.deserializeKey(fieldName, ctxt);
            t = jp.nextToken();
            if (this._ignorableProperties != null && this._ignorableProperties.contains(fieldName)) {
                jp.skipChildren();
            } else {
                Object value = t == JsonToken.VALUE_NULL ? null : (typeDeser == null ? valueDes.deserialize(jp, ctxt) : valueDes.deserializeWithType(jp, ctxt, typeDeser));
                result.put(key, value);
            }
            t = jp.nextToken();
        }
    }

    public Map<Object, Object> _deserializeUsingCreator(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt);
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        while (t == JsonToken.FIELD_NAME) {
            String propName = jp.getCurrentName();
            t = jp.nextToken();
            if (this._ignorableProperties != null && this._ignorableProperties.contains(propName)) {
                jp.skipChildren();
            } else {
                SettableBeanProperty prop = creator.findCreatorProperty(propName);
                if (prop != null) {
                    Object value = prop.deserialize(jp, ctxt);
                    if (buffer.assignParameter(prop.getPropertyIndex(), value)) {
                        Map result;
                        jp.nextToken();
                        try {
                            result = (Map)creator.build(buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow(e, this._mapType.getRawClass());
                            return null;
                        }
                        this._readAndBind(jp, ctxt, result);
                        return result;
                    }
                } else {
                    String fieldName = jp.getCurrentName();
                    Object key = this._keyDeserializer.deserializeKey(fieldName, ctxt);
                    Object value = t == JsonToken.VALUE_NULL ? null : (typeDeser == null ? valueDes.deserialize(jp, ctxt) : valueDes.deserializeWithType(jp, ctxt, typeDeser));
                    buffer.bufferMapProperty(key, value);
                }
            }
            t = jp.nextToken();
        }
        try {
            return (Map)creator.build(buffer);
        }
        catch (Exception e) {
            this.wrapAndThrow(e, this._mapType.getRawClass());
            return null;
        }
    }

    protected void wrapAndThrow(Throwable t, Object ref) throws IOException {
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        if (t instanceof IOException && !(t instanceof JsonMappingException)) {
            throw (IOException)t;
        }
        throw JsonMappingException.wrapWithPath(t, ref, null);
    }
}

