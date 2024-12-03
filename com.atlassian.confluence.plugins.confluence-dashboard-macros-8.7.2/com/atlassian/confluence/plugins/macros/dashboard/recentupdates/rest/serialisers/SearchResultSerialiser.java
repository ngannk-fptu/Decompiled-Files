/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchResult
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonProcessingException
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.rest.serialisers;

import com.atlassian.confluence.search.v2.SearchResult;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class SearchResultSerialiser
extends JsonSerializer<SearchResult> {
    public void serialize(SearchResult searchResult, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeStringField("displayTitle", searchResult.getDisplayTitle());
        jgen.writeStringField("lastModifier", searchResult.getLastModifier());
        jgen.writeStringField("content", searchResult.getContent());
        jgen.writeStringField("creator", searchResult.getCreator());
        jgen.writeStringField("lastUpdateDescription", searchResult.getLastUpdateDescription());
        jgen.writeStringField("ownerTitle", searchResult.getOwnerTitle());
        jgen.writeStringField("ownerType", searchResult.getOwnerType());
        jgen.writeStringField("spaceKey", searchResult.getSpaceKey());
        jgen.writeStringField("spaceName", searchResult.getSpaceName());
        jgen.writeStringField("type", searchResult.getType());
        jgen.writeStringField("urlPath", searchResult.getUrlPath());
        this.writeNumberIfNotNull(jgen, "contentVersion", searchResult.getContentVersion());
        this.writeNumberIfNotNull(jgen, "creationDate", searchResult.getCreationDate().getTime());
        this.writeNumberIfNotNull(jgen, "lastModificationDate", searchResult.getLastModificationDate().getTime());
        jgen.writeEndObject();
    }

    private void writeNumberIfNotNull(JsonGenerator jgen, String key, Integer number) throws IOException {
        if (number == null) {
            return;
        }
        jgen.writeNumberField(key, number.intValue());
    }

    private void writeNumberIfNotNull(JsonGenerator jgen, String key, Long number) throws IOException {
        if (number == null) {
            return;
        }
        jgen.writeNumberField(key, number.longValue());
    }
}

