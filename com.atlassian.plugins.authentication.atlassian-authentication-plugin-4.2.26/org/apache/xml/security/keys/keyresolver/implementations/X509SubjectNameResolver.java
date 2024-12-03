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
import java.util.Iterator;
import javax.crypto.SecretKey;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.x509.XMLX509SubjectName;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class X509SubjectNameResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(X509SubjectNameResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        if (!XMLUtils.elementIsInSignatureSpace(element, "X509Data")) {
            return false;
        }
        Element[] x509childNodes = XMLUtils.selectDsNodes(element.getFirstChild(), "X509SubjectName");
        return x509childNodes != null && x509childNodes.length > 0;
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
        Element[] x509childNodes = XMLUtils.selectDsNodes(element.getFirstChild(), "X509SubjectName");
        if (x509childNodes == null || x509childNodes.length <= 0) {
            return null;
        }
        try {
            if (storage == null) {
                Object[] exArgs = new Object[]{"X509SubjectName"};
                KeyResolverException ex = new KeyResolverException("KeyResolver.needStorageResolver", exArgs);
                LOG.debug("", (Throwable)ex);
                throw ex;
            }
            XMLX509SubjectName[] x509childObject = new XMLX509SubjectName[x509childNodes.length];
            for (int i = 0; i < x509childNodes.length; ++i) {
                x509childObject[i] = new XMLX509SubjectName(x509childNodes[i], baseURI);
            }
            Iterator<Certificate> storageIterator = storage.getIterator();
            while (storageIterator.hasNext()) {
                X509Certificate cert = (X509Certificate)storageIterator.next();
                XMLX509SubjectName certSN = new XMLX509SubjectName(element.getOwnerDocument(), cert);
                LOG.debug("Found Certificate SN: {}", (Object)certSN.getSubjectName());
                for (int i = 0; i < x509childObject.length; ++i) {
                    LOG.debug("Found Element SN:     {}", (Object)x509childObject[i].getSubjectName());
                    if (certSN.equals(x509childObject[i])) {
                        LOG.debug("match !!! ");
                        return cert;
                    }
                    LOG.debug("no match...");
                }
            }
            return null;
        }
        catch (XMLSecurityException ex) {
            LOG.debug("XMLSecurityException", (Throwable)ex);
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

