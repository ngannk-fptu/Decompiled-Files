/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.audit.frontend.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditPermissionData {
    @JsonProperty(value="allowViewConfiguration")
    private boolean allowViewConfiguration;
    @JsonProperty(value="allowUpdateConfiguration")
    private boolean allowUpdateConfiguration;

    public AuditPermissionData allowUpdateConfiguration(boolean allowUpdateConfiguration) {
        this.allowUpdateConfiguration = allowUpdateConfiguration;
        return this;
    }

    public AuditPermissionData allowViewConfiguration(boolean allowViewConfiguration) {
        this.allowViewConfiguration = allowViewConfiguration;
        return this;
    }
}

