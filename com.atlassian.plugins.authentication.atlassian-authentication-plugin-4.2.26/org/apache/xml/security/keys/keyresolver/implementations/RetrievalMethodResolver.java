/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.keyresolver.implementations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import javax.crypto.SecretKey;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.RetrievalMethod;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RetrievalMethodResolver
extends KeyResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(RetrievalMethodResolver.class);

    @Override
    protected boolean engineCanResolve(Element element, String baseURI, StorageResolver storage) {
        return XMLUtils.elementIsInSignatureSpace(element, "RetrievalMethod");
    }

    @Override
    protected PublicKey engineResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        try {
            RetrievalMethod rm = new RetrievalMethod(element, baseURI);
            String type = rm.getType();
            XMLSignatureInput resource = RetrievalMethodResolver.resolveInput(rm, baseURI, secureValidation);
            if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(type)) {
                X509Certificate cert = RetrievalMethodResolver.getRawCertificate(resource);
                if (cert != null) {
                    return cert.getPublicKey();
                }
                return null;
            }
            Element e = RetrievalMethodResolver.obtainReferenceElement(resource, secureValidation);
            if (XMLUtils.elementIsInSignatureSpace(e, "RetrievalMethod")) {
                if (secureValidation) {
                    if (LOG.isDebugEnabled()) {
                        String error = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
                        LOG.debug(error);
                    }
                    return null;
                }
                RetrievalMethod rm2 = new RetrievalMethod(e, baseURI);
                XMLSignatureInput resource2 = RetrievalMethodResolver.resolveInput(rm2, baseURI, secureValidation);
                Element e2 = RetrievalMethodResolver.obtainReferenceElement(resource2, secureValidation);
                if (e2 == element) {
                    LOG.debug("Error: Can't have RetrievalMethods pointing to each other");
                    return null;
                }
            }
            return RetrievalMethodResolver.resolveKey(e, baseURI, storage, secureValidation);
        }
        catch (XMLSecurityException ex) {
            LOG.debug("XMLSecurityException", (Throwable)ex);
        }
        catch (CertificateException ex) {
            LOG.debug("CertificateException", (Throwable)ex);
        }
        catch (IOException ex) {
            LOG.debug("IOException", (Throwable)ex);
        }
        return null;
    }

    @Override
    protected X509Certificate engineResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        try {
            RetrievalMethod rm = new RetrievalMethod(element, baseURI);
            String type = rm.getType();
            XMLSignatureInput resource = RetrievalMethodResolver.resolveInput(rm, baseURI, secureValidation);
            if ("http://www.w3.org/2000/09/xmldsig#rawX509Certificate".equals(type)) {
                return RetrievalMethodResolver.getRawCertificate(resource);
            }
            Element e = RetrievalMethodResolver.obtainReferenceElement(resource, secureValidation);
            if (XMLUtils.elementIsInSignatureSpace(e, "RetrievalMethod")) {
                if (secureValidation) {
                    if (LOG.isDebugEnabled()) {
                        String error = "Error: It is forbidden to have one RetrievalMethod point to another with secure validation";
                        LOG.debug(error);
                    }
                    return null;
                }
                RetrievalMethod rm2 = new RetrievalMethod(e, baseURI);
                XMLSignatureInput resource2 = RetrievalMethodResolver.resolveInput(rm2, baseURI, secureValidation);
                Element e2 = RetrievalMethodResolver.obtainReferenceElement(resource2, secureValidation);
                if (e2 == element) {
                    LOG.debug("Error: Can't have RetrievalMethods pointing to each other");
                    return null;
                }
            }
            return RetrievalMethodResolver.resolveCertificate(e, baseURI, storage, secureValidation);
        }
        catch (XMLSecurityException ex) {
            LOG.debug("XMLSecurityException", (Throwable)ex);
        }
        catch (CertificateException ex) {
            LOG.debug("CertificateException", (Throwable)ex);
        }
        catch (IOException ex) {
            LOG.debug("IOException", (Throwable)ex);
        }
        return null;
    }

    private static X509Certificate resolveCertificate(Element e, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (e != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Now we have a {" + e.getNamespaceURI() + "}" + e.getLocalName() + " Element");
            }
            return KeyResolver.getX509Certificate(e, baseURI, storage, secureValidation);
        }
        return null;
    }

    private static PublicKey resolveKey(Element e, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (e != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Now we have a {" + e.getNamespaceURI() + "}" + e.getLocalName() + " Element");
            }
            return KeyResolver.getPublicKey(e, baseURI, storage, secureValidation);
        }
        return null;
    }

    private static Element obtainReferenceElement(XMLSignatureInput resource, boolean secureValidation) throws CanonicalizationException, XMLParserException, IOException, KeyResolverException {
        Element e;
        if (resource.isElement()) {
            e = (Element)resource.getSubNode();
        } else if (resource.isNodeSet()) {
            e = RetrievalMethodResolver.getDocumentElement(resource.getNodeSet());
        } else {
            byte[] inputBytes = resource.getBytes();
            e = RetrievalMethodResolver.getDocFromBytes(inputBytes, secureValidation);
            LOG.debug("we have to parse {} bytes", (Object)inputBytes.length);
        }
        return e;
    }

    private static X509Certificate getRawCertificate(XMLSignatureInput resource) throws CanonicalizationException, IOException, CertificateException {
        byte[] inputBytes = resource.getBytes();
        CertificateFactory certFact = CertificateFactory.getInstance("X.509");
        try (ByteArrayInputStream is = new ByteArrayInputStream(inputBytes);){
            X509Certificate x509Certificate = (X509Certificate)certFact.generateCertificate(is);
            return x509Certificate;
        }
    }

    private static XMLSignatureInput resolveInput(RetrievalMethod rm, String baseURI, boolean secureValidation) throws XMLSecurityException {
        Attr uri = rm.getURIAttr();
        Transforms transforms = rm.getTransforms();
        ResourceResolverContext resContext = new ResourceResolverContext(uri, baseURI, secureValidation);
        if (resContext.isURISafeToResolve()) {
            XMLSignatureInput resource = ResourceResolver.resolve(resContext);
            if (transforms != null) {
                LOG.debug("We have Transforms");
                resource = transforms.performTransforms(resource);
            }
            return resource;
        }
        String uriToResolve = uri != null ? uri.getValue() : null;
        Object[] exArgs = new Object[]{uriToResolve != null ? uriToResolve : "null", baseURI};
        throw new ResourceResolverException("utils.resolver.noClass", exArgs, uriToResolve, baseURI);
    }

    @Override
    public SecretKey engineResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    @Override
    protected PrivateKey engineResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) {
        return null;
    }

    private static Element getDocumentElement(Set<Node> set) {
        Iterator<Node> it = set.iterator();
        Node e = null;
        while (it.hasNext()) {
            Node currentNode = it.next();
            if (currentNode == null || 1 != currentNode.getNodeType()) continue;
            e = (Element)currentNode;
            break;
        }
        ArrayList<Node> parents = new ArrayList<Node>();
        while (e != null) {
            parents.add(e);
            Node n = e.getParentNode();
            if (n == null || 1 != n.getNodeType()) break;
            e = (Element)n;
        }
        ListIterator it2 = parents.listIterator(parents.size() - 1);
        Element ele = null;
        while (it2.hasPrevious()) {
            ele = (Element)it2.previous();
            if (!set.contains(ele)) continue;
            return ele;
        }
        return null;
    }
}

