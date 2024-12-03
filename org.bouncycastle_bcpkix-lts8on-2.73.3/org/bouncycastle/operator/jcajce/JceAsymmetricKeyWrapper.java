/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey
 *  org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport
 *  org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.RSAESOAEPparams
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.crypto.CryptoServicesRegistrar
 *  org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec
 *  org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.operator.jcajce;

import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.OperatorHelper;
import org.bouncycastle.operator.jcajce.OperatorUtils;
import org.bouncycastle.util.Arrays;

public class JceAsymmetricKeyWrapper
extends AsymmetricKeyWrapper {
    private static final Set gostAlgs = new HashSet();
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private Map extraMappings = new HashMap();
    private PublicKey publicKey;
    private SecureRandom random;
    private static final Map digests;

    static boolean isGOST(ASN1ObjectIdentifier algorithm) {
        return gostAlgs.contains(algorithm);
    }

    public JceAsymmetricKeyWrapper(PublicKey publicKey) {
        super(SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()).getAlgorithm());
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper(X509Certificate certificate) {
        this(certificate.getPublicKey());
    }

    public JceAsymmetricKeyWrapper(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        super(algorithmIdentifier);
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper(AlgorithmParameters algorithmParams, PublicKey publicKey) throws InvalidParameterSpecException {
        super(JceAsymmetricKeyWrapper.extractAlgID(publicKey, algorithmParams.getParameterSpec(AlgorithmParameterSpec.class)));
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper(AlgorithmParameterSpec algorithmParameterSpec, PublicKey publicKey) {
        super(JceAsymmetricKeyWrapper.extractAlgID(publicKey, algorithmParameterSpec));
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceAsymmetricKeyWrapper setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceAsymmetricKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceAsymmetricKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        byte[] encryptedKeyBytes = null;
        if (JceAsymmetricKeyWrapper.isGOST(this.getAlgorithmIdentifier().getAlgorithm())) {
            try {
                this.random = CryptoServicesRegistrar.getSecureRandom((SecureRandom)this.random);
                KeyPairGenerator kpGen = this.helper.createKeyPairGenerator(this.getAlgorithmIdentifier().getAlgorithm());
                kpGen.initialize(((ECPublicKey)this.publicKey).getParams(), this.random);
                KeyPair ephKp = kpGen.generateKeyPair();
                byte[] ukm = new byte[8];
                this.random.nextBytes(ukm);
                SubjectPublicKeyInfo ephKeyInfo = SubjectPublicKeyInfo.getInstance((Object)ephKp.getPublic().getEncoded());
                GostR3410TransportParameters transParams = ephKeyInfo.getAlgorithm().getAlgorithm().on(RosstandartObjectIdentifiers.id_tc26) ? new GostR3410TransportParameters(RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z, ephKeyInfo, ukm) : new GostR3410TransportParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, ephKeyInfo, ukm);
                KeyAgreement agreement = this.helper.createKeyAgreement(this.getAlgorithmIdentifier().getAlgorithm());
                agreement.init((Key)ephKp.getPrivate(), (AlgorithmParameterSpec)new UserKeyingMaterialSpec(transParams.getUkm()));
                agreement.doPhase(this.publicKey, true);
                SecretKey key = agreement.generateSecret(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId());
                byte[] encKey = OperatorUtils.getJceKey(encryptionKey).getEncoded();
                Cipher keyCipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
                keyCipher.init(3, (Key)key, (AlgorithmParameterSpec)new GOST28147WrapParameterSpec(transParams.getEncryptionParamSet(), transParams.getUkm()));
                byte[] keyData = keyCipher.wrap(new SecretKeySpec(encKey, "GOST"));
                GostR3410KeyTransport transport = new GostR3410KeyTransport(new Gost2814789EncryptedKey(Arrays.copyOfRange((byte[])keyData, (int)0, (int)32), Arrays.copyOfRange((byte[])keyData, (int)32, (int)36)), transParams);
                return transport.getEncoded();
            }
            catch (Exception e) {
                throw new OperatorException("exception wrapping key: " + e.getMessage(), e);
            }
        }
        Cipher keyEncryptionCipher = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
        AlgorithmParameters algParams = null;
        try {
            if (!this.getAlgorithmIdentifier().getAlgorithm().equals((ASN1Primitive)OIWObjectIdentifiers.elGamalAlgorithm)) {
                algParams = this.helper.createAlgorithmParameters(this.getAlgorithmIdentifier());
            }
            if (algParams != null) {
                keyEncryptionCipher.init(3, (Key)this.publicKey, algParams, this.random);
            } else {
                keyEncryptionCipher.init(3, (Key)this.publicKey, this.random);
            }
            encryptedKeyBytes = keyEncryptionCipher.wrap(OperatorUtils.getJceKey(encryptionKey));
        }
        catch (InvalidKeyException ukm) {
        }
        catch (GeneralSecurityException ukm) {
        }
        catch (IllegalStateException ukm) {
        }
        catch (UnsupportedOperationException ukm) {
        }
        catch (ProviderException ukm) {
            // empty catch block
        }
        if (encryptedKeyBytes == null) {
            try {
                if (algParams != null) {
                    keyEncryptionCipher.init(1, (Key)this.publicKey, algParams, this.random);
                } else {
                    keyEncryptionCipher.init(1, (Key)this.publicKey, this.random);
                }
                encryptedKeyBytes = keyEncryptionCipher.doFinal(OperatorUtils.getJceKey(encryptionKey).getEncoded());
            }
            catch (InvalidKeyException e) {
                throw new OperatorException("unable to encrypt contents key", e);
            }
            catch (GeneralSecurityException e) {
                throw new OperatorException("unable to encrypt contents key", e);
            }
        }
        return encryptedKeyBytes;
    }

    private static AlgorithmIdentifier extractAlgID(PublicKey pubKey, AlgorithmParameterSpec algorithmParameterSpec) {
        if (algorithmParameterSpec instanceof OAEPParameterSpec) {
            OAEPParameterSpec oaepSpec = (OAEPParameterSpec)algorithmParameterSpec;
            if (oaepSpec.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
                if (oaepSpec.getPSource() instanceof PSource.PSpecified) {
                    return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, (ASN1Encodable)new RSAESOAEPparams(JceAsymmetricKeyWrapper.getDigest(oaepSpec.getDigestAlgorithm()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, (ASN1Encodable)JceAsymmetricKeyWrapper.getDigest(((MGF1ParameterSpec)oaepSpec.getMGFParameters()).getDigestAlgorithm())), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, (ASN1Encodable)new DEROctetString(((PSource.PSpecified)oaepSpec.getPSource()).getValue()))));
                }
                throw new IllegalArgumentException("unknown PSource: " + oaepSpec.getPSource().getAlgorithm());
            }
            throw new IllegalArgumentException("unknown MGF: " + oaepSpec.getMGFAlgorithm());
        }
        throw new IllegalArgumentException("unknown spec: " + algorithmParameterSpec.getClass().getName());
    }

    private static AlgorithmIdentifier getDigest(String digest) {
        AlgorithmIdentifier algId = (AlgorithmIdentifier)digests.get(digest);
        if (algId != null) {
            return algId;
        }
        throw new IllegalArgumentException("unknown digest name: " + digest);
    }

    static {
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
        digests = new HashMap();
        digests.put("SHA1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-512(224)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
        digests.put("SHA-512(256)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE));
    }
}

