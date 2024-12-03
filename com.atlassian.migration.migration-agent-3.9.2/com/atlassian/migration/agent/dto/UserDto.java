/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class UserDto {
    @JsonProperty
    private final String email;
    @JsonProperty
    private final String fullname;

    public UserDto(String email, String fullname) {
        this.email = email;
        this.fullname = fullname;
    }
}

