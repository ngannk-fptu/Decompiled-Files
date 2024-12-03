/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.io.NumberInput
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.cfg.CoercionAction
 *  com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer
 *  com.fasterxml.jackson.databind.jsontype.TypeDeserializer
 *  com.fasterxml.jackson.databind.type.LogicalType
 *  com.fasterxml.jackson.databind.util.ClassUtil
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;

abstract class JodaDeserializerBase<T>
extends StdScalarDeserializer<T> {
    protected JodaDeserializerBase(Class<?> cls) {
        super(cls);
    }

    protected JodaDeserializerBase(JodaDeserializerBase<?> src) {
        super(src);
    }

    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }

    public LogicalType logicalType() {
        return LogicalType.DateTime;
    }

    protected boolean _isValidTimestampString(String str) {
        return this._isIntNumber(str) && NumberInput.inLongRange((String)str, (str.charAt(0) == '-' ? 1 : 0) != 0);
    }

    protected T _fromEmptyString(JsonParser p, DeserializationContext ctxt, String str) throws IOException {
        CoercionAction act = this._checkFromStringCoercion(ctxt, str);
        switch (act) {
            case AsEmpty: {
                return (T)this.getEmptyValue(ctxt);
            }
        }
        return null;
    }

    public T _handleNotNumberOrString(JsonParser p, DeserializationContext ctxt) throws IOException {
        JavaType type = this.getValueType(ctxt);
        return (T)ctxt.handleUnexpectedToken(type, p.currentToken(), p, String.format("Cannot deserialize value of type %s from `JsonToken.%s`: expected Number or String", ClassUtil.getTypeDescription((JavaType)type), p.currentToken()), new Object[0]);
    }
}

