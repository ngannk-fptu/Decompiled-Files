/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityToken;

import java.security.Key;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.securityToken.SecurityToken;
import org.w3c.dom.Element;

public interface OutboundSecurityToken
extends SecurityToken {
    public OutputProcessor getProcessor();

    public Key getSecretKey(String var1) throws XMLSecurityException;

    public void addWrappedToken(OutboundSecurityToken var1);

    public Element getCustomTokenReference();
}

