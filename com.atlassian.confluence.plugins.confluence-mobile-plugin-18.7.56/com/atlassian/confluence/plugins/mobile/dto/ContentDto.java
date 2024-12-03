/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import org.codehaus.jackson.annotate.JsonProperty;

public class ContentDto {
    @JsonProperty
    protected long id;
    @JsonProperty
    protected String contentType;
    @JsonProperty
    protected Person author;
    @JsonProperty
    protected ContentMetadataDto metadata;
    @JsonProperty
    protected String title;
    @JsonProperty
    protected String type;

    public ContentDto() {
    }

    public ContentDto(long id, String type) {
        this.id = id;
        this.type = type;
        this.contentType = type;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getAuthor() {
        return this.author;
    }

    public ContentMetadataDto getMetadata() {
        return this.metadata;
    }

    public String getTitle() {
        return this.title;
    }

    public String getType() {
        return this.type;
    }

    @Deprecated
    public String getContentType() {
        return this.contentType;
    }
}

