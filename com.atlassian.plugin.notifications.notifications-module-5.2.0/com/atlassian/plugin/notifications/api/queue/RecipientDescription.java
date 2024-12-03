/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.plugin.notifications.api.queue;

import org.codehaus.jackson.annotate.JsonProperty;

public class RecipientDescription {
    @JsonProperty
    private final boolean isIndividual;
    @JsonProperty
    private final String description;

    public RecipientDescription(boolean isIndividual, String description) {
        this.isIndividual = isIndividual;
        this.description = description;
    }

    public boolean isIndividual() {
        return this.isIndividual;
    }

    public String getDescription() {
        return this.description;
    }
}

