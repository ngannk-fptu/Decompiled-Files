/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.algorithms.implementations;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.algorithms.implementations.ECDSAUtils;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SignatureECDSA
extends SignatureAlgorithmSpi {
    private static final Logger LOG = LoggerFactory.getLogger(SignatureECDSA.class);
    private final Signature signatureAlgorithm;
    private int signIntLen = -1;

    public static byte[] convertASN1toXMLDSIG(byte[] asn1Bytes, int rawLen) throws IOException {
        return ECDSAUtils.convertASN1toXMLDSIG(asn1Bytes, rawLen);
    }

    public static byte[] convertXMLDSIGtoASN1(byte[] xmldsigBytes) throws IOException {
        return ECDSAUtils.convertXMLDSIGtoASN1(xmldsigBytes);
    }

    public SignatureECDSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureECDSA(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        LOG.debug("Created SignatureECDSA using {}", (Object)algorithmID);
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
            byte[] jcebytes = SignatureECDSA.convertXMLDSIGtoASN1(signature);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Called ECDSA.verify() on " + XMLUtils.encodeToString(signature));
            }
            return this.signatureAlgorithm.verify(jcebytes);
        }
        catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {
        SignatureECDSA.engineInitVerify(publicKey, this.signatureAlgorithm);
    }

    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            byte[] jcebytes = this.signatureAlgorithm.sign();
            return SignatureECDSA.convertASN1toXMLDSIG(jcebytes, this.signIntLen);
        }
        catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom) throws XMLSignatureException {
        if (privateKey instanceof ECPrivateKey) {
            ECPrivateKey ecKey = (ECPrivateKey)privateKey;
            this.signIntLen = (ecKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
        }
        SignatureECDSA.engineInitSign(privateKey, secureRandom, this.signatureAlgorithm);
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

    public static class SignatureECDSARIPEMD160
    extends SignatureECDSA {
        public SignatureECDSARIPEMD160() throws XMLSignatureException {
        }

        public SignatureECDSARIPEMD160(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2007/05/xmldsig-more#ecdsa-ripemd160";
        }
    }

    public static class SignatureECDSASHA512
    extends SignatureECDSA {
        public SignatureECDSASHA512() throws XMLSignatureException {
        }

        public SignatureECDSASHA512(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha512";
        }
    }

    public static class SignatureECDSASHA384
    extends SignatureECDSA {
        public SignatureECDSASHA384() throws XMLSignatureException {
        }

        public SignatureECDSASHA384(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha384";
        }
    }

    public static class SignatureECDSASHA256
    extends SignatureECDSA {
        public SignatureECDSASHA256() throws XMLSignatureException {
        }

        public SignatureECDSASHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha256";
        }
    }

    public static class SignatureECDSASHA224
    extends SignatureECDSA {
        public SignatureECDSASHA224() throws XMLSignatureException {
        }

        public SignatureECDSASHA224(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha224";
        }
    }

    public static class SignatureECDSASHA1
    extends SignatureECDSA {
        public SignatureECDSASHA1() throws XMLSignatureException {
        }

        public SignatureECDSASHA1(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2001/04/xmldsig-more#ecdsa-sha1";
        }
    }
}

