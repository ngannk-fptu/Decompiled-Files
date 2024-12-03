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
import org.apache.xml.security.keys.content.keyvalues.ECKeyValue;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class ECKeyValueResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(ECKeyValueResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, "KeyValue") || XMLUtils.elementIsInSignatureSpace(element, "ECKeyValue");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        if (element == null) {
            return null;
        }
        Element ecKeyElement = null;
        boolean isKeyValue = XMLUtils.elementIsInSignatureSpace(element, "KeyValue");
        if (isKeyValue) {
            ecKeyElement = XMLUtils.selectDs11Node(element.getFirstChild(), "ECKeyValue", 0);
        } else if (XMLUtils.elementIsInSignature11Space(element, "ECKeyValue")) {
            ecKeyElement = element;
        }
        if (ecKeyElement == null) {
            return null;
        }
        try {
            ECKeyValue ecKeyValue = new ECKeyValue(ecKeyElement, baseURI);
            return ecKeyValue.getPublicKey();
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

