/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.AccessBasedSynchronizationFilterType
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.model.application.AccessBasedSynchronizationFilterType;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class AccessBasedSynchronizationFilterTypeEntity {
    @JsonProperty(value="filterType")
    private AccessBasedSynchronizationFilterType filterType;

    protected AccessBasedSynchronizationFilterTypeEntity() {
    }

    public AccessBasedSynchronizationFilterTypeEntity(AccessBasedSynchronizationFilterType filterType) {
        this.filterType = (AccessBasedSynchronizationFilterType)Preconditions.checkNotNull((Object)filterType);
    }

    public AccessBasedSynchronizationFilterType getFilterType() {
        return this.filterType;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("filterType", (Object)this.filterType).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AccessBasedSynchronizationFilterTypeEntity that = (AccessBasedSynchronizationFilterTypeEntity)o;
        return Objects.equals(this.filterType, that.filterType);
    }

    public int hashCode() {
        return Objects.hash(this.filterType);
    }
}

