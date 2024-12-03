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
import java.security.interfaces.DSAKey;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithmSpi;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignatureDSA
extends SignatureAlgorithmSpi {
    public static final String URI = "http://www.w3.org/2000/09/xmldsig#dsa-sha1";
    private static final Logger LOG = LoggerFactory.getLogger(SignatureDSA.class);
    private final Signature signatureAlgorithm;
    private int size;

    @Override
    protected String engineGetURI() {
        return URI;
    }

    public SignatureDSA() throws XMLSignatureException {
        this(null);
    }

    public SignatureDSA(Provider provider) throws XMLSignatureException {
        String algorithmID = JCEMapper.translateURItoJCEID(this.engineGetURI());
        LOG.debug("Created SignatureDSA using {}", (Object)algorithmID);
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Called DSA.verify() on " + XMLUtils.encodeToString(signature));
            }
            byte[] jcebytes = JavaUtils.convertDsaXMLDSIGtoASN1(signature, this.size / 8);
            return this.signatureAlgorithm.verify(jcebytes);
        }
        catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitVerify(Key publicKey) throws XMLSignatureException {
        SignatureDSA.engineInitVerify(publicKey, this.signatureAlgorithm);
        this.size = ((DSAKey)((Object)publicKey)).getParams().getQ().bitLength();
    }

    @Override
    protected byte[] engineSign() throws XMLSignatureException {
        try {
            byte[] jcebytes = this.signatureAlgorithm.sign();
            return JavaUtils.convertDsaASN1toXMLDSIG(jcebytes, this.size / 8);
        }
        catch (IOException | SignatureException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    @Override
    protected void engineInitSign(Key privateKey, SecureRandom secureRandom) throws XMLSignatureException {
        SignatureDSA.engineInitSign(privateKey, secureRandom, this.signatureAlgorithm);
        this.size = ((DSAKey)((Object)privateKey)).getParams().getQ().bitLength();
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
        throw new XMLSignatureException("algorithms.CannotUseAlgorithmParameterSpecOnDSA");
    }

    public static class SHA256
    extends SignatureDSA {
        public SHA256() throws XMLSignatureException {
        }

        public SHA256(Provider provider) throws XMLSignatureException {
            super(provider);
        }

        @Override
        public String engineGetURI() {
            return "http://www.w3.org/2009/xmldsig11#dsa-sha256";
        }
    }
}

