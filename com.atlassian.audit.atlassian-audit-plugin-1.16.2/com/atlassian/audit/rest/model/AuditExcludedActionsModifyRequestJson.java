/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.rest.model;

import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class AuditExcludedActionsModifyRequestJson {
    private final List<String> actionsToAdd;
    private final List<String> actionsToDelete;

    @JsonCreator
    public AuditExcludedActionsModifyRequestJson(@JsonProperty(value="add") List<String> actionsToAdd, @JsonProperty(value="delete") List<String> actionsToDelete) {
        this.actionsToAdd = actionsToAdd;
        this.actionsToDelete = actionsToDelete;
    }

    @JsonProperty(value="add")
    public List<String> getActionsToAdd() {
        return this.actionsToAdd;
    }

    @JsonProperty(value="delete")
    public List<String> getActionsToDelete() {
        return this.actionsToDelete;
    }
}

