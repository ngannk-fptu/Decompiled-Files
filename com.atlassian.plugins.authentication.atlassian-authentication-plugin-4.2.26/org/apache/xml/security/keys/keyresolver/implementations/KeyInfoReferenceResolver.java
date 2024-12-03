/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.keyresolver.implementations;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyInfoReference;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class KeyInfoReferenceResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(KeyInfoReferenceResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignature11Space(element, "KeyInfoReference");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = this.resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getPublicKey();
            }
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
        }
        return null;
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = this.resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getX509Certificate();
            }
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
        }
        return null;
    }

    @Override
    protected SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = this.resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getSecretKey();
            }
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
        }
        return null;
    }

    @Override
    public PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        try {
            KeyInfo referent = this.resolveReferentKeyInfo(element, baseURI, storage, secureValidation);
            if (referent != null) {
                return referent.getPrivateKey();
            }
        }
        catch (XMLSecurityException e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
        }
        return null;
    }

    private KeyInfo resolveReferentKeyInfo(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws XMLSecurityException {
        KeyInfoReference reference = new KeyInfoReference(element, baseURI);
        Attr uriAttr = reference.getURIAttr();
        XMLSignatureInput resource = this.resolveInput(uriAttr, baseURI, secureValidation);
        Element referentElement = null;
        try {
            referentElement = this.obtainReferenceElement(resource, secureValidation);
        }
        catch (Exception e) {
            LOG.debug("XMLSecurityException", (Throwable)e);
            return null;
        }
        if (referentElement == null) {
            LOG.debug("De-reference of KeyInfoReference URI returned null: {}", (Object)uriAttr.getValue());
            return null;
        }
        this.validateReference(referentElement, secureValidation);
        KeyInfo referent = new KeyInfo(referentElement, baseURI);
        referent.setSecureValidation(secureValidation);
        referent.addStorageResolver(storage);
        return referent;
    }

    private void validateReference(Element referentElement, boolean secureValidation) throws XMLSecurityException {
        if (!XMLUtils.elementIsInSignatureSpace(referentElement, "KeyInfo")) {
            Object[] exArgs = new Object[]{new QName(referentElement.getNamespaceURI(), referentElement.getLocalName())};
            throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.WrongType", exArgs);
        }
        KeyInfo referent = new KeyInfo(referentElement, "");
        if (referent.containsKeyInfoReference() || referent.containsRetrievalMethod()) {
            if (secureValidation) {
                throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithSecure");
            }
            throw new XMLSecurityException("KeyInfoReferenceResolver.InvalidReferentElement.ReferenceWithoutSecure");
        }
    }

    private XMLSignatureInput resolveInput(Attr uri, String baseURI, boolean secureValidation) throws XMLSecurityException {
        ResourceResolverContext resContext = new ResourceResolverContext(uri, baseURI, secureValidation);
        if (resContext.isURISafeToResolve()) {
            return ResourceResolver.resolve(resContext);
        }
        String uriToResolve = uri != null ? uri.getValue() : null;
        Object[] exArgs = new Object[]{uriToResolve != null ? uriToResolve : "null", baseURI};
        throw new ResourceResolverException("utils.resolver.noClass", exArgs, uriToResolve, baseURI);
    }

    private Element obtainReferenceElement(XMLSignatureInput resource, boolean secureValidation) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException, KeyResolverException {
        Element e;
        if (resource.isElement()) {
            e = (Element)resource.getSubNode();
        } else {
            if (resource.isNodeSet()) {
                LOG.debug("De-reference of KeyInfoReference returned an unsupported NodeSet");
                return null;
            }
            byte[] inputBytes = resource.getBytes();
            e = KeyInfoReferenceResolver.getDocFromBytes(inputBytes, secureValidation);
        }
        return e;
    }
}

