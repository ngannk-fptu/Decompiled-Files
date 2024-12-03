/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.link.LinkType
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.map.DeserializationConfig
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.DeserializerProvider
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ResolvableDeserializer
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.link.LinkType;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import java.io.IOException;
import java.lang.reflect.Field;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ResolvableDeserializer;

public class ContentDeserializer
extends JsonDeserializer<Content>
implements ResolvableDeserializer {
    private final JsonDeserializer<?> defaultDeserializer;
    private final JsonFactory jsonFactory;

    public ContentDeserializer(JsonDeserializer<?> defaultDeserializer) {
        this.defaultDeserializer = defaultDeserializer;
        this.jsonFactory = new JsonFactory();
    }

    public void resolve(DeserializationConfig config, DeserializerProvider provider) throws JsonMappingException {
        ((ResolvableDeserializer)this.defaultDeserializer).resolve(config, provider);
    }

    public Content deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.readValueAsTree();
        try (JsonParser parser = this.jsonFactory.createJsonParser(node.toString());){
            parser.setCodec(jp.getCodec());
            parser.nextToken();
            Content content = (Content)this.defaultDeserializer.deserialize(parser, ctxt);
            if (node.has("_links") || node.has("links")) {
                this.deserializeLinks(node, content);
            }
            Content content2 = content;
            return content2;
        }
    }

    private void deserializeLinks(JsonNode node, Content content) {
        JsonNode linkNode = node.has("_links") ? node.get("_links") : node.get("links");
        ModelMapBuilder mapBuilder = ModelMapBuilder.newInstance();
        LinkType.BUILT_IN.stream().filter(linkType -> linkNode.has(linkType.serialise())).forEach(linkType -> {
            String linkPath = linkNode.get(linkType.serialise()).asText();
            mapBuilder.put(linkType, (Object)new Link(linkType, linkPath));
        });
        ContentDeserializer.setFieldValue(content, "links", mapBuilder.build());
    }

    private static void setFieldValue(Content content, String fieldName, Object value) {
        try {
            Field field = Content.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(content, value);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}

