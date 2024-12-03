/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonValue
 */
package com.atlassian.audit.rest.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonValue;

@JsonIgnoreProperties(ignoreUnknown=true)
public interface RestPageCursor {
    @JsonValue
    public String getCursor();
}

