/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.HMAC;
import com.nimbusds.jose.crypto.impl.MACProvider;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class MACSigner
extends MACProvider
implements JWSSigner {
    public static int getMinRequiredSecretLength(JWSAlgorithm alg) throws JOSEException {
        if (JWSAlgorithm.HS256.equals(alg)) {
            return 256;
        }
        if (JWSAlgorithm.HS384.equals(alg)) {
            return 384;
        }
        if (JWSAlgorithm.HS512.equals(alg)) {
            return 512;
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, SUPPORTED_ALGORITHMS));
    }

    public static Set<JWSAlgorithm> getCompatibleAlgorithms(int secretLength) {
        LinkedHashSet<JWSAlgorithm> hmacAlgs = new LinkedHashSet<JWSAlgorithm>();
        if (secretLength >= 256) {
            hmacAlgs.add(JWSAlgorithm.HS256);
        }
        if (secretLength >= 384) {
            hmacAlgs.add(JWSAlgorithm.HS384);
        }
        if (secretLength >= 512) {
            hmacAlgs.add(JWSAlgorithm.HS512);
        }
        return Collections.unmodifiableSet(hmacAlgs);
    }

    public MACSigner(byte[] secret) throws KeyLengthException {
        super(secret, MACSigner.getCompatibleAlgorithms(ByteUtils.bitLength(secret.length)));
    }

    public MACSigner(String secretString) throws KeyLengthException {
        this(secretString.getBytes(StandardCharset.UTF_8));
    }

    public MACSigner(SecretKey secretKey) throws KeyLengthException {
        this(secretKey.getEncoded());
    }

    public MACSigner(OctetSequenceKey jwk) throws KeyLengthException {
        this(jwk.toByteArray());
    }

    @Override
    public Base64URL sign(JWSHeader header, byte[] signingInput) throws JOSEException {
        int minRequiredLength = MACSigner.getMinRequiredSecretLength(header.getAlgorithm());
        if (this.getSecret().length < ByteUtils.byteLength(minRequiredLength)) {
            throw new KeyLengthException("The secret length for " + header.getAlgorithm() + " must be at least " + minRequiredLength + " bits");
        }
        String jcaAlg = MACSigner.getJCAAlgorithmName(header.getAlgorithm());
        byte[] hmac = HMAC.compute(jcaAlg, this.getSecret(), signingInput, this.getJCAContext().getProvider());
        return Base64URL.encode(hmac);
    }
}

