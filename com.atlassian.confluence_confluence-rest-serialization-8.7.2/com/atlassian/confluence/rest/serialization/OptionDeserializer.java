/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.TypeDeserializer
 *  org.codehaus.jackson.map.deser.std.ContainerDeserializerBase
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.fugue.Option;
import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.type.JavaType;

@Deprecated
public class OptionDeserializer
extends ContainerDeserializerBase<Option> {
    private final JsonDeserializer<Object> elementDeserializer;
    private final JavaType contentType;

    public OptionDeserializer(JsonDeserializer elementDeserializer, JavaType contentType) {
        super(Option.class);
        this.elementDeserializer = elementDeserializer;
        this.contentType = contentType;
    }

    public Option deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
        return Option.some((Object)typeDeserializer.deserializeTypedFromAny(jp, ctxt));
    }

    public Option deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Object containedObject = this.elementDeserializer.deserialize(jp, ctxt);
        return Option.some((Object)containedObject);
    }

    public Option getNullValue() {
        return Option.none();
    }

    public JavaType getContentType() {
        return this.contentType;
    }

    public JsonDeserializer<Object> getContentDeserializer() {
        return this.elementDeserializer;
    }
}

