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
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class X509CertificateResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(X509CertificateResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return "http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI());
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
            Element[] els = XMLUtils.selectDsNodes(element.getFirstChild(), "X509Certificate");
            if (els == null || els.length == 0) {
                Element el = XMLUtils.selectDsNode(element.getFirstChild(), "X509Data", 0);
                if (el != null) {
                    return this.engineResolveX509Certificate(el, baseURI, storage, secureValidation);
                }
                return null;
            }
            for (int i = 0; i < els.length; ++i) {
                XMLX509Certificate xmlCert = new XMLX509Certificate(els[i], baseURI);
                X509Certificate cert = xmlCert.getX509Certificate();
                if (cert == null) continue;
                return cert;
            }
            return null;
        }
        catch (XMLSecurityException ex) {
            LOG.debug("Security Exception", (Throwable)ex);
            throw new KeyResolverException(ex);
        }
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }
}

