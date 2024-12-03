/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 */
package com.atlassian.confluence.plugins.remotepageview.rest.response;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect
public class TokenResponse {
    private String token;

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

