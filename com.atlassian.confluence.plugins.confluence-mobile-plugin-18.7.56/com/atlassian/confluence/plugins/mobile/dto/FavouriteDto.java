/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto;

import com.atlassian.confluence.api.model.people.Person;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Deprecated
public class FavouriteDto {
    @JsonProperty
    private long id;
    @JsonProperty
    private String title;
    @JsonProperty
    private Person author;
    @JsonProperty
    private String timeToRead;
    @JsonProperty
    private String favouritedDate;

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private FavouriteDto() {
        this(FavouriteDto.builder());
    }

    private FavouriteDto(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.author = builder.author;
        this.timeToRead = builder.timeToRead;
        this.favouritedDate = builder.favouritedDate;
    }

    public long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Person getAuthor() {
        return this.author;
    }

    public String getTimeToRead() {
        return this.timeToRead;
    }

    public String getFavouritedDate() {
        return this.favouritedDate;
    }

    public static final class Builder {
        private long id;
        private String title;
        private Person author;
        private String timeToRead;
        private String favouritedDate;

        public FavouriteDto build() {
            return new FavouriteDto(this);
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

        public Builder timeToRead(String timeToRead) {
            this.timeToRead = timeToRead;
            return this;
        }

        public Builder favouritedDate(String favouritedDate) {
            this.favouritedDate = favouritedDate;
            return this;
        }
    }
}

