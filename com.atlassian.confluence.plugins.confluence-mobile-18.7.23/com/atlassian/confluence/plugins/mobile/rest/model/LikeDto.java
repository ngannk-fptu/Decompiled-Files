/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.rest.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class LikeDto {
    @JsonProperty
    private String username;

    private LikeDto() {
    }

    public LikeDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}

