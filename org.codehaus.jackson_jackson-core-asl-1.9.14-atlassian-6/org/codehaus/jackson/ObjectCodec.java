/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson;

import java.io.IOException;
import java.util.Iterator;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class ObjectCodec {
    protected ObjectCodec() {
    }

    public abstract <T> T readValue(JsonParser var1, Class<T> var2) throws IOException, JsonProcessingException;

    public abstract <T> T readValue(JsonParser var1, TypeReference<?> var2) throws IOException, JsonProcessingException;

    public abstract <T> T readValue(JsonParser var1, JavaType var2) throws IOException, JsonProcessingException;

    public abstract JsonNode readTree(JsonParser var1) throws IOException, JsonProcessingException;

    public abstract <T> Iterator<T> readValues(JsonParser var1, Class<T> var2) throws IOException, JsonProcessingException;

    public abstract <T> Iterator<T> readValues(JsonParser var1, TypeReference<?> var2) throws IOException, JsonProcessingException;

    public abstract <T> Iterator<T> readValues(JsonParser var1, JavaType var2) throws IOException, JsonProcessingException;

    public abstract void writeValue(JsonGenerator var1, Object var2) throws IOException, JsonProcessingException;

    public abstract void writeTree(JsonGenerator var1, JsonNode var2) throws IOException, JsonProcessingException;

    public abstract JsonNode createObjectNode();

    public abstract JsonNode createArrayNode();

    public abstract JsonParser treeAsTokens(JsonNode var1);

    public abstract <T> T treeToValue(JsonNode var1, Class<T> var2) throws IOException, JsonProcessingException;
}

