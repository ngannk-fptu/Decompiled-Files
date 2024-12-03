/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.deser;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AbstractDeserializer
extends JsonDeserializer<Object> {
    protected final JavaType _baseType;
    protected final boolean _acceptString;
    protected final boolean _acceptBoolean;
    protected final boolean _acceptInt;
    protected final boolean _acceptDouble;

    public AbstractDeserializer(JavaType bt) {
        this._baseType = bt;
        Class cls = bt.getRawClass();
        this._acceptString = cls.isAssignableFrom(String.class);
        this._acceptBoolean = cls == Boolean.TYPE || cls.isAssignableFrom(Boolean.class);
        this._acceptInt = cls == Integer.TYPE || cls.isAssignableFrom(Integer.class);
        this._acceptDouble = cls == Double.TYPE || cls.isAssignableFrom(Double.class);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        Object result = this._deserializeIfNatural(jp, ctxt);
        if (result != null) {
            return result;
        }
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw ctxt.instantiationException(this._baseType.getRawClass(), "abstract types can only be instantiated with additional type information");
    }

    protected Object _deserializeIfNatural(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getCurrentToken()) {
            case VALUE_STRING: {
                if (!this._acceptString) break;
                return jp.getText();
            }
            case VALUE_NUMBER_INT: {
                if (!this._acceptInt) break;
                return jp.getIntValue();
            }
            case VALUE_NUMBER_FLOAT: {
                if (!this._acceptDouble) break;
                return jp.getDoubleValue();
            }
            case VALUE_TRUE: {
                if (!this._acceptBoolean) break;
                return Boolean.TRUE;
            }
            case VALUE_FALSE: {
                if (!this._acceptBoolean) break;
                return Boolean.FALSE;
            }
        }
        return null;
    }
}

