/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.TypeDeserializer
 *  org.codehaus.jackson.map.deser.std.ContainerDeserializerBase
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.confluence.rest.serialization;

import java.io.IOException;
import java.util.Optional;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.type.JavaType;

public class OptionalDeserializer
extends ContainerDeserializerBase<Optional> {
    private final JsonDeserializer<Object> elementDeserializer;
    private final JavaType contentType;

    public OptionalDeserializer(JsonDeserializer elementDeserializer, JavaType contentType) {
        super(Optional.class);
        this.elementDeserializer = elementDeserializer;
        this.contentType = contentType;
    }

    public Optional deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return Optional.of(typeDeserializer.deserializeTypedFromAny(jp, ctxt));
    }

    public Optional deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Object containedObject = this.elementDeserializer.deserialize(jp, ctxt);
        return Optional.of(containedObject);
    }

    public Optional getNullValue() {
        return Optional.empty();
    }

    public JavaType getContentType() {
        return this.contentType;
    }

    public JsonDeserializer<Object> getContentDeserializer() {
        return this.elementDeserializer;
    }
}

