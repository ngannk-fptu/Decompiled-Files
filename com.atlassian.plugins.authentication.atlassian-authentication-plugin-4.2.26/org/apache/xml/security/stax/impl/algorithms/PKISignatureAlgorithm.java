/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.algorithms;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.algorithms.implementations.ECDSAUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;
import org.apache.xml.security.utils.JavaUtils;

public class PKISignatureAlgorithm
implements SignatureAlgorithm {
    private final String jceName;
    private final Signature signature;
    private int signIntLen = -1;

    public PKISignatureAlgorithm(String jceName, String jceProvider) throws NoSuchProviderException, NoSuchAlgorithmException {
        this.jceName = jceName;
        this.signature = jceProvider != null ? Signature.getInstance(this.jceName, jceProvider) : Signature.getInstance(this.jceName);
    }

    @Override
    public void engineUpdate(byte[] input) throws XMLSecurityException {
        try {
            this.signature.update(input);
        }
        catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineUpdate(byte input) throws XMLSecurityException {
        try {
            this.signature.update(input);
        }
        catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineUpdate(byte[] buf, int offset, int len) throws XMLSecurityException {
        try {
            this.signature.update(buf, offset, len);
        }
        catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey) throws XMLSecurityException {
        this.initSignIntLen(signingKey);
        try {
            this.signature.initSign((PrivateKey)signingKey);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, SecureRandom secureRandom) throws XMLSecurityException {
        this.initSignIntLen(signingKey);
        try {
            this.signature.initSign((PrivateKey)signingKey, secureRandom);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException {
        this.initSignIntLen(signingKey);
        try {
            this.signature.initSign((PrivateKey)signingKey);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    private void initSignIntLen(Key signingKey) {
        if (signingKey instanceof ECPrivateKey) {
            ECPrivateKey ecKey = (ECPrivateKey)signingKey;
            this.signIntLen = (ecKey.getParams().getCurve().getField().getFieldSize() + 7) / 8;
        }
    }

    @Override
    public byte[] engineSign() throws XMLSecurityException {
        try {
            byte[] jcebytes = this.signature.sign();
            if (this.jceName.contains("ECDSA")) {
                return ECDSAUtils.convertASN1toXMLDSIG(jcebytes, this.signIntLen);
            }
            if (this.jceName.contains("DSA")) {
                return JavaUtils.convertDsaASN1toXMLDSIG(jcebytes, 20);
            }
            return jcebytes;
        }
        catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
        catch (IOException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitVerify(Key verificationKey) throws XMLSecurityException {
        try {
            this.signature.initVerify((PublicKey)verificationKey);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public boolean engineVerify(byte[] signature) throws XMLSecurityException {
        try {
            byte[] jcebytes = signature;
            if (this.jceName.contains("ECDSA")) {
                jcebytes = ECDSAUtils.convertXMLDSIGtoASN1(jcebytes);
            } else if (this.jceName.contains("DSA")) {
                jcebytes = JavaUtils.convertDsaXMLDSIGtoASN1(jcebytes, 20);
            }
            return this.signature.verify(jcebytes);
        }
        catch (SignatureException e) {
            throw new XMLSecurityException(e);
        }
        catch (IOException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineSetParameter(AlgorithmParameterSpec params) throws XMLSecurityException {
        try {
            this.signature.setParameter(params);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new XMLSecurityException(e);
        }
    }
}

