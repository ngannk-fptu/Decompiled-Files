/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.rest.api.model.RestList
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.rest.api.model.RestList;
import java.io.IOException;
import java.util.Map;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

public class PageResponseSerializer
extends SerializerBase<PageResponse<?>> {
    public PageResponseSerializer() {
        super(PageResponse.class, false);
    }

    public void serialize(PageResponse<?> value, JsonGenerator jsonGen, SerializerProvider provider) throws IOException {
        jsonGen.writeStartObject();
        jsonGen.writeArrayFieldStart("results");
        for (Object result : value.getResults()) {
            jsonGen.writeObject(result);
        }
        jsonGen.writeEndArray();
        this.serializePageRequestFields(value, jsonGen);
        jsonGen.writeObjectField("size", (Object)value.getResults().size());
        this.serializeSearchPageResponseFields(value, jsonGen);
        this.serializeContentRestrictionsFields(value, jsonGen);
        this.serializeRestListFields(value, jsonGen);
        jsonGen.writeEndObject();
        jsonGen.flush();
    }

    private void serializePageRequestFields(PageResponse<?> value, JsonGenerator jsonGen) throws IOException {
        PageRequest pageRequest = value.getPageRequest();
        if (pageRequest != null) {
            if (pageRequest.getCursor() == null) {
                jsonGen.writeObjectField("start", (Object)pageRequest.getStart());
            } else {
                jsonGen.writeObjectField("cursor", (Object)pageRequest.getCursor().toString());
                if (value.getNextCursor() != null) {
                    jsonGen.writeObjectField("nextCursor", (Object)value.getNextCursor().toString());
                }
                if (value.getPrevCursor() != null) {
                    jsonGen.writeObjectField("prevCursor", (Object)value.getPrevCursor().toString());
                }
            }
            jsonGen.writeObjectField("limit", (Object)pageRequest.getLimit());
        }
    }

    private void serializeSearchPageResponseFields(PageResponse<?> value, JsonGenerator jsonGen) throws IOException {
        if (value instanceof SearchPageResponse) {
            SearchPageResponse resp = (SearchPageResponse)value;
            jsonGen.writeObjectField("totalSize", (Object)resp.totalSize());
            jsonGen.writeObjectField("searchDuration", (Object)resp.getSearchDuration());
            jsonGen.writeObjectField("cqlQuery", (Object)resp.getCqlQuery());
            if (resp.archivedResultCount().isPresent()) {
                jsonGen.writeObjectField("archivedResultCount", resp.archivedResultCount().get());
            }
        }
    }

    private void serializeContentRestrictionsFields(PageResponse<?> value, JsonGenerator jsonGen) throws IOException {
        if (value instanceof ContentRestrictionsPageResponse) {
            ContentRestrictionsPageResponse resp = (ContentRestrictionsPageResponse)value;
            jsonGen.writeObjectField("restrictionsHash", (Object)resp.getRestrictionsHash());
        }
    }

    private void serializeRestListFields(PageResponse<?> value, JsonGenerator jsonGen) throws IOException {
        if (value instanceof RestList) {
            RestList restList = (RestList)value;
            for (Map.Entry prop : restList.properties().entrySet()) {
                jsonGen.writeFieldName((String)prop.getKey());
                jsonGen.writeObject(prop.getValue());
            }
        }
    }
}

