/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.io.NumberInput
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.io.NumberInput;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.EnumResolver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class StdKeyDeserializer
extends KeyDeserializer {
    protected final Class<?> _keyClass;

    protected StdKeyDeserializer(Class<?> cls) {
        this._keyClass = cls;
    }

    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (key == null) {
            return null;
        }
        try {
            Object result = this._parse(key, ctxt);
            if (result != null) {
                return result;
            }
        }
        catch (Exception re) {
            throw ctxt.weirdKeyException(this._keyClass, key, "not a valid representation: " + re.getMessage());
        }
        throw ctxt.weirdKeyException(this._keyClass, key, "not a valid representation");
    }

    public Class<?> getKeyClass() {
        return this._keyClass;
    }

    protected abstract Object _parse(String var1, DeserializationContext var2) throws Exception;

    protected int _parseInt(String key) throws IllegalArgumentException {
        return Integer.parseInt(key);
    }

    protected long _parseLong(String key) throws IllegalArgumentException {
        return Long.parseLong(key);
    }

    protected double _parseDouble(String key) throws IllegalArgumentException {
        return NumberInput.parseDouble((String)key);
    }

    static final class UuidKD
    extends StdKeyDeserializer {
        protected UuidKD() {
            super(UUID.class);
        }

        public UUID _parse(String key, DeserializationContext ctxt) throws IllegalArgumentException, JsonMappingException {
            return UUID.fromString(key);
        }
    }

    static final class CalendarKD
    extends StdKeyDeserializer {
        protected CalendarKD() {
            super(Calendar.class);
        }

        public Calendar _parse(String key, DeserializationContext ctxt) throws IllegalArgumentException, JsonMappingException {
            Date date = ctxt.parseDate(key);
            return date == null ? null : ctxt.constructCalendar(date);
        }
    }

    static final class DateKD
    extends StdKeyDeserializer {
        protected DateKD() {
            super(Date.class);
        }

        public Date _parse(String key, DeserializationContext ctxt) throws IllegalArgumentException, JsonMappingException {
            return ctxt.parseDate(key);
        }
    }

    static final class StringFactoryKeyDeserializer
    extends StdKeyDeserializer {
        final Method _factoryMethod;

        public StringFactoryKeyDeserializer(Method fm) {
            super(fm.getDeclaringClass());
            this._factoryMethod = fm;
        }

        public Object _parse(String key, DeserializationContext ctxt) throws Exception {
            return this._factoryMethod.invoke(null, key);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class StringCtorKeyDeserializer
    extends StdKeyDeserializer {
        protected final Constructor<?> _ctor;

        public StringCtorKeyDeserializer(Constructor<?> ctor) {
            super(ctor.getDeclaringClass());
            this._ctor = ctor;
        }

        @Override
        public Object _parse(String key, DeserializationContext ctxt) throws Exception {
            return this._ctor.newInstance(key);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class EnumKD
    extends StdKeyDeserializer {
        protected final EnumResolver<?> _resolver;
        protected final AnnotatedMethod _factory;

        protected EnumKD(EnumResolver<?> er, AnnotatedMethod factory) {
            super(er.getEnumClass());
            this._resolver = er;
            this._factory = factory;
        }

        @Override
        public Object _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            Object e;
            if (this._factory != null) {
                try {
                    return this._factory.call1(key);
                }
                catch (Exception e2) {
                    ClassUtil.unwrapAndThrowAsIAE(e2);
                }
            }
            if ((e = this._resolver.findEnum(key)) == null) {
                throw ctxt.weirdKeyException(this._keyClass, key, "not one of values for Enum class");
            }
            return e;
        }
    }

    static final class FloatKD
    extends StdKeyDeserializer {
        FloatKD() {
            super(Float.class);
        }

        public Float _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            return Float.valueOf((float)this._parseDouble(key));
        }
    }

    static final class DoubleKD
    extends StdKeyDeserializer {
        DoubleKD() {
            super(Double.class);
        }

        public Double _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            return this._parseDouble(key);
        }
    }

    static final class LongKD
    extends StdKeyDeserializer {
        LongKD() {
            super(Long.class);
        }

        public Long _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            return this._parseLong(key);
        }
    }

    static final class IntKD
    extends StdKeyDeserializer {
        IntKD() {
            super(Integer.class);
        }

        public Integer _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            return this._parseInt(key);
        }
    }

    static final class CharKD
    extends StdKeyDeserializer {
        CharKD() {
            super(Character.class);
        }

        public Character _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            if (key.length() == 1) {
                return Character.valueOf(key.charAt(0));
            }
            throw ctxt.weirdKeyException(this._keyClass, key, "can only convert 1-character Strings");
        }
    }

    static final class ShortKD
    extends StdKeyDeserializer {
        ShortKD() {
            super(Integer.class);
        }

        public Short _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            int value = this._parseInt(key);
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                throw ctxt.weirdKeyException(this._keyClass, key, "overflow, value can not be represented as 16-bit value");
            }
            return (short)value;
        }
    }

    static final class ByteKD
    extends StdKeyDeserializer {
        ByteKD() {
            super(Byte.class);
        }

        public Byte _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            int value = this._parseInt(key);
            if (value < -128 || value > 255) {
                throw ctxt.weirdKeyException(this._keyClass, key, "overflow, value can not be represented as 8-bit value");
            }
            return (byte)value;
        }
    }

    static final class BoolKD
    extends StdKeyDeserializer {
        BoolKD() {
            super(Boolean.class);
        }

        public Boolean _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            if ("true".equals(key)) {
                return Boolean.TRUE;
            }
            if ("false".equals(key)) {
                return Boolean.FALSE;
            }
            throw ctxt.weirdKeyException(this._keyClass, key, "value not 'true' or 'false'");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static final class StringKD
    extends StdKeyDeserializer {
        private static final StringKD sString = new StringKD(String.class);
        private static final StringKD sObject = new StringKD(Object.class);

        private StringKD(Class<?> nominalType) {
            super(nominalType);
        }

        public static StringKD forType(Class<?> nominalType) {
            if (nominalType == String.class) {
                return sString;
            }
            if (nominalType == Object.class) {
                return sObject;
            }
            return new StringKD(nominalType);
        }

        @Override
        public String _parse(String key, DeserializationContext ctxt) throws JsonMappingException {
            return key;
        }
    }
}

