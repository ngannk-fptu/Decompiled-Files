/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.interfaces.XECPrivateKey;
import java.security.interfaces.XECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.NamedParameterSpec;
import java.util.Optional;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.agreement.XDHUnifiedAgreement;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.params.XDHUPrivateParameters;
import org.bouncycastle.crypto.params.XDHUPublicParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class KeyAgreementSpi
extends BaseAgreementSpi {
    private RawAgreement agreement;
    private DHUParameterSpec dhuSpec;
    private byte[] result;

    KeyAgreementSpi(String algorithm) {
        super(algorithm, null);
    }

    KeyAgreementSpi(String algorithm, DerivationFunction kdf) {
        super(algorithm, kdf);
    }

    @Override
    protected byte[] doCalcSecret() {
        return this.result;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void doInitFromKey(Key key, AlgorithmParameterSpec params, SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AsymmetricKeyParameter priv = KeyAgreementSpi.getLwXDHKeyPrivate(key);
        if (priv instanceof X25519PrivateKeyParameters) {
            this.agreement = this.getAgreement("X25519");
        } else {
            if (!(priv instanceof X448PrivateKeyParameters)) throw new IllegalStateException("unsupported private key type");
            this.agreement = this.getAgreement("X448");
        }
        this.ukmParameters = null;
        if (params instanceof DHUParameterSpec) {
            if (this.kaAlgorithm.indexOf(85) < 0) {
                throw new InvalidAlgorithmParameterException("agreement algorithm not DHU based");
            }
            this.dhuSpec = (DHUParameterSpec)params;
            this.ukmParameters = this.dhuSpec.getUserKeyingMaterial();
            this.agreement.init(new XDHUPrivateParameters(priv, ((BCXDHPrivateKey)this.dhuSpec.getEphemeralPrivateKey()).engineGetKeyParameters(), ((BCXDHPublicKey)this.dhuSpec.getEphemeralPublicKey()).engineGetKeyParameters()));
        } else if (params != null) {
            this.agreement.init(priv);
            if (!(params instanceof UserKeyingMaterialSpec)) throw new InvalidAlgorithmParameterException("unknown ParameterSpec");
            if (this.kdf == null) {
                throw new InvalidAlgorithmParameterException("no KDF specified for UserKeyingMaterialSpec");
            }
            this.ukmParameters = ((UserKeyingMaterialSpec)params).getUserKeyingMaterial();
        } else {
            this.agreement.init(priv);
        }
        if (this.kdf == null || this.ukmParameters != null) return;
        this.ukmParameters = new byte[0];
    }

    @Override
    protected Key engineDoPhase(Key key, boolean lastPhase) throws InvalidKeyException, IllegalStateException {
        if (this.agreement == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!lastPhase) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        AsymmetricKeyParameter pub = this.getLwXDHKeyPublic(key);
        this.result = new byte[this.agreement.getAgreementSize()];
        if (this.dhuSpec != null) {
            this.agreement.calculateAgreement(new XDHUPublicParameters(pub, ((BCXDHPublicKey)this.dhuSpec.getOtherPartyEphemeralKey()).engineGetKeyParameters()), this.result, 0);
        } else {
            this.agreement.calculateAgreement(pub, this.result, 0);
        }
        return null;
    }

    private RawAgreement getAgreement(String alg) throws InvalidKeyException {
        if (!this.kaAlgorithm.equals("XDH") && !this.kaAlgorithm.startsWith(alg)) {
            throw new InvalidKeyException("inappropriate key for " + this.kaAlgorithm);
        }
        if (this.kaAlgorithm.indexOf(85) > 0) {
            if (alg.startsWith("X448")) {
                return new XDHUnifiedAgreement(new X448Agreement());
            }
            return new XDHUnifiedAgreement(new X25519Agreement());
        }
        if (alg.startsWith("X448")) {
            return new X448Agreement();
        }
        return new X25519Agreement();
    }

    private static AsymmetricKeyParameter getLwXDHKeyPrivate(Key key) throws InvalidKeyException {
        if (key instanceof BCXDHPrivateKey) {
            return ((BCXDHPrivateKey)key).engineGetKeyParameters();
        }
        if (key instanceof XECPrivateKey) {
            AlgorithmParameterSpec params;
            XECPrivateKey jcePriv = (XECPrivateKey)key;
            Optional<byte[]> scalar = jcePriv.getScalar();
            if (!scalar.isPresent()) {
                throw new InvalidKeyException("cannot use XEC private key without scalar");
            }
            String algorithm = jcePriv.getAlgorithm();
            if ("X25519".equalsIgnoreCase(algorithm)) {
                return KeyAgreementSpi.getX25519PrivateKey(scalar.get());
            }
            if ("X448".equalsIgnoreCase(algorithm)) {
                return KeyAgreementSpi.getX448PrivateKey(scalar.get());
            }
            if ("XDH".equalsIgnoreCase(algorithm) && (params = jcePriv.getParams()) instanceof NamedParameterSpec) {
                NamedParameterSpec namedParams = (NamedParameterSpec)params;
                String name = namedParams.getName();
                if ("X25519".equalsIgnoreCase(name)) {
                    return KeyAgreementSpi.getX25519PrivateKey(scalar.get());
                }
                if ("X448".equalsIgnoreCase(name)) {
                    return KeyAgreementSpi.getX448PrivateKey(scalar.get());
                }
            }
            throw new InvalidKeyException("cannot use XEC private key with unknown algorithm");
        }
        throw new InvalidKeyException("cannot identify XDH private key");
    }

    private AsymmetricKeyParameter getLwXDHKeyPublic(Key key) throws InvalidKeyException {
        if (key instanceof BCXDHPublicKey) {
            return ((BCXDHPublicKey)key).engineGetKeyParameters();
        }
        if (key instanceof XECPublicKey) {
            AlgorithmParameterSpec params;
            XECPublicKey jcePub = (XECPublicKey)key;
            BigInteger u = jcePub.getU();
            if (u.signum() < 0) {
                throw new InvalidKeyException("cannot use XEC public key with negative U value");
            }
            String algorithm = jcePub.getAlgorithm();
            if ("X25519".equalsIgnoreCase(algorithm)) {
                return KeyAgreementSpi.getX25519PublicKey(u);
            }
            if ("X448".equalsIgnoreCase(algorithm)) {
                return KeyAgreementSpi.getX448PublicKey(u);
            }
            if ("XDH".equalsIgnoreCase(algorithm) && (params = jcePub.getParams()) instanceof NamedParameterSpec) {
                NamedParameterSpec namedParams = (NamedParameterSpec)params;
                String name = namedParams.getName();
                if ("X25519".equalsIgnoreCase(name)) {
                    return KeyAgreementSpi.getX25519PublicKey(u);
                }
                if ("X448".equalsIgnoreCase(name)) {
                    return KeyAgreementSpi.getX448PublicKey(u);
                }
            }
            throw new InvalidKeyException("cannot use XEC public key with unknown algorithm");
        }
        throw new InvalidKeyException("cannot identify XDH public key");
    }

    private static byte[] getPublicKeyData(int length, BigInteger u) throws InvalidKeyException {
        try {
            return Arrays.reverseInPlace(BigIntegers.asUnsignedByteArray(length, u));
        }
        catch (RuntimeException e) {
            throw new InvalidKeyException("cannot use XEC public key with invalid U value");
        }
    }

    private static X25519PrivateKeyParameters getX25519PrivateKey(byte[] keyData) throws InvalidKeyException {
        if (32 != keyData.length) {
            throw new InvalidKeyException("cannot use XEC private key (X25519) with scalar of incorrect length");
        }
        return new X25519PrivateKeyParameters(keyData, 0);
    }

    private static X25519PublicKeyParameters getX25519PublicKey(BigInteger u) throws InvalidKeyException {
        byte[] keyData = KeyAgreementSpi.getPublicKeyData(32, u);
        return new X25519PublicKeyParameters(keyData, 0);
    }

    private static X448PrivateKeyParameters getX448PrivateKey(byte[] keyData) throws InvalidKeyException {
        if (56 != keyData.length) {
            throw new InvalidKeyException("cannot use XEC private key (X448) with scalar of incorrect length");
        }
        return new X448PrivateKeyParameters(keyData, 0);
    }

    private static X448PublicKeyParameters getX448PublicKey(BigInteger u) throws InvalidKeyException {
        byte[] keyData = KeyAgreementSpi.getPublicKeyData(56, u);
        return new X448PublicKeyParameters(keyData, 0);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X448UwithSHA512KDF
    extends KeyAgreementSpi {
        public X448UwithSHA512KDF() {
            super("X448UwithSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X25519UwithSHA256KDF
    extends KeyAgreementSpi {
        public X25519UwithSHA256KDF() {
            super("X25519UwithSHA256KDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X448UwithSHA512CKDF
    extends KeyAgreementSpi {
        public X448UwithSHA512CKDF() {
            super("X448UwithSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X25519UwithSHA256CKDF
    extends KeyAgreementSpi {
        public X25519UwithSHA256CKDF() {
            super("X25519UwithSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X448withSHA512KDF
    extends KeyAgreementSpi {
        public X448withSHA512KDF() {
            super("X448withSHA512KDF", new KDF2BytesGenerator(DigestFactory.createSHA512()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X25519withSHA256KDF
    extends KeyAgreementSpi {
        public X25519withSHA256KDF() {
            super("X25519withSHA256KDF", new KDF2BytesGenerator(DigestFactory.createSHA256()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X448withSHA512CKDF
    extends KeyAgreementSpi {
        public X448withSHA512CKDF() {
            super("X448withSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X448withSHA384CKDF
    extends KeyAgreementSpi {
        public X448withSHA384CKDF() {
            super("X448withSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X448withSHA256CKDF
    extends KeyAgreementSpi {
        public X448withSHA256CKDF() {
            super("X448withSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X25519withSHA512CKDF
    extends KeyAgreementSpi {
        public X25519withSHA512CKDF() {
            super("X25519withSHA512CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA512()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X25519withSHA384CKDF
    extends KeyAgreementSpi {
        public X25519withSHA384CKDF() {
            super("X25519withSHA384CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA384()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X25519withSHA256CKDF
    extends KeyAgreementSpi {
        public X25519withSHA256CKDF() {
            super("X25519withSHA256CKDF", new ConcatenationKDFGenerator(DigestFactory.createSHA256()));
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X25519
    extends KeyAgreementSpi {
        public X25519() {
            super("X25519");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class X448
    extends KeyAgreementSpi {
        public X448() {
            super("X448");
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static final class XDH
    extends KeyAgreementSpi {
        public XDH() {
            super("XDH");
        }
    }
}

