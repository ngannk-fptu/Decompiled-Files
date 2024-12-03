/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 */
package org.codehaus.jackson.map;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.TypeDeserializer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class JsonDeserializer<T> {
    public abstract T deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException;

    public T deserialize(JsonParser jp, DeserializationContext ctxt, T intoValue) throws IOException, JsonProcessingException {
        throw new UnsupportedOperationException("Can not update object of type " + intoValue.getClass().getName() + " (by deserializer of type " + this.getClass().getName() + ")");
    }

    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }

    public JsonDeserializer<T> unwrappingDeserializer() {
        return this;
    }

    public T getNullValue() {
        return null;
    }

    public T getEmptyValue() {
        return this.getNullValue();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class None
    extends JsonDeserializer<Object> {
    }
}

