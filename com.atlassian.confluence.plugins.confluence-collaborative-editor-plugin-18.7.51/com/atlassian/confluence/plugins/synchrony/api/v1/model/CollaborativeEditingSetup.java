/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.synchrony.api.v1.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CollaborativeEditingSetup {
    @JsonProperty
    private final boolean registrationComplete;
    @JsonProperty
    private final boolean publicKeyRetrieved;

    @JsonCreator
    public CollaborativeEditingSetup(@JsonProperty(value="registrationComplete") boolean registrationComplete, @JsonProperty(value="publicKeyRetrieved") boolean publicKeyRetrieved) {
        this.registrationComplete = registrationComplete;
        this.publicKeyRetrieved = publicKeyRetrieved;
    }
}

