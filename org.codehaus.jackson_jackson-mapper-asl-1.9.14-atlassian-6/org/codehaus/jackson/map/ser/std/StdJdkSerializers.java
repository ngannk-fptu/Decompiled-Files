/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 */
package org.codehaus.jackson.map.ser.std;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.NullSerializer;
import org.codehaus.jackson.map.ser.std.ScalarSerializerBase;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.codehaus.jackson.map.util.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StdJdkSerializers
implements Provider<Map.Entry<Class<?>, Object>> {
    @Override
    public Collection<Map.Entry<Class<?>, Object>> provide() {
        HashMap<Class, Object> sers = new HashMap<Class, Object>();
        ToStringSerializer sls = ToStringSerializer.instance;
        sers.put(URL.class, sls);
        sers.put(URI.class, sls);
        sers.put(Currency.class, sls);
        sers.put(UUID.class, sls);
        sers.put(Pattern.class, sls);
        sers.put(Locale.class, sls);
        sers.put(Locale.class, sls);
        sers.put(AtomicReference.class, AtomicReferenceSerializer.class);
        sers.put(AtomicBoolean.class, AtomicBooleanSerializer.class);
        sers.put(AtomicInteger.class, AtomicIntegerSerializer.class);
        sers.put(AtomicLong.class, AtomicLongSerializer.class);
        sers.put(File.class, FileSerializer.class);
        sers.put(Class.class, ClassSerializer.class);
        sers.put(Void.TYPE, NullSerializer.class);
        return sers.entrySet();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class ClassSerializer
    extends ScalarSerializerBase<Class<?>> {
        public ClassSerializer() {
            super(Class.class, false);
        }

        @Override
        public void serialize(Class<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value.getName());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class FileSerializer
    extends ScalarSerializerBase<File> {
        public FileSerializer() {
            super(File.class);
        }

        @Override
        public void serialize(File value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeString(value.getAbsolutePath());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("string", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class AtomicReferenceSerializer
    extends SerializerBase<AtomicReference<?>> {
        public AtomicReferenceSerializer() {
            super(AtomicReference.class, false);
        }

        @Override
        public void serialize(AtomicReference<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            provider.defaultSerializeValue(value.get(), jgen);
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("any", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class AtomicLongSerializer
    extends ScalarSerializerBase<AtomicLong> {
        public AtomicLongSerializer() {
            super(AtomicLong.class, false);
        }

        @Override
        public void serialize(AtomicLong value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value.get());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("integer", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class AtomicIntegerSerializer
    extends ScalarSerializerBase<AtomicInteger> {
        public AtomicIntegerSerializer() {
            super(AtomicInteger.class, false);
        }

        @Override
        public void serialize(AtomicInteger value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeNumber(value.get());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("integer", true);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static final class AtomicBooleanSerializer
    extends ScalarSerializerBase<AtomicBoolean> {
        public AtomicBooleanSerializer() {
            super(AtomicBoolean.class, false);
        }

        @Override
        public void serialize(AtomicBoolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
            jgen.writeBoolean(value.get());
        }

        @Override
        public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
            return this.createSchemaNode("boolean", true);
        }
    }
}

