/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.people.Person
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AttachmentDto
extends ContentDto {
    @JsonProperty
    private ContentDto container;
    @JsonProperty
    private String fileName;
    @JsonProperty
    private String downloadPath;

    public static Builder builder() {
        return new Builder();
    }

    public ContentDto getContainer() {
        return this.container;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getDownloadPath() {
        return this.downloadPath;
    }

    @JsonCreator
    private AttachmentDto() {
        this(AttachmentDto.builder());
    }

    private AttachmentDto(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.contentType = ContentType.ATTACHMENT.getType();
        this.type = ContentType.ATTACHMENT.getType();
        this.author = builder.author;
        this.metadata = builder.metadata;
        this.container = builder.container;
        this.fileName = builder.fileName;
        this.downloadPath = builder.downloadPath;
    }

    public static final class Builder {
        private long id;
        private String title;
        private Person author;
        private ContentMetadataDto metadata;
        private ContentDto container;
        private String fileName;
        private String downloadPath;

        public AttachmentDto build() {
            return new AttachmentDto(this);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(Person author) {
            this.author = author;
            return this;
        }

        public Builder metadata(ContentMetadataDto metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder container(ContentDto container) {
            this.container = container;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder downloadPath(String downloadPath) {
            this.downloadPath = downloadPath;
            return this;
        }
    }
}

