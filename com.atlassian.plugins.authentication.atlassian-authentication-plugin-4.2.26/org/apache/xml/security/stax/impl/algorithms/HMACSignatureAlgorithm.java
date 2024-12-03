/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.algorithms;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Mac;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;

public class HMACSignatureAlgorithm
implements SignatureAlgorithm {
    private Mac mac;

    public HMACSignatureAlgorithm(String jceName, String jceProvider) throws NoSuchProviderException, NoSuchAlgorithmException {
        this.mac = jceProvider != null ? Mac.getInstance(jceName, jceProvider) : Mac.getInstance(jceName);
    }

    @Override
    public void engineUpdate(byte[] input) throws XMLSecurityException {
        this.mac.update(input);
    }

    @Override
    public void engineUpdate(byte input) throws XMLSecurityException {
        this.mac.update(input);
    }

    @Override
    public void engineUpdate(byte[] buf, int offset, int len) throws XMLSecurityException {
        this.mac.update(buf, offset, len);
    }

    @Override
    public void engineInitSign(Key signingKey) throws XMLSecurityException {
        try {
            this.mac.init(signingKey);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, SecureRandom secureRandom) throws XMLSecurityException {
        try {
            this.mac.init(signingKey);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public void engineInitSign(Key signingKey, AlgorithmParameterSpec algorithmParameterSpec) throws XMLSecurityException {
        try {
            this.mac.init(signingKey, algorithmParameterSpec);
        }
        catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public byte[] engineSign() throws XMLSecurityException {
        return this.mac.doFinal();
    }

    @Override
    public void engineInitVerify(Key verificationKey) throws XMLSecurityException {
        try {
            this.mac.init(verificationKey);
        }
        catch (InvalidKeyException e) {
            throw new XMLSecurityException(e);
        }
    }

    @Override
    public boolean engineVerify(byte[] signature) throws XMLSecurityException {
        byte[] completeResult = this.mac.doFinal();
        return MessageDigest.isEqual(completeResult, signature);
    }

    @Override
    public void engineSetParameter(AlgorithmParameterSpec params) throws XMLSecurityException {
    }
}

