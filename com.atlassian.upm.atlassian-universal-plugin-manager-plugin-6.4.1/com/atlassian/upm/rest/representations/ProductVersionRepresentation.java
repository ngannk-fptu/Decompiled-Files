/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.rest.representations;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ProductVersionRepresentation {
    @JsonProperty
    private final boolean development;
    @JsonProperty
    private final boolean unknown;

    @JsonCreator
    public ProductVersionRepresentation(@JsonProperty(value="development") boolean development, @JsonProperty(value="unknown") boolean unknown) {
        this.development = development;
        this.unknown = unknown;
    }

    public boolean isDevelopment() {
        return this.development;
    }

    public boolean isUnknown() {
        return this.unknown;
    }
}

