/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryEntityId;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class GroupSearchResultEntity {
    @JsonProperty(value="id")
    private DirectoryEntityId id;
    @JsonProperty(value="active")
    private Boolean active;
    @JsonProperty(value="name")
    private String name;
    @JsonProperty(value="description")
    private String description;

    protected GroupSearchResultEntity() {
    }

    public GroupSearchResultEntity(String name, String description, long directoryId, boolean active) {
        this.name = name;
        this.description = description;
        this.id = new DirectoryEntityId(directoryId, name);
        this.active = active;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        GroupSearchResultEntity that = (GroupSearchResultEntity)o;
        return this.id.equals(that.id) && this.active.equals(that.active) && this.name.equals(that.name) && this.description.equals(that.description);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.active, this.name, this.description);
    }
}

