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
import org.apache.xml.security.keys.content.keyvalues.DSAKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class DSAKeyValueResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(DSAKeyValueResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, "KeyValue") || XMLUtils.elementIsInSignatureSpace(element, "DSAKeyValue");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        if (element == null) {
            return null;
        }
        Element dsaKeyElement = null;
        boolean isKeyValue = XMLUtils.elementIsInSignatureSpace(element, "KeyValue");
        if (isKeyValue) {
            dsaKeyElement = XMLUtils.selectDsNode(element.getFirstChild(), "DSAKeyValue", 0);
        } else if (XMLUtils.elementIsInSignatureSpace(element, "DSAKeyValue")) {
            dsaKeyElement = element;
        }
        if (dsaKeyElement == null) {
            return null;
        }
        try {
            DSAKeyValue dsaKeyValue = new DSAKeyValue(dsaKeyElement, baseURI);
            PublicKey pk = dsaKeyValue.getPublicKey();
            return pk;
        }
        catch (XMLSecurityException ex) {
            LOG.debug(ex.getMessage(), (Throwable)ex);
            return null;
        }
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
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

