/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
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
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JacksonStdImpl
public final class StringCollectionDeserializer
extends ContainerDeserializerBase<Collection<String>>
implements ResolvableDeserializer {
    protected final JavaType _collectionType;
    protected final JsonDeserializer<String> _valueDeserializer;
    protected final boolean _isDefaultDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;

    public StringCollectionDeserializer(JavaType collectionType, JsonDeserializer<?> valueDeser, ValueInstantiator valueInstantiator) {
        super(collectionType.getRawClass());
        this._collectionType = collectionType;
        this._valueDeserializer = valueDeser;
        this._valueInstantiator = valueInstantiator;
        this._isDefaultDeserializer = this.isDefaultSerializer(valueDeser);
    }

    protected StringCollectionDeserializer(StringCollectionDeserializer src) {
        super(src._valueClass);
        this._collectionType = src._collectionType;
        this._valueDeserializer = src._valueDeserializer;
        this._valueInstantiator = src._valueInstantiator;
        this._isDefaultDeserializer = src._isDefaultDeserializer;
    }

    @Override
    public void resolve(DeserializationConfig config, DeserializerProvider provider) throws JsonMappingException {
        AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
        if (delegateCreator != null) {
            JavaType delegateType = this._valueInstantiator.getDelegateType();
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
        JsonDeserializer<Object> deser = this._valueDeserializer;
        return deser;
    }

    @Override
    public Collection<String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return (Collection)this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
        }
        Collection result = (Collection)this._valueInstantiator.createUsingDefault();
        return this.deserialize(jp, ctxt, result);
    }

    @Override
    public Collection<String> deserialize(JsonParser jp, DeserializationContext ctxt, Collection<String> result) throws IOException, JsonProcessingException {
        JsonToken t;
        if (!jp.isExpectedStartArrayToken()) {
            return this.handleNonArray(jp, ctxt, result);
        }
        if (!this._isDefaultDeserializer) {
            return this.deserializeUsingCustom(jp, ctxt, result);
        }
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            result.add(t == JsonToken.VALUE_NULL ? null : jp.getText());
        }
        return result;
    }

    private Collection<String> deserializeUsingCustom(JsonParser jp, DeserializationContext ctxt, Collection<String> result) throws IOException, JsonProcessingException {
        JsonToken t;
        JsonDeserializer<String> deser = this._valueDeserializer;
        while ((t = jp.nextToken()) != JsonToken.END_ARRAY) {
            String value = t == JsonToken.VALUE_NULL ? null : deser.deserialize(jp, ctxt);
            result.add(value);
        }
        return result;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    private final Collection<String> handleNonArray(JsonParser jp, DeserializationContext ctxt, Collection<String> result) throws IOException, JsonProcessingException {
        if (!ctxt.isEnabled(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            throw ctxt.mappingException(this._collectionType.getRawClass());
        }
        JsonDeserializer<String> valueDes = this._valueDeserializer;
        JsonToken t = jp.getCurrentToken();
        String value = t == JsonToken.VALUE_NULL ? null : (valueDes == null ? jp.getText() : valueDes.deserialize(jp, ctxt));
        result.add(value);
        return result;
    }
}

