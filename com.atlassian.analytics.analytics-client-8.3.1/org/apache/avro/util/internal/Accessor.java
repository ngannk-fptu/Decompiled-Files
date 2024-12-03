/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro.util.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.apache.avro.JsonProperties;
import org.apache.avro.Schema;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;

public class Accessor {
    private static volatile JsonPropertiesAccessor jsonPropertiesAccessor;
    private static volatile FieldAccessor fieldAccessor;
    private static volatile ResolvingGrammarGeneratorAccessor resolvingGrammarGeneratorAccessor;

    public static void setAccessor(JsonPropertiesAccessor accessor) {
        if (jsonPropertiesAccessor != null) {
            throw new IllegalStateException("JsonPropertiesAccessor already initialized");
        }
        jsonPropertiesAccessor = accessor;
    }

    public static void setAccessor(FieldAccessor accessor) {
        if (fieldAccessor != null) {
            throw new IllegalStateException("FieldAccessor already initialized");
        }
        fieldAccessor = accessor;
    }

    private static FieldAccessor fieldAccessor() {
        if (fieldAccessor == null) {
            Accessor.ensureLoaded(Schema.Field.class);
        }
        return fieldAccessor;
    }

    public static void setAccessor(ResolvingGrammarGeneratorAccessor accessor) {
        if (resolvingGrammarGeneratorAccessor != null) {
            throw new IllegalStateException("ResolvingGrammarGeneratorAccessor already initialized");
        }
        resolvingGrammarGeneratorAccessor = accessor;
    }

    private static ResolvingGrammarGeneratorAccessor resolvingGrammarGeneratorAccessor() {
        if (resolvingGrammarGeneratorAccessor == null) {
            Accessor.ensureLoaded(ResolvingGrammarGenerator.class);
        }
        return resolvingGrammarGeneratorAccessor;
    }

    private static void ensureLoaded(Class<?> c) {
        try {
            Class.forName(c.getName());
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }

    public static void addProp(JsonProperties props, String name, JsonNode value) {
        jsonPropertiesAccessor.addProp(props, name, value);
    }

    public static JsonNode defaultValue(Schema.Field field) {
        return fieldAccessor.defaultValue(field);
    }

    public static void encode(Encoder e, Schema s, JsonNode n) throws IOException {
        Accessor.resolvingGrammarGeneratorAccessor().encode(e, s, n);
    }

    public static Schema.Field createField(String name, Schema schema, String doc, JsonNode defaultValue, boolean validate, Schema.Field.Order order) {
        return Accessor.fieldAccessor().createField(name, schema, doc, defaultValue, validate, order);
    }

    public static Schema.Field createField(String name, Schema schema, String doc, JsonNode defaultValue) {
        return Accessor.fieldAccessor().createField(name, schema, doc, defaultValue);
    }

    public static abstract class EncoderFactoryAccessor {
        protected abstract JsonEncoder jsonEncoder(EncoderFactory var1, Schema var2, JsonGenerator var3) throws IOException;
    }

    public static abstract class ResolvingGrammarGeneratorAccessor {
        protected abstract void encode(Encoder var1, Schema var2, JsonNode var3) throws IOException;
    }

    public static abstract class FieldAccessor {
        protected abstract JsonNode defaultValue(Schema.Field var1);

        protected abstract Schema.Field createField(String var1, Schema var2, String var3, JsonNode var4, boolean var5, Schema.Field.Order var6);

        protected abstract Schema.Field createField(String var1, Schema var2, String var3, JsonNode var4);
    }

    public static abstract class JsonPropertiesAccessor {
        protected abstract void addProp(JsonProperties var1, String var2, JsonNode var3);
    }
}

