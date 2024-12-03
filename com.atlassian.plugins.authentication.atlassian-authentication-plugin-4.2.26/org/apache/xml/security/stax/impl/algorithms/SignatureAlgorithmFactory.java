/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.algorithms;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.config.JCEAlgorithmMapper;
import org.apache.xml.security.stax.impl.algorithms.HMACSignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.PKISignatureAlgorithm;
import org.apache.xml.security.stax.impl.algorithms.SignatureAlgorithm;

public final class SignatureAlgorithmFactory {
    private static SignatureAlgorithmFactory instance;

    private SignatureAlgorithmFactory() {
    }

    public static synchronized SignatureAlgorithmFactory getInstance() {
        if (instance == null) {
            instance = new SignatureAlgorithmFactory();
        }
        return instance;
    }

    public SignatureAlgorithm getSignatureAlgorithm(String algoURI) throws XMLSecurityException, NoSuchProviderException, NoSuchAlgorithmException {
        String algorithmClass = JCEAlgorithmMapper.getAlgorithmClassFromURI(algoURI);
        if (algorithmClass == null) {
            throw new XMLSecurityException("algorithms.NoSuchMap", new Object[]{algoURI});
        }
        String jceName = JCEAlgorithmMapper.translateURItoJCEID(algoURI);
        String jceProvider = JCEAlgorithmMapper.getJCEProviderFromURI(algoURI);
        if ("MAC".equalsIgnoreCase(algorithmClass)) {
            return new HMACSignatureAlgorithm(jceName, jceProvider);
        }
        if ("Signature".equalsIgnoreCase(algorithmClass)) {
            return new PKISignatureAlgorithm(jceName, jceProvider);
        }
        return null;
    }
}

