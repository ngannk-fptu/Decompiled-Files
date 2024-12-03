/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.people.Person
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.plugins.mobile.dto.SpaceDto;
import com.atlassian.confluence.plugins.mobile.model.card.CardObject;
import com.atlassian.confluence.plugins.mobile.model.card.ObjectId;
import com.atlassian.confluence.plugins.mobile.model.card.ObjectType;
import java.util.Date;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonProperty;

public class PageCardObject
implements CardObject.Page {
    @JsonProperty
    private ObjectId id;
    @JsonProperty
    private ObjectType type;
    @JsonProperty
    private String title;
    @JsonProperty
    private Person createdBy;
    @JsonProperty
    private Date createdDate;
    @JsonProperty
    private String timeToRead;
    @JsonProperty
    private boolean saved;
    @JsonProperty
    private SpaceDto space;

    private PageCardObject(PageCardObjectBuilder builder) {
        this.id = builder.id;
        this.type = builder.id.getType();
        this.title = builder.title;
        this.createdBy = builder.createdBy;
        this.createdDate = builder.createdDate;
        this.timeToRead = builder.timeToRead;
        this.saved = builder.saved;
        this.space = builder.space;
    }

    @Override
    public Date getCreatedDate() {
        return new Date(this.createdDate.getTime());
    }

    @Override
    public String getTimeToRead() {
        return this.timeToRead;
    }

    @Override
    public Person getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public ObjectId getId() {
        return this.id;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public boolean isSaved() {
        return this.saved;
    }

    @Override
    public SpaceDto getSpace() {
        return this.space;
    }

    public ObjectType getType() {
        return this.type;
    }

    public static PageCardObjectBuilder builder() {
        return new PageCardObjectBuilder();
    }

    public static final class PageCardObjectBuilder {
        private ObjectId id;
        private String title;
        private Person createdBy;
        private Date createdDate;
        private String timeToRead;
        private boolean saved;
        private SpaceDto space;

        private PageCardObjectBuilder() {
        }

        public PageCardObject build() {
            return new PageCardObject(this);
        }

        public PageCardObjectBuilder id(ObjectId id) {
            this.id = id;
            return this;
        }

        public PageCardObjectBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PageCardObjectBuilder createBy(Person createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PageCardObjectBuilder createdDate(@Nonnull Date createdDate) {
            this.createdDate = new Date(createdDate.getTime());
            return this;
        }

        public PageCardObjectBuilder timeToRead(String timeToRead) {
            this.timeToRead = timeToRead;
            return this;
        }

        public PageCardObjectBuilder saved(boolean saved) {
            this.saved = saved;
            return this;
        }

        public PageCardObjectBuilder space(SpaceDto space) {
            this.space = space;
            return this;
        }
    }
}

