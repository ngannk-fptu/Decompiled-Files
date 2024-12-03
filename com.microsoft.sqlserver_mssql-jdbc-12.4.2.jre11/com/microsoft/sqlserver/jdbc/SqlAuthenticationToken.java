/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;
import java.util.Date;

public class SqlAuthenticationToken
implements Serializable {
    private static final long serialVersionUID = -1343105491285383937L;
    private final Date expiresOn;
    private final String accessToken;

    public SqlAuthenticationToken(String accessToken, long expiresOn) {
        this.accessToken = accessToken;
        this.expiresOn = new Date(expiresOn);
    }

    public SqlAuthenticationToken(String accessToken, Date expiresOn) {
        this.accessToken = accessToken;
        this.expiresOn = expiresOn;
    }

    public Date getExpiresOn() {
        return this.expiresOn;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String toString() {
        return "accessToken hashCode: " + this.accessToken.hashCode() + " expiresOn: " + this.expiresOn.toInstant().toString();
    }
}

