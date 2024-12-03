/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.lang.reflect.Method;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonCachable;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.EnumResolver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JsonCachable
public class EnumDeserializer
extends StdScalarDeserializer<Enum<?>> {
    protected final EnumResolver<?> _resolver;

    public EnumDeserializer(EnumResolver<?> res) {
        super(Enum.class);
        this._resolver = res;
    }

    public static JsonDeserializer<?> deserializerForCreator(DeserializationConfig config, Class<?> enumClass, AnnotatedMethod factory) {
        Class<Object> raw = factory.getParameterClass(0);
        if (raw == String.class) {
            raw = null;
        } else if (raw == Integer.TYPE || raw == Integer.class) {
            raw = Integer.class;
        } else if (raw == Long.TYPE || raw == Long.class) {
            raw = Long.class;
        } else {
            throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String or int/Integer/long/Long");
        }
        if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            ClassUtil.checkAndFixAccess(factory.getMember());
        }
        return new FactoryBasedDeserializer(enumClass, factory, raw);
    }

    @Override
    public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken curr = jp.getCurrentToken();
        if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
            String name = jp.getText();
            Object result = this._resolver.findEnum(name);
            if (result == null) {
                throw ctxt.weirdStringException(this._resolver.getEnumClass(), "value not one of declared Enum instance names");
            }
            return result;
        }
        if (curr == JsonToken.VALUE_NUMBER_INT) {
            if (ctxt.isEnabled(DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS)) {
                throw ctxt.mappingException("Not allowed to deserialize Enum value out of JSON number (disable DeserializationConfig.Feature.FAIL_ON_NUMBERS_FOR_ENUMS to allow)");
            }
            int index = jp.getIntValue();
            Object result = this._resolver.getEnum(index);
            if (result == null) {
                throw ctxt.weirdNumberException(this._resolver.getEnumClass(), "index value outside legal index range [0.." + this._resolver.lastValidIndex() + "]");
            }
            return result;
        }
        throw ctxt.mappingException(this._resolver.getEnumClass());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class FactoryBasedDeserializer
    extends StdScalarDeserializer<Object> {
        protected final Class<?> _enumClass;
        protected final Class<?> _inputType;
        protected final Method _factory;

        public FactoryBasedDeserializer(Class<?> cls, AnnotatedMethod f, Class<?> inputType) {
            super(Enum.class);
            this._enumClass = cls;
            this._factory = f.getAnnotated();
            this._inputType = inputType;
        }

        @Override
        public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            Object value;
            if (this._inputType == null) {
                value = jp.getText();
            } else if (this._inputType == Integer.class) {
                value = jp.getValueAsInt();
            } else if (this._inputType == Long.class) {
                value = jp.getValueAsLong();
            } else {
                throw ctxt.mappingException(this._enumClass);
            }
            try {
                return this._factory.invoke(this._enumClass, value);
            }
            catch (Exception e) {
                ClassUtil.unwrapAndThrowAsIAE(e);
                return null;
            }
        }
    }
}

