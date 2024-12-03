/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.security.core.session;

import java.io.Serializable;
import java.util.Date;
import org.springframework.util.Assert;

public class SessionInformation
implements Serializable {
    private static final long serialVersionUID = 580L;
    private Date lastRequest;
    private final Object principal;
    private final String sessionId;
    private boolean expired = false;

    public SessionInformation(Object principal, String sessionId, Date lastRequest) {
        Assert.notNull((Object)principal, (String)"Principal required");
        Assert.hasText((String)sessionId, (String)"SessionId required");
        Assert.notNull((Object)lastRequest, (String)"LastRequest required");
        this.principal = principal;
        this.sessionId = sessionId;
        this.lastRequest = lastRequest;
    }

    public void expireNow() {
        this.expired = true;
    }

    public Date getLastRequest() {
        return this.lastRequest;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public boolean isExpired() {
        return this.expired;
    }

    public void refreshLastRequest() {
        this.lastRequest = new Date();
    }
}

