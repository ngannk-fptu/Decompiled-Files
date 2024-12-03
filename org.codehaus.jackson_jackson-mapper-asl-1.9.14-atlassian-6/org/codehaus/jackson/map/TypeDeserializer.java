/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 */
package org.codehaus.jackson.map;

import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TypeDeserializer {
    public abstract JsonTypeInfo.As getTypeInclusion();

    public abstract String getPropertyName();

    public abstract TypeIdResolver getTypeIdResolver();

    public abstract Class<?> getDefaultImpl();

    public abstract Object deserializeTypedFromObject(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException;

    public abstract Object deserializeTypedFromArray(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException;

    public abstract Object deserializeTypedFromScalar(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException;

    public abstract Object deserializeTypedFromAny(JsonParser var1, DeserializationContext var2) throws IOException, JsonProcessingException;
}

