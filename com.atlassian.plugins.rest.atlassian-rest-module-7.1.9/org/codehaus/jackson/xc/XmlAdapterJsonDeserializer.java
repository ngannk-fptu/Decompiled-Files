/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.codehaus.jackson.xc;

import java.io.IOException;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class XmlAdapterJsonDeserializer
extends StdDeserializer<Object> {
    protected static final JavaType ADAPTER_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(XmlAdapter.class);
    protected final XmlAdapter<Object, Object> _xmlAdapter;
    protected final JavaType _valueType;
    protected JsonDeserializer<?> _deserializer;

    public XmlAdapterJsonDeserializer(XmlAdapter<Object, Object> xmlAdapter) {
        super(Object.class);
        this._xmlAdapter = xmlAdapter;
        TypeFactory typeFactory = TypeFactory.defaultInstance();
        JavaType type = typeFactory.constructType(xmlAdapter.getClass());
        JavaType[] rawTypes = typeFactory.findTypeParameters(type, XmlAdapter.class);
        this._valueType = rawTypes == null || rawTypes.length == 0 ? TypeFactory.unknownType() : rawTypes[0];
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonDeserializer<Object> deser = this._deserializer;
        if (deser == null) {
            DeserializationConfig config = ctxt.getConfig();
            deser = ctxt.getDeserializerProvider().findValueDeserializer(config, this._valueType, null);
            this._deserializer = deser;
        }
        Object boundObject = deser.deserialize(jp, ctxt);
        try {
            return this._xmlAdapter.unmarshal(boundObject);
        }
        catch (Exception e) {
            throw new JsonMappingException("Unable to unmarshal (to type " + this._valueType + "): " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
}

