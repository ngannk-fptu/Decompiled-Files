/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Element;

public class SingleKeyResolver
extends KeyResolverSpi {
    private final String keyName;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final SecretKey secretKey;

    public SingleKeyResolver(String keyName, PublicKey publicKey) {
        this.keyName = keyName;
        this.publicKey = publicKey;
        this.privateKey = null;
        this.secretKey = null;
    }

    public SingleKeyResolver(String keyName, PrivateKey privateKey) {
        this.keyName = keyName;
        this.privateKey = privateKey;
        this.publicKey = null;
        this.secretKey = null;
    }

    public SingleKeyResolver(String keyName, SecretKey secretKey) {
        this.keyName = keyName;
        this.secretKey = secretKey;
        this.publicKey = null;
        this.privateKey = null;
    }

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, "KeyName");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        String name;
        if (this.publicKey != null && this.keyName.equals(name = element.getFirstChild().getNodeValue())) {
            return this.publicKey;
        }
        return null;
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        String name;
        if (this.secretKey != null && this.keyName.equals(name = element.getFirstChild().getNodeValue())) {
            return this.secretKey;
        }
        return null;
    }

    @Override
    public PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        String name;
        if (this.privateKey != null && this.keyName.equals(name = element.getFirstChild().getNodeValue())) {
            return this.privateKey;
        }
        return null;
    }
}

