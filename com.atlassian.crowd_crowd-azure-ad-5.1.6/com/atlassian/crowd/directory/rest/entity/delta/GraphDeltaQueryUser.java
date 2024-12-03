/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.delta.GraphDeletableObject;
import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryRemoved;
import com.atlassian.crowd.directory.rest.entity.user.GraphUser;
import org.codehaus.jackson.annotate.JsonProperty;

public class GraphDeltaQueryUser
extends GraphUser
implements GraphDeletableObject {
    @JsonProperty(value="@removed")
    private final GraphDeltaQueryRemoved removed;

    private GraphDeltaQueryUser() {
        this.removed = null;
    }

    public GraphDeltaQueryUser(String userPrincipalName) {
        super(userPrincipalName);
        this.removed = null;
    }

    public GraphDeltaQueryUser(String id, String displayName, String givenName, String mail, String surname, String userPrincipalName, Boolean accountEnabled) {
        super(id, displayName, givenName, mail, surname, userPrincipalName, accountEnabled);
        this.removed = null;
    }

    public GraphDeltaQueryUser(String id, String displayName, String givenName, String mail, String surname, String userPrincipalName, Boolean accountEnabled, GraphDeltaQueryRemoved removed) {
        super(id, displayName, givenName, mail, surname, userPrincipalName, accountEnabled);
        this.removed = removed;
    }

    @Override
    public GraphDeltaQueryRemoved getRemoved() {
        return this.removed;
    }
}

