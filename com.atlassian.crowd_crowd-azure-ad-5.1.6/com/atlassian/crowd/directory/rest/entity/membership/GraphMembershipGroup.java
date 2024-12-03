/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.membership;

import com.atlassian.crowd.directory.rest.entity.membership.DirectoryObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GraphMembershipGroup
extends DirectoryObject {
    @JsonProperty(value="description")
    private final String description;

    private GraphMembershipGroup() {
        super(null, null);
        this.description = null;
    }

    public GraphMembershipGroup(String id, String displayName, String description) {
        super(displayName, id);
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getId() {
        return this.id;
    }
}

