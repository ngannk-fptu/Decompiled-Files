/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.filter.OptOutEntity
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.service.api;

import com.atlassian.business.insights.api.filter.OptOutEntity;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonProperty;

public class OptedOutEntity {
    private final String type;
    private final String key;

    public OptedOutEntity(@Nonnull @JsonProperty(value="type") String type, @Nonnull @JsonProperty(value="key") String key) {
        this.type = Objects.requireNonNull(type);
        this.key = Objects.requireNonNull(key);
    }

    public String getType() {
        return this.type;
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        return "OptedOutEntity{type='" + this.type + '\'' + ", key='" + this.key + '\'' + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OptedOutEntity)) {
            return false;
        }
        OptedOutEntity that = (OptedOutEntity)o;
        return this.type.equals(that.type) && this.key.equals(that.key);
    }

    public int hashCode() {
        return Objects.hash(this.type, this.key);
    }

    public static OptedOutEntity fromOptOutResource(OptOutEntity optOutEntity) {
        return new OptedOutEntity(optOutEntity.getIdentifier().getType().toString(), optOutEntity.getKey() == null ? optOutEntity.getIdentifier().getIdentifier() : optOutEntity.getKey());
    }
}

