/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import java.util.List;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.securityEvent.AbstractSecuredElementSecurityEvent;
import org.apache.xml.security.stax.securityEvent.SecurityEventConstants;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;

public class ContentEncryptedElementSecurityEvent
extends AbstractSecuredElementSecurityEvent {
    public ContentEncryptedElementSecurityEvent(InboundSecurityToken inboundSecurityToken, boolean encrypted, List<XMLSecurityConstants.ContentType> protectionOrder) {
        super(SecurityEventConstants.ContentEncrypted, inboundSecurityToken, protectionOrder, false, encrypted);
    }
}

