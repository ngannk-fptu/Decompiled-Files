/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class JsonSerializer<T> {
    public JsonSerializer<T> unwrappingSerializer() {
        return this;
    }

    public boolean isUnwrappingSerializer() {
        return false;
    }

    public abstract void serialize(T var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonProcessingException;

    public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        this.serialize(value, jgen, provider);
    }

    public Class<T> handledType() {
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class None
    extends JsonSerializer<Object> {
    }
}

