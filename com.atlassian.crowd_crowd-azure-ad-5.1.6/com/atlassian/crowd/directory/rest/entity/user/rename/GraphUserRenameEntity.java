/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.crowd.directory.rest.entity.user.rename;

import org.codehaus.jackson.annotate.JsonProperty;

public class GraphUserRenameEntity {
    @JsonProperty(value="userPrincipalName")
    private final String userPrincipalName;

    private GraphUserRenameEntity() {
        this.userPrincipalName = null;
    }

    public GraphUserRenameEntity(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getUserPrincipalName() {
        return this.userPrincipalName;
    }
}

