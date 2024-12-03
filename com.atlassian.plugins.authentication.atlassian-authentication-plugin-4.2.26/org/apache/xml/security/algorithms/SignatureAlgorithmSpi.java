/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.algorithms;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.AlgorithmParameterSpec;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Element;

public abstract class SignatureAlgorithmSpi {
    protected abstract String engineGetURI();

    protected abstract String engineGetJCEAlgorithmString();

    protected abstract String engineGetJCEProviderName();

    protected abstract void engineUpdate(byte[] var1) throws XMLSignatureException;

    protected abstract void engineUpdate(byte var1) throws XMLSignatureException;

    protected abstract void engineUpdate(byte[] var1, int var2, int var3) throws XMLSignatureException;

    protected abstract void engineInitSign(Key var1) throws XMLSignatureException;

    protected abstract void engineInitSign(Key var1, SecureRandom var2) throws XMLSignatureException;

    protected abstract void engineInitSign(Key var1, AlgorithmParameterSpec var2) throws XMLSignatureException;

    protected abstract byte[] engineSign() throws XMLSignatureException;

    protected abstract void engineInitVerify(Key var1) throws XMLSignatureException;

    protected abstract boolean engineVerify(byte[] var1) throws XMLSignatureException;

    protected abstract void engineSetParameter(AlgorithmParameterSpec var1) throws XMLSignatureException;

    protected void engineGetContextFromElement(Element element) throws XMLSignatureException {
    }

    protected void engineAddContextToElement(Element element) throws XMLSignatureException {
    }

    protected abstract void engineSetHMACOutputLength(int var1) throws XMLSignatureException;

    protected static void engineInitVerify(Key publicKey, Signature signatureAlgorithm) throws XMLSignatureException {
        if (!(publicKey instanceof PublicKey)) {
            String supplied = null;
            if (publicKey != null) {
                supplied = publicKey.getClass().getName();
            }
            String needed = PublicKey.class.getName();
            Object[] exArgs = new Object[]{supplied, needed};
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }
        try {
            signatureAlgorithm.initVerify((PublicKey)publicKey);
        }
        catch (InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
    }

    protected static void engineInitSign(Key privateKey, SecureRandom secureRandom, Signature signatureAlgorithm) throws XMLSignatureException {
        if (!(privateKey instanceof PrivateKey)) {
            String supplied = null;
            if (privateKey != null) {
                supplied = privateKey.getClass().getName();
            }
            String needed = PrivateKey.class.getName();
            Object[] exArgs = new Object[]{supplied, needed};
            throw new XMLSignatureException("algorithms.WrongKeyForThisOperation", exArgs);
        }
        try {
            if (secureRandom == null) {
                signatureAlgorithm.initSign((PrivateKey)privateKey);
            } else {
                signatureAlgorithm.initSign((PrivateKey)privateKey, secureRandom);
            }
        }
        catch (InvalidKeyException ex) {
            throw new XMLSignatureException(ex);
        }
    }
}

