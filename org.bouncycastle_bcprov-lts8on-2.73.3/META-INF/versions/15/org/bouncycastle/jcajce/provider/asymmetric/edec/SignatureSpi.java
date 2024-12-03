/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.interfaces.EdECPrivateKey;
import java.security.interfaces.EdECPublicKey;
import java.security.spec.EdECPoint;
import java.security.spec.NamedParameterSpec;
import java.util.Optional;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

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

    private static Ed25519PrivateKeyParameters getEd25519PrivateKey(byte[] keyData) throws InvalidKeyException {
        if (32 != keyData.length) {
            throw new InvalidKeyException("cannot use EdEC private key (Ed25519) with bytes of incorrect length");
        }
        return new Ed25519PrivateKeyParameters(keyData, 0);
    }

    private static Ed25519PublicKeyParameters getEd25519PublicKey(EdECPoint point) throws InvalidKeyException {
        byte[] keyData = SignatureSpi.getPublicKeyData(32, point);
        return new Ed25519PublicKeyParameters(keyData, 0);
    }

    private static Ed448PrivateKeyParameters getEd448PrivateKey(byte[] keyData) throws InvalidKeyException {
        if (57 != keyData.length) {
            throw new InvalidKeyException("cannot use EdEC private key (Ed448) with bytes of incorrect length");
        }
        return new Ed448PrivateKeyParameters(keyData, 0);
    }

    private static Ed448PublicKeyParameters getEd448PublicKey(EdECPoint point) throws InvalidKeyException {
        byte[] keyData = SignatureSpi.getPublicKeyData(57, point);
        return new Ed448PublicKeyParameters(keyData, 0);
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPrivate(Key key) throws InvalidKeyException {
        if (key instanceof BCEdDSAPrivateKey) {
            return ((BCEdDSAPrivateKey)key).engineGetKeyParameters();
        }
        if (key instanceof EdECPrivateKey) {
            NamedParameterSpec params;
            EdECPrivateKey jcaPriv = (EdECPrivateKey)key;
            Optional<byte[]> bytes = jcaPriv.getBytes();
            if (!bytes.isPresent()) {
                throw new InvalidKeyException("cannot use EdEC private key without bytes");
            }
            String algorithm = jcaPriv.getAlgorithm();
            if ("Ed25519".equalsIgnoreCase(algorithm)) {
                return SignatureSpi.getEd25519PrivateKey(bytes.get());
            }
            if ("Ed448".equalsIgnoreCase(algorithm)) {
                return SignatureSpi.getEd448PrivateKey(bytes.get());
            }
            if ("EdDSA".equalsIgnoreCase(algorithm) && (params = jcaPriv.getParams()) instanceof NamedParameterSpec) {
                NamedParameterSpec namedParams = params;
                String name = namedParams.getName();
                if ("Ed25519".equalsIgnoreCase(name)) {
                    return SignatureSpi.getEd25519PrivateKey(bytes.get());
                }
                if ("Ed448".equalsIgnoreCase(name)) {
                    return SignatureSpi.getEd448PrivateKey(bytes.get());
                }
            }
            throw new InvalidKeyException("cannot use EdEC private key with unknown algorithm");
        }
        throw new InvalidKeyException("cannot identify EdDSA private key");
    }

    private static AsymmetricKeyParameter getLwEdDSAKeyPublic(Key key) throws InvalidKeyException {
        if (key instanceof BCEdDSAPublicKey) {
            return ((BCEdDSAPublicKey)key).engineGetKeyParameters();
        }
        if (key instanceof EdECPublicKey) {
            NamedParameterSpec params;
            EdECPublicKey jcaPub = (EdECPublicKey)key;
            EdECPoint point = jcaPub.getPoint();
            String algorithm = jcaPub.getAlgorithm();
            if ("Ed25519".equalsIgnoreCase(algorithm)) {
                return SignatureSpi.getEd25519PublicKey(point);
            }
            if ("Ed448".equalsIgnoreCase(algorithm)) {
                return SignatureSpi.getEd448PublicKey(point);
            }
            if ("EdDSA".equalsIgnoreCase(algorithm) && (params = jcaPub.getParams()) instanceof NamedParameterSpec) {
                NamedParameterSpec namedParams = params;
                String name = namedParams.getName();
                if ("Ed25519".equalsIgnoreCase(name)) {
                    return SignatureSpi.getEd25519PublicKey(point);
                }
                if ("Ed448".equalsIgnoreCase(name)) {
                    return SignatureSpi.getEd448PublicKey(point);
                }
            }
            throw new InvalidKeyException("cannot use EdEC public key with unknown algorithm");
        }
        throw new InvalidKeyException("cannot identify EdDSA public key");
    }

    private static byte[] getPublicKeyData(int length, EdECPoint point) throws InvalidKeyException {
        BigInteger y = point.getY();
        if (y.signum() < 0) {
            throw new InvalidKeyException("cannot use EdEC public key with negative Y value");
        }
        try {
            byte[] keyData = BigIntegers.asUnsignedByteArray(length, y);
            if ((keyData[0] & 0x80) == 0) {
                if (point.isXOdd()) {
                    keyData[0] = (byte)(keyData[0] | 0x80);
                }
                return Arrays.reverseInPlace(keyData);
            }
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        throw new InvalidKeyException("cannot use EdEC public key with invalid Y value");
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

