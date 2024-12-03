/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityEvent;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityEvent.SecurityEvent;

public interface SecurityEventListener {
    public void registerSecurityEvent(SecurityEvent var1) throws XMLSecurityException;
}

