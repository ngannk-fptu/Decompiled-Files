/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.securityToken;

import java.security.Key;
import java.security.PublicKey;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.impl.securityToken.AbstractSecurityToken;
import org.apache.xml.security.stax.securityEvent.AlgorithmSuiteSecurityEvent;
import org.apache.xml.security.stax.securityToken.InboundSecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

public abstract class AbstractInboundSecurityToken
extends AbstractSecurityToken
implements InboundSecurityToken {
    private boolean invoked = false;
    private InboundSecurityContext inboundSecurityContext;
    private List<QName> elementPath;
    private XMLSecEvent xmlSecEvent;
    private SecurityTokenConstants.KeyIdentifier keyIdentifier;
    private final List<InboundSecurityToken> wrappedTokens = new ArrayList<InboundSecurityToken>();
    private InboundSecurityToken keyWrappingToken;
    private boolean includedInMessage = false;

    public AbstractInboundSecurityToken(InboundSecurityContext inboundSecurityContext, String id, SecurityTokenConstants.KeyIdentifier keyIdentifier, boolean includedInMessage) {
        super(id);
        if (keyIdentifier == null) {
            throw new IllegalArgumentException("No keyIdentifier specified");
        }
        this.inboundSecurityContext = inboundSecurityContext;
        this.keyIdentifier = keyIdentifier;
        this.includedInMessage = includedInMessage;
    }

    private void testAndSetInvocation() throws XMLSecurityException {
        if (this.invoked) {
            throw new XMLSecurityException("stax.recursiveKeyReference");
        }
        this.invoked = true;
    }

    private void unsetInvocation() {
        this.invoked = false;
    }

    @Override
    public SecurityTokenConstants.KeyIdentifier getKeyIdentifier() {
        return this.keyIdentifier;
    }

    @Override
    public List<QName> getElementPath() {
        return this.elementPath;
    }

    public void setElementPath(List<QName> elementPath) {
        this.elementPath = Collections.unmodifiableList(elementPath);
    }

    @Override
    public XMLSecEvent getXMLSecEvent() {
        return this.xmlSecEvent;
    }

    public void setXMLSecEvent(XMLSecEvent xmlSecEvent) {
        this.xmlSecEvent = xmlSecEvent;
    }

    protected Key getKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException {
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
    public final Key getSecretKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException {
        if (correlationID == null) {
            throw new IllegalArgumentException("correlationID must not be null");
        }
        this.testAndSetInvocation();
        Key key = this.getKey(algorithmURI, algorithmUsage, correlationID);
        if (key != null && this.inboundSecurityContext != null) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithmURI);
            algorithmSuiteSecurityEvent.setAlgorithmUsage(algorithmUsage);
            algorithmSuiteSecurityEvent.setCorrelationID(correlationID);
            if (SecurityTokenConstants.DerivedKeyToken.equals(this.getTokenType())) {
                algorithmSuiteSecurityEvent.setDerivedKey(true);
            }
            if (key instanceof RSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((RSAKey)((Object)key)).getModulus().bitLength());
            } else if (key instanceof DSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((DSAKey)((Object)key)).getParams().getP().bitLength());
            } else if (key instanceof ECKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((ECKey)((Object)key)).getParams().getOrder().bitLength());
            } else if (key instanceof SecretKey) {
                algorithmSuiteSecurityEvent.setKeyLength(key.getEncoded().length * 8);
            } else {
                throw new XMLSecurityException("java.security.UnknownKeyType", new Object[]{key.getClass().getName()});
            }
            this.inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        }
        this.unsetInvocation();
        return key;
    }

    protected PublicKey getPubKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException {
        return this.getPublicKey();
    }

    @Override
    public final PublicKey getPublicKey(String algorithmURI, XMLSecurityConstants.AlgorithmUsage algorithmUsage, String correlationID) throws XMLSecurityException {
        if (correlationID == null) {
            throw new IllegalArgumentException("correlationID must not be null");
        }
        this.testAndSetInvocation();
        PublicKey publicKey = this.getPubKey(algorithmURI, algorithmUsage, correlationID);
        if (publicKey != null && this.inboundSecurityContext != null) {
            AlgorithmSuiteSecurityEvent algorithmSuiteSecurityEvent = new AlgorithmSuiteSecurityEvent();
            algorithmSuiteSecurityEvent.setAlgorithmURI(algorithmURI);
            algorithmSuiteSecurityEvent.setAlgorithmUsage(algorithmUsage);
            algorithmSuiteSecurityEvent.setCorrelationID(correlationID);
            if (publicKey instanceof RSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((RSAKey)((Object)publicKey)).getModulus().bitLength());
            } else if (publicKey instanceof DSAKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((DSAKey)((Object)publicKey)).getParams().getP().bitLength());
            } else if (publicKey instanceof ECKey) {
                algorithmSuiteSecurityEvent.setKeyLength(((ECKey)((Object)publicKey)).getParams().getOrder().bitLength());
            } else {
                throw new XMLSecurityException("java.security.UnknownKeyType", new Object[]{publicKey.getClass().getName()});
            }
            this.inboundSecurityContext.registerSecurityEvent(algorithmSuiteSecurityEvent);
        }
        this.unsetInvocation();
        return publicKey;
    }

    @Override
    public void verify() throws XMLSecurityException {
    }

    public List<InboundSecurityToken> getWrappedTokens() {
        return Collections.unmodifiableList(this.wrappedTokens);
    }

    @Override
    public void addWrappedToken(InboundSecurityToken inboundSecurityToken) {
        this.wrappedTokens.add(inboundSecurityToken);
    }

    @Override
    public void addTokenUsage(SecurityTokenConstants.TokenUsage tokenUsage) throws XMLSecurityException {
        this.testAndSetInvocation();
        if (!this.tokenUsages.contains(tokenUsage)) {
            this.tokenUsages.add(tokenUsage);
        }
        if (this.getKeyWrappingToken() != null) {
            this.getKeyWrappingToken().addTokenUsage(tokenUsage);
        }
        this.unsetInvocation();
    }

    @Override
    public InboundSecurityToken getKeyWrappingToken() throws XMLSecurityException {
        return this.keyWrappingToken;
    }

    public void setKeyWrappingToken(InboundSecurityToken keyWrappingToken) {
        this.keyWrappingToken = keyWrappingToken;
    }

    @Override
    public boolean isIncludedInMessage() {
        return this.includedInMessage;
    }
}

