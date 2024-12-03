/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.crowd.model.authentication;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AuthenticatedToken
implements Serializable {
    private String name;
    private String token;

    public AuthenticatedToken() {
    }

    public AuthenticatedToken(String name, String token) {
        this.name = name;
        this.token = token;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuthenticatedToken that = (AuthenticatedToken)o;
        if (this.getName() != null ? !this.getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        return !(this.getToken() != null ? !this.getToken().equals(that.getToken()) : that.getToken() != null);
    }

    public int hashCode() {
        int result = this.getName() != null ? this.getName().hashCode() : 0;
        result = 31 * result + (this.getToken() != null ? this.getToken().hashCode() : 0);
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("name", (Object)this.getName()).append("token", (Object)this.getToken()).toString();
    }
}

