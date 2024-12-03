/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.securityToken;

import java.security.Key;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.OutputProcessor;
import org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken;
import org.apache.xml.security.stax.securityToken.OutboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.w3c.dom.Element;

public class GenericOutboundSecurityToken
extends AbstractSecurityToken
implements OutboundSecurityToken {
    private SecurityTokenConstants.TokenType tokenType;
    private OutputProcessor processor;
    private final List<OutboundSecurityToken> wrappedTokens = new ArrayList<OutboundSecurityToken>();
    private OutboundSecurityToken keyWrappingToken;
    private Element customTokenReference;

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType, Key key, X509Certificate[] x509Certificates) {
        this(id, tokenType, key);
        this.setX509Certificates(x509Certificates);
    }

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType, Key key) {
        this(id, tokenType);
        this.setSecretKey("", key);
        if (key instanceof PublicKey) {
            this.setPublicKey((PublicKey)key);
        }
    }

    public GenericOutboundSecurityToken(String id, SecurityTokenConstants.TokenType tokenType) {
        super(id);
        this.tokenType = tokenType;
    }

    @Override
    public OutputProcessor getProcessor() {
        return this.processor;
    }

    public void setProcessor(OutputProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Key getSecretKey(String algorithmURI) throws XMLSecurityException {
        if (algorithmURI == null) {
            return null;
        }
        Key key = (Key)this.keyTable.get(algorithmURI);
        if (key == null) {
            key = (Key)this.keyTable.get("");
        }
        return key;
    }

    @Override
    public OutboundSecurityToken getKeyWrappingToken() throws XMLSecurityException {
        return this.keyWrappingToken;
    }

    public void setKeyWrappingToken(OutboundSecurityToken keyWrappingToken) {
        this.keyWrappingToken = keyWrappingToken;
    }

    public List<OutboundSecurityToken> getWrappedTokens() throws XMLSecurityException {
        return Collections.unmodifiableList(this.wrappedTokens);
    }

    @Override
    public void addWrappedToken(OutboundSecurityToken securityToken) {
        this.wrappedTokens.add(securityToken);
    }

    public void setTokenType(SecurityTokenConstants.TokenType tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public SecurityTokenConstants.TokenType getTokenType() {
        return this.tokenType;
    }

    @Override
    public SecurityTokenConstants.KeyIdentifier getKeyIdentifier() {
        return null;
    }

    @Override
    public Element getCustomTokenReference() {
        return this.customTokenReference;
    }

    public void setCustomTokenReference(Element customTokenReference) {
        this.customTokenReference = customTokenReference;
    }
}

