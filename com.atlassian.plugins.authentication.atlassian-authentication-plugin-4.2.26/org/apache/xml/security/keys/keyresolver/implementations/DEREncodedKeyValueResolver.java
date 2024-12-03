/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.keyresolver.implementations;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.DEREncodedKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class DEREncodedKeyValueResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(DEREncodedKeyValueResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignature11Space(element, "DEREncodedKeyValue");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            DEREncodedKeyValue derKeyValue = new DEREncodedKeyValue(element, baseURI);
            return derKeyValue.getPublicKey();
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
            return null;
        }
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    public PrivateKey engineLookupAndResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    @Override
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }
}

