/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class DirectoryMappingAuthenticationEntity {
    @JsonProperty(value="allowAll")
    private Boolean allowAll;
    @JsonProperty(value="allowGroups")
    private List<String> allowGroups;

    protected DirectoryMappingAuthenticationEntity() {
    }

    public DirectoryMappingAuthenticationEntity(Boolean allowAll, Collection<String> allowGroups) {
        this.allowAll = allowAll;
        this.allowGroups = null == allowGroups ? null : ImmutableList.copyOf(allowGroups);
    }

    public Boolean getAllowAll() {
        return this.allowAll;
    }

    public List<String> getAllowGroups() {
        return this.allowGroups;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("allowAll", (Object)this.allowAll).add("allowGroups", this.allowGroups).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryMappingAuthenticationEntity that = (DirectoryMappingAuthenticationEntity)o;
        return Objects.equals(this.allowAll, that.allowAll) && Objects.equals(this.allowGroups, that.allowGroups);
    }

    public int hashCode() {
        return Objects.hash(this.allowAll, this.allowGroups);
    }
}

