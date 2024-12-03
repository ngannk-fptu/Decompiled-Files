/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.securityToken;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

public interface SecurityToken {
    public String getId();

    public boolean isAsymmetric() throws XMLSecurityException;

    public Map<String, Key> getSecretKey() throws XMLSecurityException;

    public PublicKey getPublicKey() throws XMLSecurityException;

    public X509Certificate[] getX509Certificates() throws XMLSecurityException;

    public SecurityToken getKeyWrappingToken() throws XMLSecurityException;

    public List<? extends SecurityToken> getWrappedTokens() throws XMLSecurityException;

    public SecurityTokenConstants.KeyIdentifier getKeyIdentifier();

    public SecurityTokenConstants.TokenType getTokenType();

    public List<SecurityTokenConstants.TokenUsage> getTokenUsages();

    public void addTokenUsage(SecurityTokenConstants.TokenUsage var1) throws XMLSecurityException;

    public String getSha1Identifier();
}

