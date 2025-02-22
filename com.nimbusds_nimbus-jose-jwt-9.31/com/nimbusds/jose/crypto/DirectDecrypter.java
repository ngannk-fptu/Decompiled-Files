/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.DirectCryptoProvider;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DirectDecrypter
extends DirectCryptoProvider
implements JWEDecrypter,
CriticalHeaderParamsAware {
    private final boolean promiscuousMode;
    private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();

    public DirectDecrypter(SecretKey key) throws KeyLengthException {
        this(key, false);
    }

    public DirectDecrypter(SecretKey key, boolean promiscuousMode) throws KeyLengthException {
        super(key);
        this.promiscuousMode = promiscuousMode;
    }

    public DirectDecrypter(byte[] keyBytes) throws KeyLengthException {
        this((SecretKey)new SecretKeySpec(keyBytes, "AES"), false);
    }

    public DirectDecrypter(OctetSequenceKey octJWK) throws KeyLengthException {
        this(octJWK.toSecretKey("AES"));
    }

    public DirectDecrypter(SecretKey key, Set<String> defCritHeaders) throws KeyLengthException {
        this(key, defCritHeaders, false);
    }

    public DirectDecrypter(SecretKey key, Set<String> defCritHeaders, boolean promiscuousMode) throws KeyLengthException {
        super(key);
        this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
        this.promiscuousMode = promiscuousMode;
    }

    @Override
    public Set<String> getProcessedCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    @Override
    public Set<String> getDeferredCriticalHeaderParams() {
        return this.critPolicy.getProcessedCriticalHeaderParams();
    }

    @Override
    public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
        if (!this.promiscuousMode) {
            JWEAlgorithm alg = header.getAlgorithm();
            if (!alg.equals(JWEAlgorithm.DIR)) {
                throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
            }
            if (encryptedKey != null) {
                throw new JOSEException("Unexpected present JWE encrypted key");
            }
        }
        if (iv == null) {
            throw new JOSEException("Unexpected present JWE initialization vector (IV)");
        }
        if (authTag == null) {
            throw new JOSEException("Missing JWE authentication tag");
        }
        this.critPolicy.ensureHeaderPasses(header);
        return ContentCryptoProvider.decrypt(header, null, iv, cipherText, authTag, this.getKey(), this.getJCAContext());
    }
}

