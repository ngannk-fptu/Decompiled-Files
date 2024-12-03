/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.Cursor
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.pagination.CursorFactory
 *  com.atlassian.confluence.rest.api.model.RestList
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.JsonParser
 *  org.codehaus.jackson.JsonToken
 *  org.codehaus.jackson.map.DeserializationContext
 *  org.codehaus.jackson.map.JsonDeserializer
 *  org.codehaus.jackson.map.TypeDeserializer
 *  org.codehaus.jackson.map.deser.std.ContainerDeserializerBase
 *  org.codehaus.jackson.type.JavaType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.pagination.Cursor;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.pagination.CursorFactory;
import com.atlassian.confluence.rest.api.model.RestList;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestListDeserializer
extends ContainerDeserializerBase {
    private static final Logger log = LoggerFactory.getLogger(RestListDeserializer.class);
    private final JsonDeserializer contentDeserializer;
    private final JavaType contentType;
    private final TypeDeserializer elementTypeDeserializer;

    public RestListDeserializer(JsonDeserializer elementDeserializer, JavaType contentType, TypeDeserializer elementTypeDeserializer) {
        super(PageResponse.class);
        this.elementTypeDeserializer = elementTypeDeserializer;
        this.contentDeserializer = Objects.requireNonNull(elementDeserializer);
        this.contentType = Objects.requireNonNull(contentType);
    }

    public JavaType getContentType() {
        return this.contentType;
    }

    public JsonDeserializer<Object> getContentDeserializer() {
        return this.contentDeserializer;
    }

    public PageResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        RestList list;
        DeserializedRestListData deserializedData = null;
        if (JsonToken.START_ARRAY.equals((Object)jsonParser.getCurrentToken())) {
            deserializedData = new DeserializedRestListData();
            deserializedData.resultList = this.deserializeArray(jsonParser, deserializationContext);
        } else if (JsonToken.START_OBJECT.equals((Object)jsonParser.getCurrentToken())) {
            deserializedData = this.deserializeObject(jsonParser, deserializationContext);
        }
        if (deserializedData == null || deserializedData.resultList == null) {
            return null;
        }
        if (deserializedData.limit == null) {
            deserializedData.limit = deserializedData.resultList.size();
        }
        if (deserializedData.requestCursor == null) {
            if (deserializedData.start == null) {
                deserializedData.start = 0;
            }
            list = RestList.newRestList((PageRequest)new SimplePageRequest(deserializedData.start.intValue(), deserializedData.limit.intValue())).results((List)deserializedData.resultList, deserializedData.hasMore).build();
        } else {
            list = RestList.newRestList((PageRequest)new SimplePageRequest(deserializedData.requestCursor, deserializedData.limit.intValue())).results((List)deserializedData.resultList, deserializedData.nextCursor, deserializedData.prevCursor, deserializedData.hasMore).build();
        }
        list.putProperties(deserializedData.properties);
        return list;
    }

    private ImmutableList deserializeArray(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonToken token;
        ImmutableList.Builder results = ImmutableList.builder();
        while ((token = jsonParser.nextToken()) != JsonToken.END_ARRAY && token != null) {
            if (!token.equals((Object)JsonToken.START_OBJECT)) {
                throw new IllegalStateException();
            }
            results.add(this.deserializeNextElement(jsonParser, deserializationContext));
        }
        return results.build();
    }

    private Object deserializeNextElement(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (this.elementTypeDeserializer == null) {
            return this.contentDeserializer.deserialize(jsonParser, deserializationContext);
        }
        return this.contentDeserializer.deserializeWithType(jsonParser, deserializationContext, this.elementTypeDeserializer);
    }

    private DeserializedRestListData deserializeObject(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        DeserializedRestListData deserializedData = new DeserializedRestListData();
        JsonToken token = jsonParser.getCurrentToken();
        int openObjectCount = 0;
        do {
            if (token == JsonToken.END_OBJECT) {
                --openObjectCount;
                continue;
            }
            if (token == JsonToken.START_OBJECT) {
                ++openObjectCount;
                continue;
            }
            if (token != JsonToken.FIELD_NAME || jsonParser.getCurrentName() == null) continue;
            log.debug(String.format("Start parsing field %s", jsonParser.getCurrentName()));
            String fieldName = jsonParser.getCurrentName().toLowerCase();
            if (openObjectCount == 1) {
                this.deserializeFirstLevelField(jsonParser, deserializationContext, deserializedData, fieldName);
                continue;
            }
            this.deserializeField(jsonParser, deserializationContext, deserializedData, fieldName);
        } while (openObjectCount != 0 && (token = jsonParser.nextToken()) != null);
        return deserializedData;
    }

    private void deserializeFirstLevelField(JsonParser jsonParser, DeserializationContext deserializationContext, DeserializedRestListData deserializedData, String fieldName) throws IOException {
        switch (fieldName) {
            case "results": 
            case "start": 
            case "limit": 
            case "hasmore": 
            case "cursor": 
            case "nextcursor": 
            case "prevcursor": {
                this.deserializePageResponseField(jsonParser, deserializationContext, deserializedData, fieldName);
                break;
            }
            case "cqlquery": 
            case "totalsize": 
            case "searchduration": 
            case "archivedresultcount": {
                this.deserializeSearchPageResponseField(jsonParser, deserializedData, fieldName);
                break;
            }
            default: {
                this.deserializeField(jsonParser, deserializationContext, deserializedData, fieldName);
            }
        }
    }

    private void deserializePageResponseField(JsonParser jsonParser, DeserializationContext deserializationContext, DeserializedRestListData deserializedData, String fieldName) throws IOException {
        JsonToken token = jsonParser.nextToken();
        switch (fieldName) {
            case "results": {
                if (!token.equals((Object)JsonToken.START_ARRAY)) {
                    throw new IllegalStateException();
                }
                deserializedData.resultList = this.deserializeArray(jsonParser, deserializationContext);
                break;
            }
            case "start": {
                deserializedData.start = jsonParser.getIntValue();
                break;
            }
            case "limit": {
                deserializedData.limit = jsonParser.getIntValue();
                break;
            }
            case "hasmore": {
                deserializedData.hasMore = jsonParser.getBooleanValue();
                break;
            }
            case "cursor": {
                deserializedData.requestCursor = this.deserializeCursor(jsonParser);
                break;
            }
            case "nextcursor": {
                deserializedData.nextCursor = this.deserializeCursor(jsonParser);
                break;
            }
            case "prevcursor": {
                deserializedData.prevCursor = this.deserializeCursor(jsonParser);
                break;
            }
            default: {
                throw new IllegalStateException(String.format("Unexpected PageResponse field %s", fieldName));
            }
        }
    }

    private void deserializeSearchPageResponseField(JsonParser jsonParser, DeserializedRestListData deserializedData, String fieldName) throws IOException {
        jsonParser.nextToken();
        switch (fieldName) {
            case "cqlquery": {
                deserializedData.properties.put("cqlQuery", jsonParser.getText());
                break;
            }
            case "totalsize": {
                deserializedData.properties.put("totalSize", jsonParser.getIntValue());
                break;
            }
            case "searchduration": {
                deserializedData.properties.put("searchDuration", jsonParser.getIntValue());
                break;
            }
            case "archivedresultcount": {
                deserializedData.properties.put("archivedResultCount", jsonParser.getIntValue());
                break;
            }
            default: {
                throw new IllegalStateException(String.format("Unexpected SearchPageResponse field %s", fieldName));
            }
        }
    }

    private void deserializeField(JsonParser jsonParser, DeserializationContext deserializationContext, DeserializedRestListData deserializedData, String fieldName) throws IOException {
        switch (fieldName) {
            case "nodes": {
                JsonToken token = jsonParser.nextToken();
                if (!token.equals((Object)JsonToken.START_ARRAY)) {
                    throw new IllegalStateException();
                }
                deserializedData.resultList = this.deserializeArray(jsonParser, deserializationContext);
                break;
            }
            case "pageinfo": {
                jsonParser.nextToken();
                JsonNode pageInfo = jsonParser.readValueAsTree();
                deserializedData.hasMore = pageInfo.get("hasNextPage").asBoolean();
                break;
            }
            case "next": {
                jsonParser.nextToken();
                deserializedData.hasMore = true;
                break;
            }
            default: {
                log.debug(String.format("Deserializer for field %s not found", jsonParser.getCurrentName()));
            }
        }
    }

    private Cursor deserializeCursor(JsonParser jsonParser) throws IOException {
        String stringCursor = jsonParser.getText();
        return CursorFactory.buildFrom((String)stringCursor);
    }

    private static class DeserializedRestListData {
        private Integer start;
        private Integer limit;
        private boolean hasMore;
        private Cursor requestCursor;
        private Cursor nextCursor;
        private Cursor prevCursor;
        private final Map<String, Object> properties = new HashMap<String, Object>();
        private ImmutableList resultList;

        private DeserializedRestListData() {
        }
    }
}

