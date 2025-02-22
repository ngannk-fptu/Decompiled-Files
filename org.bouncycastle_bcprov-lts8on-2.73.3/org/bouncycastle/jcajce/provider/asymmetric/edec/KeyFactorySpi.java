/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.util.OpenSSHPrivateKeyUtil;
import org.bouncycastle.crypto.util.OpenSSHPublicKeyUtil;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCXDHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.BaseKeyFactorySpi;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jcajce.spec.OpenSSHPrivateKeySpec;
import org.bouncycastle.jcajce.spec.OpenSSHPublicKeySpec;
import org.bouncycastle.jcajce.spec.RawEncodedKeySpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class KeyFactorySpi
extends BaseKeyFactorySpi
implements AsymmetricKeyInfoConverter {
    static final byte[] x448Prefix = Hex.decode("3042300506032b656f033900");
    static final byte[] x25519Prefix = Hex.decode("302a300506032b656e032100");
    static final byte[] Ed448Prefix = Hex.decode("3043300506032b6571033a00");
    static final byte[] Ed25519Prefix = Hex.decode("302a300506032b6570032100");
    private static final byte x448_type = 111;
    private static final byte x25519_type = 110;
    private static final byte Ed448_type = 113;
    private static final byte Ed25519_type = 112;
    String algorithm;
    private final boolean isXdh;
    private final int specificBase;

    public KeyFactorySpi(String algorithm, boolean isXdh, int specificBase) {
        this.algorithm = algorithm;
        this.isXdh = isXdh;
        this.specificBase = specificBase;
    }

    @Override
    protected Key engineTranslateKey(Key key) throws InvalidKeyException {
        throw new InvalidKeyException("key type unknown");
    }

    @Override
    protected KeySpec engineGetKeySpec(Key key, Class spec) throws InvalidKeySpecException {
        if (spec.isAssignableFrom(OpenSSHPrivateKeySpec.class) && key instanceof BCEdDSAPrivateKey) {
            try {
                ASN1Sequence seq = ASN1Sequence.getInstance(key.getEncoded());
                ASN1OctetString val = ASN1OctetString.getInstance(seq.getObjectAt(2));
                byte[] encoding = ASN1OctetString.getInstance(ASN1Primitive.fromByteArray(val.getOctets())).getOctets();
                return new OpenSSHPrivateKeySpec(OpenSSHPrivateKeyUtil.encodePrivateKey(new Ed25519PrivateKeyParameters(encoding)));
            }
            catch (IOException ex) {
                throw new InvalidKeySpecException(ex.getMessage(), ex.getCause());
            }
        }
        if (spec.isAssignableFrom(OpenSSHPublicKeySpec.class) && key instanceof BCEdDSAPublicKey) {
            try {
                byte[] encoding = key.getEncoded();
                if (!Arrays.areEqual(Ed25519Prefix, 0, Ed25519Prefix.length, encoding, 0, encoding.length - 32)) {
                    throw new InvalidKeySpecException("Invalid Ed25519 public key encoding");
                }
                Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(encoding, Ed25519Prefix.length);
                return new OpenSSHPublicKeySpec(OpenSSHPublicKeyUtil.encodePublicKey(publicKey));
            }
            catch (IOException ex) {
                throw new InvalidKeySpecException(ex.getMessage(), ex.getCause());
            }
        }
        if (spec.isAssignableFrom(RawEncodedKeySpec.class)) {
            if (key instanceof XDHPublicKey) {
                return new RawEncodedKeySpec(((XDHPublicKey)key).getUEncoding());
            }
            if (key instanceof EdDSAPublicKey) {
                return new RawEncodedKeySpec(((EdDSAPublicKey)key).getPointEncoding());
            }
        }
        return super.engineGetKeySpec(key, spec);
    }

    @Override
    protected PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof OpenSSHPrivateKeySpec) {
            AsymmetricKeyParameter parameters = OpenSSHPrivateKeyUtil.parsePrivateKeyBlob(((OpenSSHPrivateKeySpec)keySpec).getEncoded());
            if (parameters instanceof Ed25519PrivateKeyParameters) {
                return new BCEdDSAPrivateKey((Ed25519PrivateKeyParameters)parameters);
            }
            throw new IllegalStateException("openssh private key not Ed25519 private key");
        }
        return super.engineGeneratePrivate(keySpec);
    }

    @Override
    protected PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            byte[] enc = ((X509EncodedKeySpec)keySpec).getEncoded();
            if (this.specificBase == 0 || this.specificBase == enc[8]) {
                if (enc[9] == 5 && enc[10] == 0) {
                    SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(enc);
                    keyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(keyInfo.getAlgorithm().getAlgorithm()), keyInfo.getPublicKeyData().getBytes());
                    try {
                        enc = keyInfo.getEncoded("DER");
                    }
                    catch (IOException e) {
                        throw new InvalidKeySpecException("attempt to reconstruct key failed: " + e.getMessage());
                    }
                }
                switch (enc[8]) {
                    case 111: {
                        return new BCXDHPublicKey(x448Prefix, enc);
                    }
                    case 110: {
                        return new BCXDHPublicKey(x25519Prefix, enc);
                    }
                    case 113: {
                        return new BCEdDSAPublicKey(Ed448Prefix, enc);
                    }
                    case 112: {
                        return new BCEdDSAPublicKey(Ed25519Prefix, enc);
                    }
                }
                return super.engineGeneratePublic(keySpec);
            }
        } else {
            if (keySpec instanceof RawEncodedKeySpec) {
                byte[] enc = ((RawEncodedKeySpec)keySpec).getEncoded();
                switch (this.specificBase) {
                    case 111: {
                        return new BCXDHPublicKey(new X448PublicKeyParameters(enc));
                    }
                    case 110: {
                        return new BCXDHPublicKey(new X25519PublicKeyParameters(enc));
                    }
                    case 113: {
                        return new BCEdDSAPublicKey(new Ed448PublicKeyParameters(enc));
                    }
                    case 112: {
                        return new BCEdDSAPublicKey(new Ed25519PublicKeyParameters(enc));
                    }
                }
                throw new InvalidKeySpecException("factory not a specific type, cannot recognise raw encoding");
            }
            if (keySpec instanceof OpenSSHPublicKeySpec) {
                AsymmetricKeyParameter parameters = OpenSSHPublicKeyUtil.parsePublicKey(((OpenSSHPublicKeySpec)keySpec).getEncoded());
                if (parameters instanceof Ed25519PublicKeyParameters) {
                    return new BCEdDSAPublicKey(new byte[0], ((Ed25519PublicKeyParameters)parameters).getEncoded());
                }
                throw new IllegalStateException("openssh public key not Ed25519 public key");
            }
        }
        return super.engineGeneratePublic(keySpec);
    }

    @Override
    public PrivateKey generatePrivate(PrivateKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getPrivateKeyAlgorithm().getAlgorithm();
        if (this.isXdh) {
            if ((this.specificBase == 0 || this.specificBase == 111) && algOid.equals(EdECObjectIdentifiers.id_X448)) {
                return new BCXDHPrivateKey(keyInfo);
            }
            if ((this.specificBase == 0 || this.specificBase == 110) && algOid.equals(EdECObjectIdentifiers.id_X25519)) {
                return new BCXDHPrivateKey(keyInfo);
            }
        } else if (algOid.equals(EdECObjectIdentifiers.id_Ed448) || algOid.equals(EdECObjectIdentifiers.id_Ed25519)) {
            if ((this.specificBase == 0 || this.specificBase == 113) && algOid.equals(EdECObjectIdentifiers.id_Ed448)) {
                return new BCEdDSAPrivateKey(keyInfo);
            }
            if ((this.specificBase == 0 || this.specificBase == 112) && algOid.equals(EdECObjectIdentifiers.id_Ed25519)) {
                return new BCEdDSAPrivateKey(keyInfo);
            }
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognized");
    }

    @Override
    public PublicKey generatePublic(SubjectPublicKeyInfo keyInfo) throws IOException {
        ASN1ObjectIdentifier algOid = keyInfo.getAlgorithm().getAlgorithm();
        if (this.isXdh) {
            if ((this.specificBase == 0 || this.specificBase == 111) && algOid.equals(EdECObjectIdentifiers.id_X448)) {
                return new BCXDHPublicKey(keyInfo);
            }
            if ((this.specificBase == 0 || this.specificBase == 110) && algOid.equals(EdECObjectIdentifiers.id_X25519)) {
                return new BCXDHPublicKey(keyInfo);
            }
        } else if (algOid.equals(EdECObjectIdentifiers.id_Ed448) || algOid.equals(EdECObjectIdentifiers.id_Ed25519)) {
            if ((this.specificBase == 0 || this.specificBase == 113) && algOid.equals(EdECObjectIdentifiers.id_Ed448)) {
                return new BCEdDSAPublicKey(keyInfo);
            }
            if ((this.specificBase == 0 || this.specificBase == 112) && algOid.equals(EdECObjectIdentifiers.id_Ed25519)) {
                return new BCEdDSAPublicKey(keyInfo);
            }
        }
        throw new IOException("algorithm identifier " + algOid + " in key not recognized");
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class Ed25519
    extends KeyFactorySpi {
        public Ed25519() {
            super("Ed25519", false, 112);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class Ed448
    extends KeyFactorySpi {
        public Ed448() {
            super("Ed448", false, 113);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class EdDSA
    extends KeyFactorySpi {
        public EdDSA() {
            super("EdDSA", false, 0);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X25519
    extends KeyFactorySpi {
        public X25519() {
            super("X25519", true, 110);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class X448
    extends KeyFactorySpi {
        public X448() {
            super("X448", true, 111);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    public static class XDH
    extends KeyFactorySpi {
        public XDH() {
            super("XDH", true, 0);
        }
    }
}

