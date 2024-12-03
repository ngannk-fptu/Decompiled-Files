/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin;

import com.atlassian.crowd.plugin.rest.entity.admin.DirectoryMappingEntity;
import java.util.List;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonAutoDetect(getterVisibility=JsonAutoDetect.Visibility.NONE, isGetterVisibility=JsonAutoDetect.Visibility.NONE)
public class DirectoryMappingsEntity {
    @JsonProperty(value="mappings")
    private List<DirectoryMappingEntity> mappings;

    public DirectoryMappingsEntity() {
    }

    public DirectoryMappingsEntity(List<DirectoryMappingEntity> mappings) {
        this.mappings = mappings;
    }
}

