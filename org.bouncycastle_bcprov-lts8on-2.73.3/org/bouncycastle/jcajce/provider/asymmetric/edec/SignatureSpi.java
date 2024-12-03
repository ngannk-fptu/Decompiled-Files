/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.jcajce.provider.asymmetric.edec.EdECUtil;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class SignatureSpi
extends java.security.SignatureSpi {
    private static final byte[] EMPTY_CONTEXT = new byte[0];
    private final String algorithm;
    private Signer signer;

    SignatureSpi(String algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    protected void engineInitVerify(PublicKey publicKey) throws InvalidKeyException {
        AsymmetricKeyParameter pub = SignatureSpi.getLwEdDSAKeyPublic(publicKey);
        if (pub instanceof Ed25519PublicKeyParameters) {
            this.signer = this.getSigner("Ed25519");
        } else if (pub instanceof Ed448PublicKeyParameters) {
            this.signer = this.getSigner("Ed448");
        } else {
            throw new IllegalStateException("unsupported public key type");
        }
        this.signer.init(false, pub);
    }

    @Override
    protected void engineInitSign(PrivateKey privateKey) throws InvalidKeyException {
        AsymmetricKeyParameter priv = SignatureSpi.getLwEdDSAKeyPrivate(privateKey);
        if (priv instanceof Ed25519PrivateKeyParameters) {
            this.signer = this.getSigner("Ed25519");
        } else if (priv instanceof Ed448PrivateKeyParameters) {
            this.signer = this.getSigner("Ed448");
        } else {
            throw new IllegalStateException("unsupported private key type");
        }
        this.signer.init(true, priv);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPrivate(PrivateKey key) throws InvalidKeyException {
        return EdECUtil.generatePrivateKeyParameter(key);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPublic(PublicKey key) throws InvalidKeyException {
        return EdECUtil.generatePublicKeyParameter(key);
    }

    private Signer getSigner(String alg) throws InvalidKeyException {
        if (this.algorithm != null && !alg.equals(this.algorithm)) {
            throw new InvalidKeyException("inappropriate key for " + this.algorithm);
        }
        if (alg.equals("Ed448")) {
            return new Ed448Signer(EMPTY_CONTEXT);
        }
        return new Ed25519Signer();
    }

    @Override
    protected void engineUpdate(byte b) throws SignatureException {
        this.signer.update(b);
    }

    @Override
    protected void engineUpdate(byte[] bytes, int off, int len) throws SignatureException {
        this.signer.update(bytes, off, len);
    }

    @Override
    protected byte[] engineSign() throws SignatureException {
        try {
            return this.signer.generateSignature();
        }
        catch (CryptoException e) {
            throw new SignatureException(e.getMessage());
        }
    }

    @Override
    protected boolean engineVerify(byte[] signature) throws SignatureException {
        return this.signer.verifySignature(signature);
    }

    @Override
    protected void engineSetParameter(String s, Object o) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    @Override
    protected Object engineGetParameter(String s) throws InvalidParameterException {
        throw new UnsupportedOperationException("engineGetParameter unsupported");
    }

    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class Ed25519
    extends SignatureSpi {
        public Ed25519() {
            super("Ed25519");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class Ed448
    extends SignatureSpi {
        public Ed448() {
            super("Ed448");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class EdDSA
    extends SignatureSpi {
        public EdDSA() {
            super(null);
        }
    }
}

