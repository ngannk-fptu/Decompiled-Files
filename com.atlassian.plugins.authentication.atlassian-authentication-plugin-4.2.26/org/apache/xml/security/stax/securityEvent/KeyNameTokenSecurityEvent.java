/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.SecurityToken;

public class KeyNameTokenSecurityEvent
extends TokenSecurityEvent<SecurityToken> {
    public KeyNameTokenSecurityEvent() {
        super(SecurityEventConstants.KeyNameToken);
    }
}

