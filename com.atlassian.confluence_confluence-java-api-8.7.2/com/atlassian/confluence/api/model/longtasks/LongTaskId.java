/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.api.model.longtasks;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import java.util.Objects;
import java.util.UUID;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonValue;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
public class LongTaskId {
    private final UUID uuid;

    private LongTaskId(UUID uuid) {
        this.uuid = uuid;
    }

    @JsonCreator
    public static LongTaskId deserialise(String id) {
        if (id == null || id.isEmpty()) {
            throw new BadRequestException("LongTaskId string must not be null or empty: " + id);
        }
        return new LongTaskId(UUID.fromString(id));
    }

    @JsonValue
    public String serialise() {
        return String.valueOf(this.uuid);
    }

    public boolean equals(Object other) {
        if (other instanceof LongTaskId) {
            return Objects.equals(this.uuid, ((LongTaskId)other).uuid);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.uuid);
    }
}

