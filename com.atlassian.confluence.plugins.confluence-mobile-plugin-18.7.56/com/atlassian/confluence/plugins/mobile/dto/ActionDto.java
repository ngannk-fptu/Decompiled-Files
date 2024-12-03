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
import com.atlassian.confluence.plugins.mobile.dto.ActionContentDto;
import com.atlassian.confluence.plugins.mobile.dto.metadata.AbstractActionMetadataDto;
import com.atlassian.confluence.plugins.mobile.notification.NotificationCategory;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ActionDto {
    @JsonProperty
    private NotificationCategory category;
    @JsonProperty
    private Person by;
    @JsonProperty
    private long when;
    @JsonProperty
    private ActionContentDto content;
    @JsonProperty
    private AbstractActionMetadataDto metadata;

    public long getWhen() {
        return this.when;
    }

    public NotificationCategory getCategory() {
        return this.category;
    }

    public Person getBy() {
        return this.by;
    }

    public ActionContentDto getContent() {
        return this.content;
    }

    public AbstractActionMetadataDto getMetadata() {
        return this.metadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    @JsonCreator
    private ActionDto() {
        this(ActionDto.builder());
    }

    private ActionDto(Builder builder) {
        this.by = builder.by;
        this.when = builder.when;
        this.content = builder.content;
        this.metadata = builder.metadata;
        this.category = builder.category;
    }

    public static final class Builder {
        private Person by;
        private long when;
        private NotificationCategory category;
        private AbstractActionMetadataDto metadata;
        private ActionContentDto content;

        public ActionDto build() {
            return new ActionDto(this);
        }

        public Builder when(long when) {
            this.when = when;
            return this;
        }

        public Builder by(Person by) {
            this.by = by;
            return this;
        }

        public Builder category(NotificationCategory category) {
            this.category = category;
            return this;
        }

        public Builder content(ActionContentDto content) {
            this.content = content;
            return this;
        }

        public Builder metadata(AbstractActionMetadataDto metadata) {
            this.metadata = metadata;
            return this;
        }
    }
}

