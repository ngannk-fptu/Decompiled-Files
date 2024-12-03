/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Collection;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ResolvableDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JacksonStdImpl;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.map.deser.std.StdValueInstantiator;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public class CollectionDeserializer
extends ContainerDeserializerBase<Collection<Object>>
implements ResolvableDeserializer {
    protected final JavaType _collectionType;
    protected final JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;

    @Deprecated
    protected CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, Constructor<Collection<Object>> defCtor) {
        super(collectionType.getRawClass());
        this._collectionType = collectionType;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        StdValueInstantiator inst = new StdValueInstantiator(null, collectionType);
        if (defCtor != null) {
            AnnotatedConstructor aCtor = new AnnotatedConstructor(defCtor, null, null);
            inst.configureFromObjectSettings(aCtor, null, null, null, null);
        }
        this._valueInstantiator = inst;
    }

    public CollectionDeserializer(JavaType collectionType, JsonDeserializer<Object> valueDeser, TypeDeserializer valueTypeDeser, ValueInstantiator valueInstantiator) {
        super(collectionType.getRawClass());
        this._collectionType = collectionType;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = valueTypeDeser;
        this._valueInstantiator = valueInstantiator;
    }

    protected CollectionDeserializer(CollectionDeserializer src) {
        super(src._valueClass);
        this._collectionType = src._collectionType;
        this._valueDeserializer = src._valueDeserializer;
        this._valueTypeDeserializer = src._valueTypeDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
    }

    @Override
    public void resolve(DeserializationConfig config, DeserializerProvider provider) throws JsonMappingException {
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            JavaType delegateType = this._valueInstantiator.getDelegateType();
            if (delegateType == null) {
                throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._collectionType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
            }
            AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            BeanProperty.Std property = new BeanProperty.Std(null, delegateType, null, delegateCreator);
            this._delegateDeserializer = this.findDeserializer(config, provider, delegateType, property);
        }
    }

    @Override
    public JavaType getContentType() {
        return this._collectionType.getContentType();
    }

    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }

    @Override
    public Collection<Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String str;
        if (this._delegateDeserializer != null) {
            return (Collection)this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING && (str = jp.getText()).length() == 0) {
            return (Collection)this._valueInstantiator.createFromString(str);
        }
        return this.deserialize(jp, ctxt, (Collection)this._valueInstantiator.createUsingDefault());
    }

    @Override
    public Collection<Object> deserialize(JsonParser jp, DeserializationContext ctxt, Collection<Object> result) throws IOException, JsonProcessingException {
        JsonToken t;
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt, result);
        }
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            Object value = t == JsonToken.VALUE_NULL ? null : (typeDeser == null ? valueDes.deserialize(jp, ctxt) : valueDes.deserializeWithType(jp, ctxt, typeDeser));
            result.add(value);
        }
        return result;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    private final Collection<Object> handleNonArray(JsonParser jp, DeserializationContext ctxt, Collection<Object> result) throws IOException, JsonProcessingException {
        if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            throw ctxt.mappingException(this._collectionType.getRawClass());
        }
        JsonDeserializer<Object> valueDes = this._valueDeserializer;
        TypeDeserializer typeDeser = this._valueTypeDeserializer;
        JsonToken t = jp.getCurrentToken();
        Object value = t == JsonToken.VALUE_NULL ? null : (typeDeser == null ? valueDes.deserialize(jp, ctxt) : valueDes.deserializeWithType(jp, ctxt, typeDeser));
        result.add(value);
        return result;
    }
}

