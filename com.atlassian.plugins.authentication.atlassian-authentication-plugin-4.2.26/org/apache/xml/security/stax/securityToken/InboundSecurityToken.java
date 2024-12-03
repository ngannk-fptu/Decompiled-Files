/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityToken;

import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.securityToken.SecurityToken;

public interface InboundSecurityToken
extends SecurityToken {
    public Key getSecretKey(String var1, XMLSecurityConstants.AlgorithmUsage var2, String var3) throws XMLSecurityException;

    public PublicKey getPublicKey(String var1, XMLSecurityConstants.AlgorithmUsage var2, String var3) throws XMLSecurityException;

    public void addWrappedToken(InboundSecurityToken var1);

    public void verify() throws XMLSecurityException;

    public List<QName> getElementPath();

    public XMLSecEvent getXMLSecEvent();

    public boolean isIncludedInMessage();
}

