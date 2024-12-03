/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.annotations.PublicApi;
import com.google.common.base.Preconditions;
import java.io.Serializable;

@PublicApi
public class StagedEmailThreadKey
implements Serializable {
    private String token;

    private StagedEmailThreadKey() {
    }

    public StagedEmailThreadKey(String token) {
        this.token = (String)Preconditions.checkNotNull((Object)token);
    }

    public String getToken() {
        return this.token;
    }

    public String toString() {
        return this.token;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        StagedEmailThreadKey that = (StagedEmailThreadKey)o;
        return this.token.equals(that.token);
    }

    public int hashCode() {
        return this.token.hashCode();
    }
}

