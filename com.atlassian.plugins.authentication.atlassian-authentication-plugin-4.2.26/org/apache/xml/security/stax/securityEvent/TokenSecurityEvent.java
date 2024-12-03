/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityEvent.SecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityToken.SecurityToken;

public abstract class TokenSecurityEvent<T extends SecurityToken>
extends SecurityEvent {
    private T securityToken;

    public TokenSecurityEvent(SecurityEventConstants.Event securityEventType) {
        super(securityEventType);
    }

    public T getSecurityToken() {
        return this.securityToken;
    }

    public void setSecurityToken(T securityToken) {
        this.securityToken = securityToken;
    }
}

