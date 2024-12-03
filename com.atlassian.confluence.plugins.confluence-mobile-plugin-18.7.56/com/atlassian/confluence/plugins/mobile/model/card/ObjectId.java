/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.plugins.mobile.model.card.ObjectType;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonValue;

@JsonIgnoreProperties(ignoreUnknown=true)
public final class ObjectId {
    private Long id;
    private ObjectType type;

    private ObjectId(Long id, ObjectType type) {
        this.id = id;
        this.type = type;
    }

    public ObjectType getType() {
        return this.type;
    }

    @JsonValue
    public String serialise() {
        return String.valueOf(this.id);
    }

    public static ObjectId of(long id, ObjectType type) {
        return new ObjectId(id, type);
    }
}

