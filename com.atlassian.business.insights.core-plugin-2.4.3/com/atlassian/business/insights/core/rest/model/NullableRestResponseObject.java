/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.business.insights.core.rest.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public interface NullableRestResponseObject {
    @JsonIgnore
    public boolean isEmpty();
}

