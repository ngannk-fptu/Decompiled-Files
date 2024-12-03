/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.group;

import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryEntityId;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class GroupIdentifiersEntityList {
    @JsonProperty(value="ids")
    private final List<DirectoryEntityId> groupIdentifiers;

    public GroupIdentifiersEntityList() {
        this.groupIdentifiers = null;
    }

    public GroupIdentifiersEntityList(List<DirectoryEntityId> groupIdentifiers) {
        this.groupIdentifiers = groupIdentifiers;
    }

    public List<DirectoryEntityId> getGroupIdentifiers() {
        return this.groupIdentifiers;
    }
}

