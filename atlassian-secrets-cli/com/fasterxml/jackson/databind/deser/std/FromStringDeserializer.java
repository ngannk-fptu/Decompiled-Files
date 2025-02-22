/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public abstract class FromStringDeserializer<T>
extends StdScalarDeserializer<T> {
    public static Class<?>[] types() {
        return new Class[]{File.class, URL.class, URI.class, Class.class, JavaType.class, Currency.class, Pattern.class, Locale.class, Charset.class, TimeZone.class, InetAddress.class, InetSocketAddress.class, StringBuilder.class};
    }

    protected FromStringDeserializer(Class<?> vc) {
        super(vc);
    }

    public static FromStringDeserializer<?> findDeserializer(Class<?> rawType) {
        int kind = 0;
        if (rawType == File.class) {
            kind = 1;
        } else if (rawType == URL.class) {
            kind = 2;
        } else if (rawType == URI.class) {
            kind = 3;
        } else if (rawType == Class.class) {
            kind = 4;
        } else if (rawType == JavaType.class) {
            kind = 5;
        } else if (rawType == Currency.class) {
            kind = 6;
        } else if (rawType == Pattern.class) {
            kind = 7;
        } else if (rawType == Locale.class) {
            kind = 8;
        } else if (rawType == Charset.class) {
            kind = 9;
        } else if (rawType == TimeZone.class) {
            kind = 10;
        } else if (rawType == InetAddress.class) {
            kind = 11;
        } else if (rawType == InetSocketAddress.class) {
            kind = 12;
        } else {
            if (rawType == StringBuilder.class) {
                return new StringBuilderDeserializer();
            }
            return null;
        }
        return new Std(rawType, kind);
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.OtherScalar;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getValueAsString();
        if (text == null) {
            JsonToken t = p.currentToken();
            if (t != JsonToken.START_OBJECT) {
                return (T)this._deserializeFromOther(p, ctxt, t);
            }
            text = ctxt.extractScalarFromObject(p, this, this._valueClass);
        }
        if (text.isEmpty() || (text = text.trim()).isEmpty()) {
            return (T)this._deserializeFromEmptyString(ctxt);
        }
        Exception cause = null;
        try {
            return this._deserialize(text, ctxt);
        }
        catch (IllegalArgumentException | MalformedURLException e) {
            cause = e;
            String msg = "not a valid textual representation";
            String m2 = cause.getMessage();
            if (m2 != null) {
                msg = msg + ", problem: " + m2;
            }
            JsonMappingException e2 = ctxt.weirdStringException(text, this._valueClass, msg);
            e2.initCause(cause);
            throw e2;
        }
    }

    protected abstract T _deserialize(String var1, DeserializationContext var2) throws IOException;

    protected Object _deserializeFromOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
        if (t == JsonToken.START_ARRAY) {
            return this._deserializeFromArray(p, ctxt);
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (this._valueClass.isAssignableFrom(ob.getClass())) {
                return ob;
            }
            return this._deserializeEmbedded(ob, ctxt);
        }
        return ctxt.handleUnexpectedToken(this._valueClass, p);
    }

    protected T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException {
        ctxt.reportInputMismatch(this, "Don't know how to convert embedded Object of type %s into %s", ob.getClass().getName(), this._valueClass.getName());
        return null;
    }

    @Deprecated
    protected final T _deserializeFromEmptyString() throws IOException {
        return null;
    }

    protected Object _deserializeFromEmptyString(DeserializationContext ctxt) throws IOException {
        CoercionAction act = ctxt.findCoercionAction(this.logicalType(), this._valueClass, CoercionInputShape.EmptyString);
        if (act == CoercionAction.Fail) {
            ctxt.reportInputMismatch(this, "Cannot coerce empty String (\"\") to %s (but could if enabling coercion using `CoercionConfig`)", this._coercedTypeDesc());
        }
        if (act == CoercionAction.AsNull) {
            return this.getNullValue(ctxt);
        }
        if (act == CoercionAction.AsEmpty) {
            return this.getEmptyValue(ctxt);
        }
        return this._deserializeFromEmptyStringDefault(ctxt);
    }

    protected Object _deserializeFromEmptyStringDefault(DeserializationContext ctxt) throws IOException {
        return this.getNullValue(ctxt);
    }

    static class StringBuilderDeserializer
    extends FromStringDeserializer<Object> {
        public StringBuilderDeserializer() {
            super(StringBuilder.class);
        }

        @Override
        public LogicalType logicalType() {
            return LogicalType.Textual;
        }

        @Override
        public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            return new StringBuilder();
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            if (text != null) {
                return this._deserialize(text, ctxt);
            }
            return super.deserialize(p, ctxt);
        }

        @Override
        protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return new StringBuilder(value);
        }
    }

    public static class Std
    extends FromStringDeserializer<Object> {
        private static final long serialVersionUID = 1L;
        public static final int STD_FILE = 1;
        public static final int STD_URL = 2;
        public static final int STD_URI = 3;
        public static final int STD_CLASS = 4;
        public static final int STD_JAVA_TYPE = 5;
        public static final int STD_CURRENCY = 6;
        public static final int STD_PATTERN = 7;
        public static final int STD_LOCALE = 8;
        public static final int STD_CHARSET = 9;
        public static final int STD_TIME_ZONE = 10;
        public static final int STD_INET_ADDRESS = 11;
        public static final int STD_INET_SOCKET_ADDRESS = 12;
        protected final int _kind;

        protected Std(Class<?> valueType, int kind) {
            super(valueType);
            this._kind = kind;
        }

        @Override
        protected Object _deserialize(String value, DeserializationContext ctxt) throws IOException {
            switch (this._kind) {
                case 1: {
                    return new File(value);
                }
                case 2: {
                    return new URL(value);
                }
                case 3: {
                    return URI.create(value);
                }
                case 4: {
                    try {
                        return ctxt.findClass(value);
                    }
                    catch (Exception e) {
                        return ctxt.handleInstantiationProblem(this._valueClass, value, ClassUtil.getRootCause(e));
                    }
                }
                case 5: {
                    return ctxt.getTypeFactory().constructFromCanonical(value);
                }
                case 6: {
                    return Currency.getInstance(value);
                }
                case 7: {
                    return Pattern.compile(value);
                }
                case 8: {
                    int ix = this._firstHyphenOrUnderscore(value);
                    if (ix < 0) {
                        return new Locale(value);
                    }
                    String first = value.substring(0, ix);
                    if ((ix = this._firstHyphenOrUnderscore(value = value.substring(ix + 1))) < 0) {
                        return new Locale(first, value);
                    }
                    String second = value.substring(0, ix);
                    return new Locale(first, second, value.substring(ix + 1));
                }
                case 9: {
                    return Charset.forName(value);
                }
                case 10: {
                    return TimeZone.getTimeZone(value);
                }
                case 11: {
                    return InetAddress.getByName(value);
                }
                case 12: {
                    if (value.startsWith("[")) {
                        int i = value.lastIndexOf(93);
                        if (i == -1) {
                            throw new InvalidFormatException(ctxt.getParser(), "Bracketed IPv6 address must contain closing bracket", (Object)value, InetSocketAddress.class);
                        }
                        int j = value.indexOf(58, i);
                        int port = j > -1 ? Integer.parseInt(value.substring(j + 1)) : 0;
                        return new InetSocketAddress(value.substring(0, i + 1), port);
                    }
                    int ix = value.indexOf(58);
                    if (ix >= 0 && value.indexOf(58, ix + 1) < 0) {
                        int port = Integer.parseInt(value.substring(ix + 1));
                        return new InetSocketAddress(value.substring(0, ix), port);
                    }
                    return new InetSocketAddress(value, 0);
                }
            }
            VersionUtil.throwInternal();
            return null;
        }

        @Override
        public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
            switch (this._kind) {
                case 3: {
                    return URI.create("");
                }
                case 8: {
                    return Locale.ROOT;
                }
            }
            return super.getEmptyValue(ctxt);
        }

        @Override
        protected Object _deserializeFromEmptyStringDefault(DeserializationContext ctxt) throws IOException {
            return this.getEmptyValue(ctxt);
        }

        protected int _firstHyphenOrUnderscore(String str) {
            int end = str.length();
            for (int i = 0; i < end; ++i) {
                char c = str.charAt(i);
                if (c != '_' && c != '-') continue;
                return i;
            }
            return -1;
        }
    }
}

