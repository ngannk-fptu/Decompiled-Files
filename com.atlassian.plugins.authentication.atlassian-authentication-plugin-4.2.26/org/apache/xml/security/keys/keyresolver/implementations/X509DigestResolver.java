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
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.keys.content.x509.XMLX509Digest;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class X509DigestResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(X509DigestResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        if (XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            try {
                X509Data x509Data = new X509Data(element, baseURI);
                return x509Data.containsDigest();
            }
            catch (XMLSecurityException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        X509Certificate cert = this.engineResolveX509Certificate(element, baseURI, storage, secureValidation);
        if (cert != null) {
            return cert.getPublicKey();
        }
        return null;
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            return this.resolveCertificate(element, baseURI, storage);
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
            return null;
        }
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        return null;
    }

    private X509Certificate resolveCertificate(Element element, String baseURI, StorageResolver storage) throws XMLSecurityException {
        XMLX509Digest[] x509Digests = null;
        Element[] x509childNodes = XMLUtils.selectDs11Nodes(element.getFirstChild(), "X509Digest");
        if (x509childNodes == null || x509childNodes.length <= 0) {
            return null;
        }
        try {
            this.checkStorage(storage);
            x509Digests = new XMLX509Digest[x509childNodes.length];
            for (int i = 0; i < x509childNodes.length; ++i) {
                x509Digests[i] = new XMLX509Digest(x509childNodes[i], baseURI);
            }
            Iterator<Certificate> storageIterator = storage.getIterator();
            while (storageIterator.hasNext()) {
                X509Certificate cert = (X509Certificate)storageIterator.next();
                for (int i = 0; i < x509Digests.length; ++i) {
                    XMLX509Digest keyInfoDigest = x509Digests[i];
                    byte[] certDigestBytes = XMLX509Digest.getDigestBytesFromCert(cert, keyInfoDigest.getAlgorithm());
                    if (!Arrays.equals(keyInfoDigest.getDigestBytes(), certDigestBytes)) continue;
                    LOG.debug("Found certificate with: {}", (Object)cert.getSubjectX500Principal().getName());
                    return cert;
                }
            }
        }
        catch (XMLSecurityException ex) {
            throw new KeyResolverException(ex);
        }
        return null;
    }

    private void checkStorage(StorageResolver storage) throws KeyResolverException {
        if (storage == null) {
            Object[] exArgs = new Object[]{"X509Digest"};
            KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", exArgs);
            LOG.debug("", (Throwable)ex);
            throw ex;
        }
    }

    @Override
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }
}

