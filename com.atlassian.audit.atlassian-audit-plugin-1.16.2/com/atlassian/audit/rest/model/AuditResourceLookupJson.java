/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import com.atlassian.audit.rest.model.AuditResourceJson;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditResourceLookupJson
extends AuditResourceJson {
    @Nonnull
    private final String searchId = this.getType() + "," + this.getId();

    @JsonCreator
    public AuditResourceLookupJson(@JsonProperty(value="name") @Nonnull String name, @JsonProperty(value="type") @Nonnull String type, @JsonProperty(value="uri") @Nullable String uri, @JsonProperty(value="id") @Nullable String id) {
        super(name, type, uri, id);
    }

    @Nonnull
    @JsonProperty(value="searchId")
    public String getSearchId() {
        return this.searchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditResourceLookupJson)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        AuditResourceLookupJson that = (AuditResourceLookupJson)o;
        return this.getSearchId().equals(that.getSearchId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.getSearchId());
    }

    @Override
    public String toString() {
        return "AuditResourceLookupJson{searchId='" + this.searchId + '\'' + "} " + super.toString();
    }
}

