/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityEvent.TokenSecurityEvent;
import org.apache.xml.security.stax.securityToken.SecurityToken;

public class EncryptedKeyTokenSecurityEvent
extends TokenSecurityEvent<SecurityToken> {
    public EncryptedKeyTokenSecurityEvent() {
        super(SecurityEventConstants.EncryptedKeyToken);
    }
}

