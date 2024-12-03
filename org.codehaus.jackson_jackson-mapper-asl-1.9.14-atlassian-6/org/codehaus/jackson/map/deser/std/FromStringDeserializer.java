/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.JsonToken
 */
package org.codehaus.jackson.map.deser.std;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FromStringDeserializer<T>
extends StdScalarDeserializer<T> {
    protected FromStringDeserializer(Class<?> vc) {
        super(vc);
    }

    public static Iterable<FromStringDeserializer<?>> all() {
        ArrayList all = new ArrayList();
        all.add(new UUIDDeserializer());
        all.add(new URLDeserializer());
        all.add(new URIDeserializer());
        all.add(new CurrencyDeserializer());
        all.add(new PatternDeserializer());
        all.add(new LocaleDeserializer());
        all.add(new InetAddressDeserializer());
        all.add(new TimeZoneDeserializer());
        all.add(new CharsetDeserializer());
        return all;
    }

    @Override
    public final T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
            String text = jp.getText().trim();
            if (text.length() == 0) {
                return null;
            }
            try {
                T result = this._deserialize(text, ctxt);
                if (result != null) {
                    return result;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            throw ctxt.weirdStringException(this._valueClass, "not a valid textual representation");
        }
        if (jp.getCurrentToken() == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object ob = jp.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (this._valueClass.isAssignableFrom(ob.getClass())) {
                return (T)ob;
            }
            return this._deserializeEmbedded(ob, ctxt);
        }
        throw ctxt.mappingException(this._valueClass);
    }

    protected abstract T _deserialize(String var1, DeserializationContext var2) throws IOException, JsonProcessingException;

    protected T _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        throw ctxt.mappingException("Don't know how to convert embedded Object of type " + ob.getClass().getName() + " into " + this._valueClass.getName());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class TimeZoneDeserializer
    extends FromStringDeserializer<TimeZone> {
        public TimeZoneDeserializer() {
            super(TimeZone.class);
        }

        @Override
        protected TimeZone _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return TimeZone.getTimeZone(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class CharsetDeserializer
    extends FromStringDeserializer<Charset> {
        public CharsetDeserializer() {
            super(Charset.class);
        }

        @Override
        protected Charset _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return Charset.forName(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class InetAddressDeserializer
    extends FromStringDeserializer<InetAddress> {
        public InetAddressDeserializer() {
            super(InetAddress.class);
        }

        @Override
        protected InetAddress _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return InetAddress.getByName(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected static class LocaleDeserializer
    extends FromStringDeserializer<Locale> {
        public LocaleDeserializer() {
            super(Locale.class);
        }

        @Override
        protected Locale _deserialize(String value, DeserializationContext ctxt) throws IOException {
            int ix = value.indexOf(95);
            if (ix < 0) {
                return new Locale(value);
            }
            String first = value.substring(0, ix);
            if ((ix = (value = value.substring(ix + 1)).indexOf(95)) < 0) {
                return new Locale(first, value);
            }
            String second = value.substring(0, ix);
            return new Locale(first, second, value.substring(ix + 1));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class PatternDeserializer
    extends FromStringDeserializer<Pattern> {
        public PatternDeserializer() {
            super(Pattern.class);
        }

        @Override
        protected Pattern _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return Pattern.compile(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class CurrencyDeserializer
    extends FromStringDeserializer<Currency> {
        public CurrencyDeserializer() {
            super(Currency.class);
        }

        @Override
        protected Currency _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return Currency.getInstance(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class URIDeserializer
    extends FromStringDeserializer<URI> {
        public URIDeserializer() {
            super(URI.class);
        }

        @Override
        protected URI _deserialize(String value, DeserializationContext ctxt) throws IllegalArgumentException {
            return URI.create(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class URLDeserializer
    extends FromStringDeserializer<URL> {
        public URLDeserializer() {
            super(URL.class);
        }

        @Override
        protected URL _deserialize(String value, DeserializationContext ctxt) throws IOException {
            return new URL(value);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class UUIDDeserializer
    extends FromStringDeserializer<UUID> {
        public UUIDDeserializer() {
            super(UUID.class);
        }

        @Override
        protected UUID _deserialize(String value, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return UUID.fromString(value);
        }

        @Override
        protected UUID _deserializeEmbedded(Object ob, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (ob instanceof byte[]) {
                byte[] bytes = (byte[])ob;
                if (bytes.length != 16) {
                    ctxt.mappingException("Can only construct UUIDs from 16 byte arrays; got " + bytes.length + " bytes");
                }
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
                long l1 = in.readLong();
                long l2 = in.readLong();
                return new UUID(l1, l2);
            }
            super._deserializeEmbedded(ob, ctxt);
            return null;
        }
    }
}

