/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AESKW;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.BaseJWEProvider;
import com.nimbusds.jose.crypto.impl.ConcatKDF;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.ECDH;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.util.Base64URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;

public abstract class ECDHCryptoProvider
extends BaseJWEProvider {
    public static final Set<JWEAlgorithm> SUPPORTED_ALGORITHMS;
    public static final Set<EncryptionMethod> SUPPORTED_ENCRYPTION_METHODS;
    private final Curve curve;
    private final ConcatKDF concatKDF;

    protected ECDHCryptoProvider(Curve curve) throws JOSEException {
        super(SUPPORTED_ALGORITHMS, ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS);
        Curve definedCurve;
        Curve curve2 = definedCurve = curve != null ? curve : new Curve("unknown");
        if (!this.supportedEllipticCurves().contains(curve)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedEllipticCurve(definedCurve, this.supportedEllipticCurves()));
        }
        this.curve = curve;
        this.concatKDF = new ConcatKDF("SHA-256");
    }

    protected ConcatKDF getConcatKDF() {
        return this.concatKDF;
    }

    public abstract Set<Curve> supportedEllipticCurves();

    public Curve getCurve() {
        return this.curve;
    }

    protected JWECryptoParts encryptWithZ(JWEHeader header, SecretKey Z, byte[] clearText) throws JOSEException {
        return this.encryptWithZ(header, Z, clearText, null);
    }

    protected JWECryptoParts encryptWithZ(JWEHeader header, SecretKey Z, byte[] clearText, SecretKey contentEncryptionKey) throws JOSEException {
        Base64URL encryptedKey;
        SecretKey cek;
        JWEAlgorithm alg = header.getAlgorithm();
        ECDH.AlgorithmMode algMode = ECDH.resolveAlgorithmMode(alg);
        EncryptionMethod enc = header.getEncryptionMethod();
        this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
        SecretKey sharedKey = ECDH.deriveSharedKey(header, Z, this.getConcatKDF());
        if (algMode.equals((Object)ECDH.AlgorithmMode.DIRECT)) {
            cek = sharedKey;
            encryptedKey = null;
        } else if (algMode.equals((Object)ECDH.AlgorithmMode.KW)) {
            cek = contentEncryptionKey != null ? contentEncryptionKey : ContentCryptoProvider.generateCEK(enc, this.getJCAContext().getSecureRandom());
            encryptedKey = Base64URL.encode(AESKW.wrapCEK(cek, sharedKey, this.getJCAContext().getKeyEncryptionProvider()));
        } else {
            throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + (Object)((Object)algMode));
        }
        return ContentCryptoProvider.encrypt(header, clearText, cek, encryptedKey, this.getJCAContext());
    }

    protected byte[] decryptWithZ(JWEHeader header, SecretKey Z, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
        SecretKey cek;
        JWEAlgorithm alg = header.getAlgorithm();
        ECDH.AlgorithmMode algMode = ECDH.resolveAlgorithmMode(alg);
        this.getConcatKDF().getJCAContext().setProvider(this.getJCAContext().getMACProvider());
        SecretKey sharedKey = ECDH.deriveSharedKey(header, Z, this.getConcatKDF());
        if (algMode.equals((Object)ECDH.AlgorithmMode.DIRECT)) {
            cek = sharedKey;
        } else if (algMode.equals((Object)ECDH.AlgorithmMode.KW)) {
            if (encryptedKey == null) {
                throw new JOSEException("Missing JWE encrypted key");
            }
            cek = AESKW.unwrapCEK(sharedKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
        } else {
            throw new JOSEException("Unexpected JWE ECDH algorithm mode: " + (Object)((Object)algMode));
        }
        return ContentCryptoProvider.decrypt(header, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
    }

    static {
        SUPPORTED_ENCRYPTION_METHODS = ContentCryptoProvider.SUPPORTED_ENCRYPTION_METHODS;
        LinkedHashSet<JWEAlgorithm> algs = new LinkedHashSet<JWEAlgorithm>();
        algs.add(JWEAlgorithm.ECDH_ES);
        algs.add(JWEAlgorithm.ECDH_ES_A128KW);
        algs.add(JWEAlgorithm.ECDH_ES_A192KW);
        algs.add(JWEAlgorithm.ECDH_ES_A256KW);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
    }
}

