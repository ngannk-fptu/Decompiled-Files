/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import java.util.List;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityEvent.AbstractSecuredElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;

public class EncryptedElementSecurityEvent
extends AbstractSecuredElementSecurityEvent {
    public EncryptedElementSecurityEvent(InboundSecurityToken inboundSecurityToken, boolean encrypted, List<XMLSecurityConstants.ContentType> protectionOrder) {
        super(SecurityEventConstants.EncryptedElement, inboundSecurityToken, protectionOrder, false, encrypted);
    }
}

