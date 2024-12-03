/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.signature;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.Manifest;
import org.apache.xml.security.signature.MissingResourceFailureException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.params.InclusiveNamespaces;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SignedInfo
extends Manifest {
    private final SignatureAlgorithm signatureAlgorithm;
    private byte[] c14nizedBytes;
    private Element c14nMethod;
    private Element signatureMethod;

    public SignedInfo(Document doc) throws XMLSecurityException {
        this(doc, "http://www.w3.org/2000/09/xmldsig#dsa-sha1", "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
    }

    public SignedInfo(Document doc, String signatureMethodURI, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, signatureMethodURI, 0, canonicalizationMethodURI, null, null);
    }

    public SignedInfo(Document doc, String signatureMethodURI, String canonicalizationMethodURI, Provider provider) throws XMLSecurityException {
        this(doc, signatureMethodURI, 0, canonicalizationMethodURI, provider, null);
    }

    public SignedInfo(Document doc, String signatureMethodURI, int hMACOutputLength, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, signatureMethodURI, hMACOutputLength, canonicalizationMethodURI, null, null);
    }

    public SignedInfo(Document doc, String signatureMethodURI, int hMACOutputLength, String canonicalizationMethodURI, Provider provider, AlgorithmParameterSpec spec) throws XMLSecurityException {
        super(doc);
        this.c14nMethod = XMLUtils.createElementInSignatureSpace(this.getDocument(), "CanonicalizationMethod");
        this.c14nMethod.setAttributeNS(null, "Algorithm", canonicalizationMethodURI);
        this.appendSelf(this.c14nMethod);
        this.addReturnToSelf();
        this.signatureAlgorithm = hMACOutputLength > 0 ? new SignatureAlgorithm(this.getDocument(), signatureMethodURI, hMACOutputLength, provider) : new SignatureAlgorithm(this.getDocument(), signatureMethodURI, provider, spec);
        this.signatureMethod = this.signatureAlgorithm.getElement();
        this.appendSelf(this.signatureMethod);
        this.addReturnToSelf();
    }

    public SignedInfo(Document doc, Element signatureMethodElem, Element canonicalizationMethodElem) throws XMLSecurityException {
        this(doc, signatureMethodElem, canonicalizationMethodElem, null);
    }

    public SignedInfo(Document doc, Element signatureMethodElem, Element canonicalizationMethodElem, Provider provider) throws XMLSecurityException {
        super(doc);
        this.c14nMethod = canonicalizationMethodElem;
        this.appendSelf(this.c14nMethod);
        this.addReturnToSelf();
        this.signatureAlgorithm = new SignatureAlgorithm(signatureMethodElem, null, provider);
        this.signatureMethod = this.signatureAlgorithm.getElement();
        this.appendSelf(this.signatureMethod);
        this.addReturnToSelf();
    }

    public SignedInfo(Element element, String baseURI) throws XMLSecurityException {
        this(element, baseURI, true, null);
    }

    public SignedInfo(Element element, String baseURI, boolean secureValidation) throws XMLSecurityException {
        this(element, baseURI, secureValidation, null);
    }

    public SignedInfo(Element element, String baseURI, boolean secureValidation, Provider provider) throws XMLSecurityException {
        super(element, baseURI, secureValidation);
        this.c14nMethod = XMLUtils.getNextElement(element.getFirstChild());
        if (this.c14nMethod == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.c14nMethod.getNamespaceURI()) || !"CanonicalizationMethod".equals(this.c14nMethod.getLocalName())) {
            Object[] exArgs = new Object[]{"CanonicalizationMethod", "SignedInfo"};
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }
        this.signatureMethod = XMLUtils.getNextElement(this.c14nMethod.getNextSibling());
        if (this.signatureMethod == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.signatureMethod.getNamespaceURI()) || !"SignatureMethod".equals(this.signatureMethod.getLocalName())) {
            Object[] exArgs = new Object[]{"SignatureMethod", "SignedInfo"};
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }
        this.signatureAlgorithm = new SignatureAlgorithm(this.signatureMethod, this.getBaseURI(), secureValidation, provider);
    }

    public boolean verify() throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(false);
    }

    public boolean verify(boolean followManifests) throws MissingResourceFailureException, XMLSecurityException {
        return super.verifyReferences(followManifests);
    }

    public byte[] getCanonicalizedOctetStream() throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException, IOException {
        if (this.c14nizedBytes == null) {
            Canonicalizer c14nizer = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
            String inclusiveNamespaces = this.getInclusiveNamespaces();
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                if (inclusiveNamespaces == null) {
                    c14nizer.canonicalizeSubtree(this.getElement(), baos);
                } else {
                    c14nizer.canonicalizeSubtree(this.getElement(), inclusiveNamespaces, baos);
                }
                this.c14nizedBytes = baos.toByteArray();
            }
        }
        return (byte[])this.c14nizedBytes.clone();
    }

    public void signInOctetStream(OutputStream os) throws CanonicalizationException, InvalidCanonicalizerException, XMLSecurityException {
        if (this.c14nizedBytes == null) {
            Canonicalizer c14nizer = Canonicalizer.getInstance(this.getCanonicalizationMethodURI());
            String inclusiveNamespaces = this.getInclusiveNamespaces();
            if (inclusiveNamespaces == null) {
                c14nizer.canonicalizeSubtree(this.getElement(), os);
            } else {
                c14nizer.canonicalizeSubtree(this.getElement(), inclusiveNamespaces, os);
            }
        } else {
            try {
                os.write(this.c14nizedBytes);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getCanonicalizationMethodURI() {
        return this.c14nMethod.getAttributeNS(null, "Algorithm");
    }

    public String getSignatureMethodURI() {
        Element signatureElement = this.getSignatureMethodElement();
        if (signatureElement != null) {
            return signatureElement.getAttributeNS(null, "Algorithm");
        }
        return null;
    }

    public Element getSignatureMethodElement() {
        return this.signatureMethod;
    }

    public SecretKey createSecretKey(byte[] secretKeyBytes) {
        return new SecretKeySpec(secretKeyBytes, this.signatureAlgorithm.getJCEAlgorithmString());
    }

    public SignatureAlgorithm getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    @Override
    public String getBaseLocalName() {
        return "SignedInfo";
    }

    public String getInclusiveNamespaces() {
        String c14nMethodURI = this.getCanonicalizationMethodURI();
        if (!"http://www.w3.org/2001/10/xml-exc-c14n#".equals(c14nMethodURI) && !"http://www.w3.org/2001/10/xml-exc-c14n#WithComments".equals(c14nMethodURI)) {
            return null;
        }
        Element inclusiveElement = XMLUtils.getNextElement(this.c14nMethod.getFirstChild());
        if (inclusiveElement != null) {
            try {
                String inclusiveNamespaces = new InclusiveNamespaces(inclusiveElement, "http://www.w3.org/2001/10/xml-exc-c14n#").getInclusiveNamespaces();
                return inclusiveNamespaces;
            }
            catch (XMLSecurityException e) {
                return null;
            }
        }
        return null;
    }
}

