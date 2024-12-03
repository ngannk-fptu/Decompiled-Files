/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.plugin.rest.entity.admin.user;

import com.atlassian.crowd.plugin.rest.entity.admin.directory.DirectoryEntityId;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserIdentifiersEntityList {
    @JsonProperty(value="ids")
    private final List<DirectoryEntityId> userIdentifiers;

    public UserIdentifiersEntityList() {
        this.userIdentifiers = null;
    }

    @JsonCreator
    public UserIdentifiersEntityList(@JsonProperty(value="ids") List<DirectoryEntityId> userIdentifiers) {
        this.userIdentifiers = userIdentifiers;
    }

    public List<DirectoryEntityId> getUserIdentifiers() {
        return this.userIdentifiers;
    }
}

