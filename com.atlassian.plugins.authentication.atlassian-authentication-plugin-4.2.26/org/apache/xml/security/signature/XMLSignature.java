/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.signature;

import java.io.IOException;
import java.security.Key;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.X509Data;
import org.apache.xml.security.signature.Manifest;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.SignatureProperties;
import org.apache.xml.security.signature.SignedInfo;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.SignatureElementProxy;
import org.apache.xml.security.utils.SignerOutputStream;
import org.apache.xml.security.utils.UnsyncBufferedOutputStream;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public final class XMLSignature
extends SignatureElementProxy {
    public static final String ALGO_ID_MAC_HMAC_SHA1 = "http://www.w3.org/2000/09/xmldsig#hmac-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    public static final String ALGO_ID_SIGNATURE_DSA_SHA256 = "http://www.w3.org/2009/xmldsig11#dsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    public static final String ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5 = "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
    public static final String ALGO_ID_SIGNATURE_RSA_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA1_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_224_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_256_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_384_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
    public static final String ALGO_ID_SIGNATURE_RSA_SHA3_512_MGF1 = "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";
    public static final String ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5 = "http://www.w3.org/2001/04/xmldsig-more#hmac-md5";
    public static final String ALGO_ID_MAC_HMAC_RIPEMD160 = "http://www.w3.org/2001/04/xmldsig-more#hmac-ripemd160";
    public static final String ALGO_ID_MAC_HMAC_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha224";
    public static final String ALGO_ID_MAC_HMAC_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha256";
    public static final String ALGO_ID_MAC_HMAC_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha384";
    public static final String ALGO_ID_MAC_HMAC_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#hmac-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA1 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA224 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA256 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA384 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
    public static final String ALGO_ID_SIGNATURE_ECDSA_SHA512 = "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
    public static final String ALGO_ID_SIGNATURE_ECDSA_RIPEMD160 = "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
    public static final String ALGO_ID_SIGNATURE_EDDSA_ED25519 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed25519";
    public static final String ALGO_ID_SIGNATURE_EDDSA_ED448 = "http://www.w3.org/2021/04/xmldsig-more#eddsa-ed448";
    public static final String ALGO_ID_SIGNATURE_RSA_PSS = "http://www.w3.org/2007/05/xmldsig-more#rsa-pss";
    private static final Logger LOG = LoggerFactory.getLogger(XMLSignature.class);
    private final SignedInfo signedInfo;
    private KeyInfo keyInfo;
    private boolean followManifestsDuringValidation = false;
    private Element signatureValueElement;
    private static final int MODE_SIGN = 0;
    private static final int MODE_VERIFY = 1;
    private int state = 0;

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, Provider provider) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", provider, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, hmacOutputLength, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength, Provider provider) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, hmacOutputLength, "http://www.w3.org/TR/2001/REC-xml-c14n-20010315", provider, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, canonicalizationMethodURI, null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, String canonicalizationMethodURI, Provider provider) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, 0, canonicalizationMethodURI, provider, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength, String canonicalizationMethodURI) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodURI, hmacOutputLength, canonicalizationMethodURI, null, null);
    }

    public XMLSignature(Document doc, String baseURI, String signatureMethodURI, int hmacOutputLength, String canonicalizationMethodURI, Provider provider, AlgorithmParameterSpec spec) throws XMLSecurityException {
        super(doc);
        String xmlnsDsPrefix = XMLSignature.getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
        if (xmlnsDsPrefix == null || xmlnsDsPrefix.length() == 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
        } else {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + xmlnsDsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        this.addReturnToSelf();
        this.baseURI = baseURI;
        this.signedInfo = new SignedInfo(this.getDocument(), signatureMethodURI, hmacOutputLength, canonicalizationMethodURI, provider, spec);
        this.appendSelf(this.signedInfo);
        this.addReturnToSelf();
        this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.getDocument(), "SignatureValue");
        this.appendSelf(this.signatureValueElement);
        this.addReturnToSelf();
    }

    public XMLSignature(Document doc, String baseURI, Element signatureMethodElem, Element canonicalizationMethodElem) throws XMLSecurityException {
        this(doc, baseURI, signatureMethodElem, canonicalizationMethodElem, null);
    }

    public XMLSignature(Document doc, String baseURI, Element signatureMethodElem, Element canonicalizationMethodElem, Provider provider) throws XMLSecurityException {
        super(doc);
        String xmlnsDsPrefix = XMLSignature.getDefaultPrefix("http://www.w3.org/2000/09/xmldsig#");
        if (xmlnsDsPrefix == null || xmlnsDsPrefix.length() == 0) {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://www.w3.org/2000/09/xmldsig#");
        } else {
            this.getElement().setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + xmlnsDsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        this.addReturnToSelf();
        this.baseURI = baseURI;
        this.signedInfo = new SignedInfo(this.getDocument(), signatureMethodElem, canonicalizationMethodElem, provider);
        this.appendSelf(this.signedInfo);
        this.addReturnToSelf();
        this.signatureValueElement = XMLUtils.createElementInSignatureSpace(this.getDocument(), "SignatureValue");
        this.appendSelf(this.signatureValueElement);
        this.addReturnToSelf();
    }

    public XMLSignature(Element element, String baseURI) throws XMLSignatureException, XMLSecurityException {
        this(element, baseURI, true, null);
    }

    public XMLSignature(Element element, String baseURI, Provider provider) throws XMLSignatureException, XMLSecurityException {
        this(element, baseURI, true, provider);
    }

    public XMLSignature(Element element, String baseURI, boolean secureValidation) throws XMLSignatureException, XMLSecurityException {
        this(element, baseURI, secureValidation, null);
    }

    public XMLSignature(Element element, String baseURI, boolean secureValidation, Provider provider) throws XMLSignatureException, XMLSecurityException {
        super(element, baseURI);
        if (!"http://www.w3.org/2000/09/xmldsig#".equals(element.getNamespaceURI()) || !"Signature".equals(element.getLocalName())) {
            Object[] exArgs = new Object[]{element.getLocalName()};
            throw new XMLSignatureException("signature.Verification.InvalidElement", exArgs);
        }
        Element signedInfoElem = XMLUtils.getNextElement(element.getFirstChild());
        if (signedInfoElem == null || !"http://www.w3.org/2000/09/xmldsig#".equals(signedInfoElem.getNamespaceURI()) || !"SignedInfo".equals(signedInfoElem.getLocalName())) {
            Object[] exArgs = new Object[]{"SignedInfo", "Signature"};
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }
        this.signedInfo = new SignedInfo(signedInfoElem, baseURI, secureValidation, provider);
        signedInfoElem = XMLUtils.getNextElement(element.getFirstChild());
        this.signatureValueElement = XMLUtils.getNextElement(signedInfoElem.getNextSibling());
        if (this.signatureValueElement == null || !"http://www.w3.org/2000/09/xmldsig#".equals(this.signatureValueElement.getNamespaceURI()) || !"SignatureValue".equals(this.signatureValueElement.getLocalName())) {
            Object[] exArgs = new Object[]{"SignatureValue", "Signature"};
            throw new XMLSignatureException("xml.WrongContent", exArgs);
        }
        Attr signatureValueAttr = this.signatureValueElement.getAttributeNodeNS(null, "Id");
        if (signatureValueAttr != null) {
            this.signatureValueElement.setIdAttributeNode(signatureValueAttr, true);
        }
        Element keyInfoElem = XMLUtils.getNextElement(this.signatureValueElement.getNextSibling());
        Element objectElem = null;
        if (keyInfoElem != null && "http://www.w3.org/2000/09/xmldsig#".equals(keyInfoElem.getNamespaceURI()) && "KeyInfo".equals(keyInfoElem.getLocalName())) {
            this.keyInfo = new KeyInfo(keyInfoElem, baseURI);
            this.keyInfo.setSecureValidation(secureValidation);
            objectElem = XMLUtils.getNextElement(keyInfoElem.getNextSibling());
        } else {
            objectElem = keyInfoElem;
        }
        while (objectElem != null) {
            if (!"http://www.w3.org/2000/09/xmldsig#".equals(objectElem.getNamespaceURI()) || !"Object".equals(objectElem.getLocalName())) {
                Object[] exArgs = new Object[]{objectElem.getLocalName()};
                throw new XMLSignatureException("signature.Verification.InvalidElement", exArgs);
            }
            Attr objectAttr = objectElem.getAttributeNodeNS(null, "Id");
            if (objectAttr != null) {
                objectElem.setIdAttributeNode(objectAttr, true);
            }
            for (Node firstChild = objectElem.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                if (firstChild.getNodeType() != 1) continue;
                Element childElem = (Element)firstChild;
                String tag = childElem.getLocalName();
                if ("Manifest".equals(tag)) {
                    new Manifest(childElem, baseURI);
                    continue;
                }
                if (!"SignatureProperties".equals(tag)) continue;
                new SignatureProperties(childElem, baseURI);
            }
            objectElem = XMLUtils.getNextElement(objectElem.getNextSibling());
        }
        this.state = 1;
    }

    public void setId(String id) {
        if (id != null) {
            this.setLocalIdAttribute("Id", id);
        }
    }

    public String getId() {
        return this.getLocalAttribute("Id");
    }

    public SignedInfo getSignedInfo() {
        return this.signedInfo;
    }

    public byte[] getSignatureValue() throws XMLSignatureException {
        String content = XMLUtils.getFullTextChildrenFromNode(this.signatureValueElement);
        return XMLUtils.decode(content);
    }

    private void setSignatureValueElement(byte[] bytes) {
        while (this.signatureValueElement.hasChildNodes()) {
            this.signatureValueElement.removeChild(this.signatureValueElement.getFirstChild());
        }
        String base64codedValue = XMLUtils.encodeToString(bytes);
        if (base64codedValue.length() > 76 && !XMLUtils.ignoreLineBreaks()) {
            base64codedValue = "\n" + base64codedValue + "\n";
        }
        Text t = this.createText(base64codedValue);
        this.signatureValueElement.appendChild(t);
    }

    public KeyInfo getKeyInfo() {
        if (this.state == 0 && this.keyInfo == null) {
            this.keyInfo = new KeyInfo(this.getDocument());
            Element keyInfoElement = this.keyInfo.getElement();
            Element firstObject = XMLUtils.selectDsNode(this.getElement().getFirstChild(), "Object", 0);
            if (firstObject != null) {
                this.getElement().insertBefore(keyInfoElement, firstObject);
                XMLUtils.addReturnBeforeChild(this.getElement(), firstObject);
            } else {
                this.appendSelf(keyInfoElement);
                this.addReturnToSelf();
            }
        }
        return this.keyInfo;
    }

    public void appendObject(ObjectContainer object) throws XMLSignatureException {
        this.appendSelf(object);
        this.addReturnToSelf();
    }

    public ObjectContainer getObjectItem(int i) {
        Element objElem = XMLUtils.selectDsNode(this.getFirstChild(), "Object", i);
        try {
            return new ObjectContainer(objElem, this.baseURI);
        }
        catch (XMLSecurityException ex) {
            return null;
        }
    }

    public int getObjectLength() {
        return this.length("http://www.w3.org/2000/09/xmldsig#", "Object");
    }

    public void sign(Key signingKey) throws XMLSignatureException {
        if (signingKey instanceof PublicKey) {
            throw new IllegalArgumentException(I18n.translate("algorithms.operationOnlyVerification"));
        }
        SignedInfo si = this.getSignedInfo();
        SignatureAlgorithm sa = si.getSignatureAlgorithm();
        try (SignerOutputStream output = new SignerOutputStream(sa);
             UnsyncBufferedOutputStream so = new UnsyncBufferedOutputStream(output);){
            si.generateDigestValues();
            sa.initSign(signingKey);
            si.signInOctetStream(so);
            this.setSignatureValueElement(sa.sign());
        }
        catch (XMLSignatureException ex) {
            throw ex;
        }
        catch (IOException | XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public void addResourceResolver(ResourceResolverSpi resolver) {
        this.getSignedInfo().addResourceResolver(resolver);
    }

    public boolean checkSignatureValue(X509Certificate cert) throws XMLSignatureException {
        if (cert != null) {
            return this.checkSignatureValue(cert.getPublicKey());
        }
        Object[] exArgs = new Object[]{"Didn't get a certificate"};
        throw new XMLSignatureException("empty", exArgs);
    }

    public boolean checkSignatureValue(Key pk) throws XMLSignatureException {
        if (pk == null) {
            Object[] exArgs = new Object[]{"Didn't get a key"};
            throw new XMLSignatureException("empty", exArgs);
        }
        try {
            SignedInfo si = this.getSignedInfo();
            SignatureAlgorithm sa = si.getSignatureAlgorithm();
            LOG.debug("signatureMethodURI = {}", (Object)sa.getAlgorithmURI());
            LOG.debug("jceSigAlgorithm = {}", (Object)sa.getJCEAlgorithmString());
            LOG.debug("PublicKey = {}", (Object)pk);
            byte[] sigBytes = null;
            try (SignerOutputStream so = new SignerOutputStream(sa);
                 UnsyncBufferedOutputStream bos = new UnsyncBufferedOutputStream(so);){
                sa.initVerify(pk);
                LOG.debug("jceSigProvider = {}", (Object)sa.getJCEProviderName());
                si.signInOctetStream(bos);
                sigBytes = this.getSignatureValue();
            }
            catch (IOException ex) {
                LOG.debug(ex.getMessage(), (Throwable)ex);
            }
            catch (XMLSecurityException ex) {
                throw ex;
            }
            if (!sa.verify(sigBytes)) {
                LOG.warn("Signature verification failed.");
                return false;
            }
            return si.verify(this.followManifestsDuringValidation);
        }
        catch (XMLSignatureException ex) {
            throw ex;
        }
        catch (XMLSecurityException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    public void addDocument(String referenceURI, Transforms trans, String digestURI, String referenceId, String referenceType) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, digestURI, referenceId, referenceType);
    }

    public void addDocument(String referenceURI, Transforms trans, String digestURI) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, digestURI, null, null);
    }

    public void addDocument(String referenceURI, Transforms trans) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, trans, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }

    public void addDocument(String referenceURI) throws XMLSignatureException {
        this.signedInfo.addDocument(this.baseURI, referenceURI, null, "http://www.w3.org/2000/09/xmldsig#sha1", null, null);
    }

    public void addKeyInfo(X509Certificate cert) throws XMLSecurityException {
        X509Data x509data = new X509Data(this.getDocument());
        x509data.addCertificate(cert);
        this.getKeyInfo().add(x509data);
    }

    public void addKeyInfo(PublicKey pk) {
        this.getKeyInfo().add(pk);
    }

    public SecretKey createSecretKey(byte[] secretKeyBytes) {
        return this.getSignedInfo().createSecretKey(secretKeyBytes);
    }

    public void setFollowNestedManifests(boolean followManifests) {
        this.followManifestsDuringValidation = followManifests;
    }

    @Override
    public String getBaseLocalName() {
        return "Signature";
    }
}

