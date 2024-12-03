/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.google.common.base.MoreObjects
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.plugin.rest.entity.admin.DirectoryMappingAuthenticationEntity;
import com.atlassian.plugins.rest.common.expand.Expandable;
import com.google.common.base.MoreObjects;
import java.util.List;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class DirectoryMappingEntity {
    @JsonProperty(value="id")
    private Long id;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="authentication")
    @Expandable(value="authentication")
    private DirectoryMappingAuthenticationEntity authentication;
    @JsonProperty(value="defaultGroups")
    @Expandable(value="defaultGroups")
    private List<String> defaultGroups;

    protected DirectoryMappingEntity() {
    }

    public DirectoryMappingEntity(Long id, String name, DirectoryMappingAuthenticationEntity authentication, List<String> defaultGroups) {
        this.id = id;
        this.name = name;
        this.authentication = authentication;
        this.defaultGroups = defaultGroups;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public DirectoryMappingAuthenticationEntity getAuthentication() {
        return this.authentication;
    }

    public List<String> getDefaultGroups() {
        return this.defaultGroups;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("name", (Object)this.name).add("authentication", (Object)this.authentication).add("defaultGroups", this.defaultGroups).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DirectoryMappingEntity that = (DirectoryMappingEntity)o;
        return Objects.equals(this.id, that.id) && Objects.equals(this.name, that.name) && Objects.equals(this.authentication, that.authentication) && Objects.equals(this.defaultGroups, that.defaultGroups);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.name, this.authentication, this.defaultGroups);
    }
}

