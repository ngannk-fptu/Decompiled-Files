/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.securityToken;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.securityToken.SecurityToken;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

public abstract class AbstractSecurityToken
implements SecurityToken {
    private final String id;
    private PublicKey publicKey;
    private X509Certificate[] x509Certificates;
    private boolean asymmetric = false;
    private String sha1Identifier;
    protected final Map<String, Key> keyTable = new HashMap<String, Key>();
    protected final List<SecurityTokenConstants.TokenUsage> tokenUsages = new ArrayList<SecurityTokenConstants.TokenUsage>();

    public AbstractSecurityToken(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("No id specified");
        }
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    protected void setAsymmetric(boolean asymmetric) {
        this.asymmetric = asymmetric;
    }

    @Override
    public boolean isAsymmetric() throws XMLSecurityException {
        return this.asymmetric;
    }

    public void setSecretKey(String algorithmURI, Key key) {
        if (algorithmURI == null) {
            throw new IllegalArgumentException("algorithmURI must not be null");
        }
        if (key != null) {
            this.keyTable.put(algorithmURI, key);
        }
        if (key instanceof PrivateKey) {
            this.asymmetric = true;
        }
    }

    @Override
    public Map<String, Key> getSecretKey() throws XMLSecurityException {
        return Collections.unmodifiableMap(this.keyTable);
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
        this.asymmetric = true;
    }

    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (this.publicKey != null) {
            return this.publicKey;
        }
        X509Certificate[] x509Certificates = this.getX509Certificates();
        if (x509Certificates != null && x509Certificates.length > 0) {
            this.publicKey = x509Certificates[0].getPublicKey();
        }
        return this.publicKey;
    }

    public void setX509Certificates(X509Certificate[] x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    @Override
    public X509Certificate[] getX509Certificates() throws XMLSecurityException {
        return this.x509Certificates;
    }

    @Override
    public void addTokenUsage(SecurityTokenConstants.TokenUsage tokenUsage) throws XMLSecurityException {
        this.tokenUsages.add(tokenUsage);
    }

    @Override
    public List<SecurityTokenConstants.TokenUsage> getTokenUsages() {
        return this.tokenUsages;
    }

    @Override
    public String getSha1Identifier() {
        return this.sha1Identifier;
    }

    public void setSha1Identifier(String sha1Identifier) {
        this.sha1Identifier = sha1Identifier;
    }
}

