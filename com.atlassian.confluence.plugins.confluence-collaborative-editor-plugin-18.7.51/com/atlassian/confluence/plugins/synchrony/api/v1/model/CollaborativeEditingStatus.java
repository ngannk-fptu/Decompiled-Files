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

public class CollaborativeEditingStatus {
    @JsonProperty
    private final boolean sharedDraftsEnabled;
    @JsonProperty
    private final boolean sharedDraftsExplicitlyDisabled;
    @JsonProperty
    private final String configuredUrl;
    @JsonProperty
    private final String applicationId;
    @JsonProperty
    private final boolean registrationComplete;
    @JsonProperty
    private final String configuredPublicKey;

    @JsonCreator
    public CollaborativeEditingStatus(@JsonProperty(value="sharedDraftsEnabled") boolean sharedDraftsEnabled, @JsonProperty(value="sharedDraftsExplicitlyDisabled") boolean sharedDraftsExplicitlyDisabled, @JsonProperty(value="configuredUrl") String configuredUrl, @JsonProperty(value="applicationId") String applicationId, @JsonProperty(value="registrationComplete") boolean registrationComplete, @JsonProperty(value="configuredPublicKey") String configuredPublicKey) {
        this.sharedDraftsEnabled = sharedDraftsEnabled;
        this.sharedDraftsExplicitlyDisabled = sharedDraftsExplicitlyDisabled;
        this.configuredUrl = configuredUrl;
        this.applicationId = applicationId;
        this.registrationComplete = registrationComplete;
        this.configuredPublicKey = configuredPublicKey;
    }
}

