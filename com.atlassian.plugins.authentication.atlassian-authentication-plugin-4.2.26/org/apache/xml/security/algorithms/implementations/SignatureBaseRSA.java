/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.algorithms.implementations;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public abstract class SignatureBaseRSA
extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger(SignatureBaseRSA.class);
    private final Signature signatureAlgorithm;

    public SignatureBaseRSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureBaseRSA(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        LOG.debug("Created SignatureRSA using {}", (Object)algorithmID);
        try {
            String providerId;
            this.signatureAlgorithm = provider == null ? ((providerId = JCEMapper.getProviderId()) == null ? Signature.getInstance(algorithmID) : Signature.getInstance(algorithmID, providerId)) : Signature.getInstance(algorithmID, provider);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            Object[] exArgs = new Object[]{algorithmID, ex.getLocalizedMessage()};
            throw new XMLSignatureException("algorithms.NoSuchAlgorithm", exArgs);
        }
    }

    @Override
    protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.setParameter(params);
        }
        catch (InvalidAlgorithmParameterException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected boolean engineVerify(byte[] signature) throws XMLSignatureException {
        try {
            return this.signatureAlgorithm.verify(signature);
        }
        catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {
        SignatureBaseRSA.engineInitVerify(publicKey, this.signatureAlgorithm);
    }

    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            return this.signatureAlgorithm.sign();
        }
        catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom) throws XMLSignatureException {
        SignatureBaseRSA.engineInitSign(privateKey, secureRandom, this.signatureAlgorithm);
    }

    @Override
    protected void engineInitSign(Key privateKey) throws XMLSignatureException {
        this.engineInitSign(privateKey, (SecureRandom)null);
    }

    @Override
    protected void engineUpdate(byte[] input) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(input);
        }
        catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineUpdate(byte input) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(input);
        }
        catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineUpdate(byte[] buf, int offset, int len) throws XMLSignatureException {
        try {
            this.signatureAlgorithm.update(buf, offset, len);
        }
        catch (SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected String engineGetJCEAlgorithmString() {
        return this.signatureAlgorithm.getAlgorithm();
    }

    @Override
    protected String engineGetJCEProviderName() {
        return this.signatureAlgorithm.getProvider().getName();
    }

    @Override
    protected void engineSetHMACOutputLength(int HMACOutputLength2) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.HMACOutputLengthOnlyForHMAC");
    }

    @Override
    protected void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSignatureException {
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnRSA");
    }

    public static class SignatureRSASSAPSS
    extends SignatureBaseRSA {
        PSSParameterSpec pssParameterSpec;

        public SignatureRSASSAPSS() throws XMLSignatureException {
        }

        public SignatureRSASSAPSS(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#rsa-pss";
        }

        @Override
        protected void engineAddContextToElement(Element element) throws XMLSignatureException {
            if (element == null) {
                throw new IllegalArgumentException("null element");
            }
            Document doc = element.getOwnerDocument();
            Element rsaPssParamsElement = doc.createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:RSAPSSParams");
            rsaPssParamsElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:pss", "http://www.w3.org/2007/05/xmldsig-more#");
            Element digestMethodElement = XMLUtils.createElementInSignatureSpace(rsaPssParamsElement.getOwnerDocument(), "DigestMethod");
            digestMethodElement.setAttributeNS(null, "Algorithm", DigestAlgorithm.fromDigestAlgorithm(this.pssParameterSpec.getDigestAlgorithm()).getXmlDigestAlgorithm());
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            rsaPssParamsElement.appendChild(digestMethodElement);
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            Element saltLengthElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:SaltLength");
            Text saltLengthText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.pssParameterSpec.getSaltLength()));
            saltLengthElement.appendChild(saltLengthText);
            rsaPssParamsElement.appendChild(saltLengthElement);
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            Element trailerFieldElement = rsaPssParamsElement.getOwnerDocument().createElementNS("http://www.w3.org/2007/05/xmldsig-more#", "pss:TrailerField");
            Text trailerFieldText = rsaPssParamsElement.getOwnerDocument().createTextNode(String.valueOf(this.pssParameterSpec.getTrailerField()));
            trailerFieldElement.appendChild(trailerFieldText);
            rsaPssParamsElement.appendChild(trailerFieldElement);
            XMLUtils.addReturnToElement(rsaPssParamsElement);
            XMLUtils.addReturnToElement(element);
            element.appendChild(rsaPssParamsElement);
            XMLUtils.addReturnToElement(element);
        }

        @Override
        protected void engineGetContextFromElement(Element element) throws XMLSignatureException {
            if (this.pssParameterSpec == null) {
                super.engineGetContextFromElement(element);
                Element rsaPssParams = XMLUtils.selectNode(element.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", "RSAPSSParams", 0);
                if (rsaPssParams == null) {
                    throw new XMLSignatureException("algorithms.MissingRSAPSSParams");
                }
                Element saltLengthNode = XMLUtils.selectNode(rsaPssParams.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", "SaltLength", 0);
                Element trailerFieldNode = XMLUtils.selectNode(rsaPssParams.getFirstChild(), "http://www.w3.org/2007/05/xmldsig-more#", "TrailerField", 0);
                int trailerField = 1;
                if (trailerFieldNode != null) {
                    try {
                        trailerField = Integer.parseInt(trailerFieldNode.getTextContent());
                    }
                    catch (NumberFormatException ex) {
                        throw new XMLSignatureException("empty", new Object[]{"Invalid trailer field value supplied"});
                    }
                }
                String xmlAlgorithm = XMLUtils.selectDsNode(rsaPssParams.getFirstChild(), "DigestMethod", 0).getAttribute("Algorithm");
                DigestAlgorithm digestAlgorithm = DigestAlgorithm.fromXmlDigestAlgorithm(xmlAlgorithm);
                String digestAlgorithmName = digestAlgorithm.getDigestAlgorithm();
                int saltLength = digestAlgorithm.getSaltLength();
                if (saltLengthNode != null) {
                    try {
                        saltLength = Integer.parseInt(saltLengthNode.getTextContent());
                    }
                    catch (NumberFormatException ex) {
                        throw new XMLSignatureException("empty", new Object[]{"Invalid salt length value supplied"});
                    }
                }
                this.engineSetParameter(new PSSParameterSpec(digestAlgorithmName, "MGF1", new MGF1ParameterSpec(digestAlgorithmName), saltLength, trailerField));
            }
        }

        @Override
        protected void engineSetParameter(AlgorithmParameterSpec params) throws XMLSignatureException {
            this.pssParameterSpec = (PSSParameterSpec)params;
            super.engineSetParameter(params);
        }

        public static enum DigestAlgorithm {
            SHA256("SHA-256", "http://www.w3.org/2001/04/xmlenc#sha256", 32),
            SHA384("SHA-384", "http://www.w3.org/2001/04/xmldsig-more#sha384", 48),
            SHA512("SHA-512", "http://www.w3.org/2001/04/xmlenc#sha512", 64);

            private final String xmlDigestAlgorithm;
            private final String digestAlgorithm;
            private final int saltLength;

            private DigestAlgorithm(String digestAlgorithm, String xmlDigestAlgorithm, int saltLength) {
                this.digestAlgorithm = digestAlgorithm;
                this.xmlDigestAlgorithm = xmlDigestAlgorithm;
                this.saltLength = saltLength;
            }

            public String getXmlDigestAlgorithm() {
                return this.xmlDigestAlgorithm;
            }

            public String getDigestAlgorithm() {
                return this.digestAlgorithm;
            }

            public int getSaltLength() {
                return this.saltLength;
            }

            public static DigestAlgorithm fromXmlDigestAlgorithm(String xmlDigestAlgorithm) throws XMLSignatureException {
                for (DigestAlgorithm value : DigestAlgorithm.values()) {
                    if (!value.getXmlDigestAlgorithm().equals(xmlDigestAlgorithm)) continue;
                    return value;
                }
                throw new XMLSignatureException();
            }

            public static DigestAlgorithm fromDigestAlgorithm(String digestAlgorithm) throws XMLSignatureException {
                for (DigestAlgorithm value : DigestAlgorithm.values()) {
                    if (!value.getDigestAlgorithm().equals(digestAlgorithm)) continue;
                    return value;
                }
                throw new XMLSignatureException();
            }
        }
    }

    public static class SignatureRSASHA3_512MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA3_512MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_512MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-512-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA3_384MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA3_384MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_384MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-384-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA3_256MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA3_256MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_256MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-256-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA3_224MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA3_224MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA3_224MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha3-224-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA512MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA512MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA512MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha512-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA384MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA384MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA384MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha384-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA256MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA256MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA256MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha256-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA224MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA224MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA224MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha224-rsa-MGF1";
        }
    }

    public static class SignatureRSASHA1MGF1
    extends SignatureBaseRSA {
        public SignatureRSASHA1MGF1() throws XMLSignatureException {
        }

        public SignatureRSASHA1MGF1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#sha1-rsa-MGF1";
        }
    }

    public static class SignatureRSAMD5
    extends SignatureBaseRSA {
        public SignatureRSAMD5() throws XMLSignatureException {
        }

        public SignatureRSAMD5(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-md5";
        }
    }

    public static class SignatureRSARIPEMD160
    extends SignatureBaseRSA {
        public SignatureRSARIPEMD160() throws XMLSignatureException {
        }

        public SignatureRSARIPEMD160(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-ripemd160";
        }
    }

    public static class SignatureRSASHA512
    extends SignatureBaseRSA {
        public SignatureRSASHA512() throws XMLSignatureException {
        }

        public SignatureRSASHA512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha512";
        }
    }

    public static class SignatureRSASHA384
    extends SignatureBaseRSA {
        public SignatureRSASHA384() throws XMLSignatureException {
        }

        public SignatureRSASHA384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha384";
        }
    }

    public static class SignatureRSASHA256
    extends SignatureBaseRSA {
        public SignatureRSASHA256() throws XMLSignatureException {
        }

        public SignatureRSASHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
        }
    }

    public static class SignatureRSASHA224
    extends SignatureBaseRSA {
        public SignatureRSASHA224() throws XMLSignatureException {
        }

        public SignatureRSASHA224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#rsa-sha224";
        }
    }

    public static class SignatureRSASHA1
    extends SignatureBaseRSA {
        public SignatureRSASHA1() throws XMLSignatureException {
        }

        public SignatureRSASHA1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
    }
}

