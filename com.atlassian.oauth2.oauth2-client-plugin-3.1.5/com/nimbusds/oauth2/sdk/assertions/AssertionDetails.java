/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.assertions;

import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import java.util.Date;
import java.util.List;

public abstract class AssertionDetails {
    private final Issuer issuer;
    private final Subject subject;
    private final List<Audience> audience;
    private final Date iat;
    private final Date exp;
    private final Identifier id;

    public AssertionDetails(Issuer issuer, Subject subject, List<Audience> audience, Date iat, Date exp, Identifier id) {
        if (issuer == null) {
            throw new IllegalArgumentException("The issuer must not be null");
        }
        this.issuer = issuer;
        if (subject == null) {
            throw new IllegalArgumentException("The subject must not be null");
        }
        this.subject = subject;
        if (audience == null || audience.isEmpty()) {
            throw new IllegalArgumentException("The audience must not be null or empty");
        }
        this.audience = audience;
        if (exp == null) {
            throw new IllegalArgumentException("The expiration time must not be null");
        }
        this.exp = exp;
        this.iat = iat;
        this.id = id;
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public Subject getSubject() {
        return this.subject;
    }

    public List<Audience> getAudience() {
        return this.audience;
    }

    public Date getExpirationTime() {
        return this.exp;
    }

    public Date getIssueTime() {
        return this.iat;
    }

    public Identifier getID() {
        return this.id;
    }
}

