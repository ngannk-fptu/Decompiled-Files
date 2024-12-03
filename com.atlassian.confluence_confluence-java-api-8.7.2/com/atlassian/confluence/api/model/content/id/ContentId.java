/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.graphql.annotations.GraphQLIDType
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model.content.id;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.AttachmentContentId;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.graphql.annotations.GraphQLIDType;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class ContentId
implements Comparable<ContentId> {
    public static final ContentId UNSET = new ContentId(0L);
    private Long id;

    ContentId(long id) {
        this.id = id;
    }

    @JsonCreator
    public static ContentId deserialise(@JsonProperty(value="id") String id) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("ContentId string must not be null or empty: " + id);
        }
        try {
            return new ContentId(Long.parseLong(id));
        }
        catch (NumberFormatException numberFormatException) {
            if (AttachmentContentId.handles(id)) {
                return new AttachmentContentId(id);
            }
            throw new BadRequestException("Can't parse as a ContentId: " + id);
        }
    }

    @Deprecated
    public static ContentId of(ContentType type, long id) {
        return new ContentId(id);
    }

    public static ContentId of(long id) {
        return new ContentId(id);
    }

    @JsonValue
    @GraphQLIDType
    public String serialise() {
        return String.valueOf(this.id);
    }

    public long asLong() {
        return this.id;
    }

    public boolean isSet() {
        return this.id > 0L;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContentId contentId = (ContentId)o;
        return Objects.equals(this.id, contentId.id);
    }

    public String toString() {
        return "ContentId{id=" + this.id + '}';
    }

    public static ContentId valueOf(String id) throws BadRequestException {
        return ContentId.deserialise(id);
    }

    @Override
    public int compareTo(ContentId other) {
        if (this.id.equals(other.id)) {
            return 0;
        }
        if (this.id < other.id) {
            return -1;
        }
        return 1;
    }
}

