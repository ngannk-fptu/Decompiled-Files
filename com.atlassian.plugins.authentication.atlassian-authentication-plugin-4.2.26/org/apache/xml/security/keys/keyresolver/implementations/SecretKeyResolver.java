/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class SecretKeyResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(SecretKeyResolver.class);
    private final KeyStore keyStore;
    private final char[] password;

    public SecretKeyResolver(KeyStore keyStore, char[] password) {
        this.keyStore = keyStore;
        this.password = password;
    }

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, "KeyName");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        LOG.debug("Can I resolve {}?", (Object)element.getTagName());
        if (XMLUtils.elementIsInSignatureSpace(element, "KeyName")) {
            String keyName = element.getFirstChild().getNodeValue();
            try {
                Key key = this.keyStore.getKey(keyName, this.password);
                if (key instanceof SecretKey) {
                    return (SecretKey)key;
                }
            }
            catch (Exception e) {
                LOG.debug("Cannot recover the key", (Throwable)e);
            }
        }
        LOG.debug("I can't");
        return null;
    }

    @Override
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }
}

