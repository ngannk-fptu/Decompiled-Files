/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.signature;

import java.io.IOException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.signature.MissingResourceFailureException;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.ReferenceNotInitializedException;
import org.apache.xml.security.signature.VerifiedReference;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Manifest
extends SignatureElementProxy {
    public static final int MAXIMUM_REFERENCE_COUNT = 30;
    private static final Logger LOG = LoggerFactory.getLogger(Manifest.class);
    private static Integer referenceCount = AccessController.doPrivileged(() -> Integer.parseInt(System.getProperty("org.apache.xml.security.maxReferences", Integer.toString(30))));
    private List<Reference> references;
    private Element[] referencesEl;
    private List<VerifiedReference> verificationResults;
    private Map<String, String> resolverProperties;
    private List<ResourceResolverSpi> perManifestResolvers;
    private boolean secureValidation = true;

    public Manifest(Document doc) {
        super(doc);
        this.addReturnToSelf();
        this.references = new ArrayList<Reference>();
    }

    public Manifest(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, true);
    }

    public Manifest(Element element, String baseURI, boolean secureValidation) throws XMLSecurityException {
        super(element, baseURI);
        Attr attr = element.getAttributeNodeNS(null, "Id");
        if (attr != null) {
            element.setIdAttributeNode(attr, true);
        }
        this.secureValidation = secureValidation;
        this.referencesEl = XMLUtils.selectDsNodes(this.getFirstChild(), "Reference");
        int le = this.referencesEl.length;
        if (le == 0) {
            Object[] exArgs = new Object[]{"Reference", "Manifest"};
            throw new DOMException(4, I18n.translate("xml.WrongContent", exArgs));
        }
        if (secureValidation && le > referenceCount) {
            Object[] exArgs = new Object[]{le, referenceCount};
            throw new XMLSecurityException("signature.tooManyReferences", exArgs);
        }
        this.references = new ArrayList<Reference>(le);
        for (int i = 0; i < le; ++i) {
            Element refElem = this.referencesEl[i];
            Attr refAttr = refElem.getAttributeNodeNS(null, "Id");
            if (refAttr != null) {
                refElem.setIdAttributeNode(refAttr, true);
            }
            this.references.add(null);
        }
    }

    public void addDocument(String baseURI, String referenceURI, Transforms transforms, String digestURI, String referenceId, String referenceType) throws XMLSignatureException {
        Reference ref = new Reference(this.getDocument(), baseURI, referenceURI, this, transforms, digestURI);
        if (referenceId != null) {
            ref.setId(referenceId);
        }
        if (referenceType != null) {
            ref.setType(referenceType);
        }
        this.references.add(ref);
        this.appendSelf(ref);
        this.addReturnToSelf();
    }

    public void generateDigestValues() throws XMLSignatureException, ReferenceNotInitializedException {
        for (int i = 0; i < this.getLength(); ++i) {
            Reference currentRef = this.references.get(i);
            currentRef.generateDigestValue();
        }
    }

    public int getLength() {
        return this.references.size();
    }

    public Reference item(int i) throws XMLSecurityException {
        if (this.references.get(i) == null) {
            Reference ref = new Reference(this.referencesEl[i], this.baseURI, this, this.secureValidation);
            this.references.set(i, ref);
        }
        return this.references.get(i);
    }

    public void setId(String Id) {
        if (Id != null) {
            this.setLocalIdAttribute("Id", Id);
        }
    }

    public String getId() {
        return this.getLocalAttribute("Id");
    }

    public boolean verifyReferences() throws MissingResourceFailureException, XMLSecurityException {
        return this.verifyReferences(false);
    }

    public boolean verifyReferences(boolean followManifests) throws MissingResourceFailureException, XMLSecurityException {
        if (this.referencesEl == null) {
            this.referencesEl = XMLUtils.selectDsNodes(this.getFirstChild(), "Reference");
        }
        LOG.debug("verify {} References", (Object)this.referencesEl.length);
        LOG.debug("I am {} requested to follow nested Manifests", (Object)(followManifests ? "" : "not"));
        if (this.referencesEl.length == 0) {
            throw new XMLSecurityException("empty", new Object[]{"References are empty"});
        }
        if (this.secureValidation && this.referencesEl.length > referenceCount) {
            Object[] exArgs = new Object[]{this.referencesEl.length, referenceCount};
            throw new XMLSecurityException("signature.tooManyReferences", exArgs);
        }
        this.verificationResults = new ArrayList<VerifiedReference>(this.referencesEl.length);
        boolean verify = true;
        for (int i = 0; i < this.referencesEl.length; ++i) {
            Reference currentRef = new Reference(this.referencesEl[i], this.baseURI, this, this.secureValidation);
            this.references.set(i, currentRef);
            try {
                boolean currentRefVerified = currentRef.verify();
                if (!currentRefVerified) {
                    verify = false;
                }
                LOG.debug("The Reference has Type {}", (Object)currentRef.getType());
                List<VerifiedReference> manifestReferences = Collections.emptyList();
                if (verify && followManifests && currentRef.typeIsReferenceToManifest()) {
                    LOG.debug("We have to follow a nested Manifest");
                    try {
                        XMLSignatureInput signedManifestNodes = currentRef.dereferenceURIandPerformTransforms(null);
                        Set<Node> nl = signedManifestNodes.getNodeSet();
                        Manifest referencedManifest = null;
                        for (Node n : nl) {
                            if (n.getNodeType() != 1 || !((Element)n).getNamespaceURI().equals("http://www.w3.org/2000/09/xmldsig#") || !((Element)n).getLocalName().equals("Manifest")) continue;
                            try {
                                referencedManifest = new Manifest((Element)n, signedManifestNodes.getSourceURI(), this.secureValidation);
                                break;
                            }
                            catch (XMLSecurityException ex) {
                                LOG.debug(ex.getMessage(), (Throwable)ex);
                            }
                        }
                        if (referencedManifest == null) {
                            throw new MissingResourceFailureException(currentRef, "empty", new Object[]{"No Manifest found"});
                        }
                        referencedManifest.perManifestResolvers = this.perManifestResolvers;
                        referencedManifest.resolverProperties = this.resolverProperties;
                        boolean referencedManifestValid = referencedManifest.verifyReferences(followManifests);
                        if (!referencedManifestValid) {
                            verify = false;
                            LOG.warn("The nested Manifest was invalid (bad)");
                        } else {
                            LOG.debug("The nested Manifest was valid (good)");
                        }
                        manifestReferences = referencedManifest.getVerificationResults();
                    }
                    catch (IOException ex) {
                        throw new ReferenceNotInitializedException(ex);
                    }
                    catch (XMLParserException ex) {
                        throw new ReferenceNotInitializedException(ex);
                    }
                }
                this.verificationResults.add(new VerifiedReference(currentRefVerified, currentRef.getURI(), manifestReferences));
                continue;
            }
            catch (ReferenceNotInitializedException ex) {
                Object[] exArgs = new Object[]{currentRef.getURI()};
                throw new MissingResourceFailureException(ex, currentRef, "signature.Verification.Reference.NoInput", exArgs);
            }
        }
        return verify;
    }

    public boolean getVerificationResult(int index) throws XMLSecurityException {
        if (index < 0 || index > this.getLength() - 1) {
            Object[] exArgs = new Object[]{Integer.toString(index), Integer.toString(this.getLength())};
            IndexOutOfBoundsException e = new IndexOutOfBoundsException(I18n.translate("signature.Verification.IndexOutOfBounds", exArgs));
            throw new XMLSecurityException(e);
        }
        if (this.verificationResults == null) {
            try {
                this.verifyReferences();
            }
            catch (Exception ex) {
                throw new XMLSecurityException(ex);
            }
        }
        return this.verificationResults.get(index).isValid();
    }

    public List<VerifiedReference> getVerificationResults() {
        if (this.verificationResults == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.verificationResults);
    }

    public void addResourceResolver(ResourceResolverSpi resolver) {
        if (resolver == null) {
            return;
        }
        if (this.perManifestResolvers == null) {
            this.perManifestResolvers = new ArrayList<ResourceResolverSpi>();
        }
        this.perManifestResolvers.add(resolver);
    }

    public List<ResourceResolverSpi> getPerManifestResolvers() {
        return this.perManifestResolvers;
    }

    public Map<String, String> getResolverProperties() {
        return this.resolverProperties;
    }

    public void setResolverProperty(String key, String value) {
        if (this.resolverProperties == null) {
            this.resolverProperties = new HashMap<String, String>(10);
        }
        this.resolverProperties.put(key, value);
    }

    public String getResolverProperty(String key) {
        return this.resolverProperties.get(key);
    }

    public byte[] getSignedContentItem(int i) throws XMLSignatureException {
        try {
            return this.getReferencedContentAfterTransformsItem(i).getBytes();
        }
        catch (IOException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (CanonicalizationException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (InvalidCanonicalizerException ex) {
            throw new XMLSignatureException(ex);
        }
        catch (XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public XMLSignatureInput getReferencedContentBeforeTransformsItem(int i) throws XMLSecurityException {
        return this.item(i).getContentsBeforeTransformation();
    }

    public XMLSignatureInput getReferencedContentAfterTransformsItem(int i) throws XMLSecurityException {
        return this.item(i).getContentsAfterTransformation();
    }

    public int getSignedContentLength() {
        return this.getLength();
    }

    @Override
    public String getBaseLocalName() {
        return "Manifest";
    }

    public boolean isSecureValidation() {
        return this.secureValidation;
    }
}

