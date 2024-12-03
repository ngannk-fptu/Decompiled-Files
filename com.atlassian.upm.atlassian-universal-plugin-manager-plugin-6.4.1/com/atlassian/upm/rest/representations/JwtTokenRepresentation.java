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

public class JwtTokenRepresentation {
    @JsonProperty
    private final String token;

    @JsonCreator
    public JwtTokenRepresentation(@JsonProperty(value="token") String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}

