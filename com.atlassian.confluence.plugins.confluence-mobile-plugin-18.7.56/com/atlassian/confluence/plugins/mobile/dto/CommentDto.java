/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.people.Person
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CommentDto
extends ContentDto {
    @JsonProperty
    private String location;
    @JsonProperty
    private Map<String, Object> properties;
    @JsonProperty
    private Date createdDate;
    @JsonProperty
    private ContentDto container;
    @JsonProperty
    private CommentDto parent;
    @JsonProperty
    private List<CommentDto> children;
    @JsonProperty
    private String body;

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private CommentDto() {
        this(CommentDto.builder());
    }

    public CommentDto getParent() {
        return this.parent;
    }

    public String getLocation() {
        return this.location;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public ContentDto getContainer() {
        return this.container;
    }

    public String getBody() {
        return this.body;
    }

    private CommentDto(Builder builder) {
        this.id = builder.id;
        this.body = builder.body;
        this.author = builder.author;
        this.createdDate = builder.createdDate;
        this.location = builder.location;
        this.properties = builder.properties;
        this.container = builder.container;
        this.parent = builder.parent;
        this.contentType = ContentType.COMMENT.getType();
        this.type = ContentType.COMMENT.getType();
        this.metadata = builder.metadata;
        this.children = builder.children;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public void setChildren(List<CommentDto> children) {
        this.children = children;
    }

    public void addChildren(CommentDto commentDto) {
        if (this.children == null) {
            this.children = new ArrayList<CommentDto>();
        }
        this.children.add(commentDto);
    }

    public Date getCreatedDate() {
        return this.createdDate != null ? new Date(this.createdDate.getTime()) : null;
    }

    public List<CommentDto> getChildren() {
        return this.children;
    }

    public static final class Builder {
        private long id;
        private String body;
        private Person author;
        private Date createdDate;
        private String location;
        private Map<String, Object> properties;
        private ContentDto container;
        private CommentDto parent;
        private ContentMetadataDto metadata;
        private List<CommentDto> children;

        public CommentDto build() {
            return new CommentDto(this);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder author(Person author) {
            this.author = author;
            return this;
        }

        public Builder createdDate(@Nonnull Date createdDate) {
            this.createdDate = new Date(createdDate.getTime());
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder properties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public Builder container(ContentDto container) {
            this.container = container;
            return this;
        }

        public Builder parent(CommentDto parent) {
            this.parent = parent;
            return this;
        }

        public Builder metadata(ContentMetadataDto metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder children(List<CommentDto> children) {
            this.children = children;
            return this;
        }
    }
}

