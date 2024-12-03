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

public class AuditExcludedActionsJson {
    private final List<String> actions;

    @JsonCreator
    public AuditExcludedActionsJson(@JsonProperty(value="actions") List<String> actions) {
        this.actions = actions;
    }

    @JsonProperty(value="actions")
    public List<String> getActions() {
        return this.actions;
    }
}

