/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.ContentDto;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.ContentMetadataDto;
import java.util.Date;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class AbstractPageDto
extends ContentDto {
    @JsonProperty
    private Date lastModifiedDate;
    @JsonProperty
    private String timeToRead;
    @JsonProperty
    private SpaceDto space;
    @JsonProperty
    private String body;

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    public String getTimeToRead() {
        return this.timeToRead;
    }

    public SpaceDto getSpace() {
        return this.space;
    }

    public Date getLastModifiedDate() {
        return this.lastModifiedDate != null ? new Date(this.lastModifiedDate.getTime()) : null;
    }

    public String getBody() {
        return this.body;
    }

    @JsonCreator
    private AbstractPageDto() {
        this(AbstractPageDto.builder());
    }

    private AbstractPageDto(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.body = builder.body;
        this.contentType = builder.contentType;
        this.author = builder.author;
        this.lastModifiedDate = builder.lastModifiedDate;
        this.timeToRead = builder.timeToRead;
        this.space = builder.space;
        this.metadata = builder.metadata;
        this.type = builder.contentType;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public static final class Builder {
        private long id;
        private String title;
        private String body;
        private String contentType;
        private Person author;
        private Date lastModifiedDate;
        private String timeToRead;
        private SpaceDto space;
        private ContentMetadataDto metadata;

        public AbstractPageDto build() {
            return new AbstractPageDto(this);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder author(Person author) {
            this.author = author;
            return this;
        }

        public Builder timeToRead(String timeToRead) {
            this.timeToRead = timeToRead;
            return this;
        }

        public Builder lastModifiedDate(@Nonnull Date lastModifiedDate) {
            this.lastModifiedDate = new Date(lastModifiedDate.getTime());
            return this;
        }

        public Builder space(SpaceDto space) {
            this.space = space;
            return this;
        }

        public Builder metadata(ContentMetadataDto metadata) {
            this.metadata = metadata;
            return this;
        }
    }
}

