/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model.content.id;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class JsonContentPropertyId {
    public static final ContentId UNSET = new ContentId(0L);
    private Long id;

    private JsonContentPropertyId(long id) {
        this.id = id;
    }

    @JsonCreator
    public static JsonContentPropertyId deserialise(@JsonProperty(value="id") String id) throws BadRequestException {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("ContentId string must not be null or empty: " + id);
        }
        try {
            return new JsonContentPropertyId(Long.parseLong(id));
        }
        catch (NumberFormatException numberFormatException) {
            throw new BadRequestException("Can't parse as a JsonContentPropertyId: " + id);
        }
    }

    public static JsonContentPropertyId of(long id) {
        return new JsonContentPropertyId(id);
    }

    @JsonValue
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
        JsonContentPropertyId that = (JsonContentPropertyId)o;
        return Objects.equals(this.id, that.id);
    }

    public String toString() {
        return "JsonContentPropertyId{id=" + this.id + '}';
    }

    @Deprecated
    public static JsonContentPropertyId valueOf(String id) throws BadRequestException {
        return JsonContentPropertyId.deserialise(id);
    }
}

