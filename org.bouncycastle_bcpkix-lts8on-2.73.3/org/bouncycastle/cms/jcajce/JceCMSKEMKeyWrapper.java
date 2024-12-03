/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo
 *  org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.jcajce.spec.KTSParameterSpec
 *  org.bouncycastle.jcajce.spec.KTSParameterSpec$Builder
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.KEMKeyWrapper;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.JcaJceExtHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

class JceCMSKEMKeyWrapper
extends KEMKeyWrapper {
    private final AlgorithmIdentifier symWrapAlgorithm;
    private final int kekLength;
    private JcaJceExtHelper helper = new DefaultJcaJceExtHelper();
    private Map extraMappings = new HashMap();
    private PublicKey publicKey;
    private SecureRandom random;
    private AlgorithmIdentifier kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, (ASN1Encodable)new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, (ASN1Encodable)DERNull.INSTANCE));
    private byte[] encapsulation;
    private static Map encLengths = new HashMap();

    public JceCMSKEMKeyWrapper(PublicKey publicKey, ASN1ObjectIdentifier symWrapAlg) {
        super(publicKey instanceof RSAPublicKey ? new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa) : SubjectPublicKeyInfo.getInstance((Object)publicKey.getEncoded()).getAlgorithm());
        this.publicKey = publicKey;
        this.symWrapAlgorithm = new AlgorithmIdentifier(symWrapAlg);
        this.kekLength = CMSUtils.getKekSize(symWrapAlg);
    }

    public JceCMSKEMKeyWrapper setProvider(Provider provider) {
        this.helper = new ProviderJcaJceExtHelper(provider);
        return this;
    }

    public JceCMSKEMKeyWrapper setProvider(String providerName) {
        this.helper = new NamedJcaJceExtHelper(providerName);
        return this;
    }

    public JceCMSKEMKeyWrapper setKDF(AlgorithmIdentifier kdfAlgorithm) {
        this.kdfAlgorithm = kdfAlgorithm;
        return this;
    }

    public JceCMSKEMKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceCMSKEMKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    @Override
    public byte[] getEncapsulation() {
        return this.encapsulation;
    }

    @Override
    public AlgorithmIdentifier getKdfAlgorithmIdentifier() {
        return this.kdfAlgorithm;
    }

    @Override
    public int getKekLength() {
        return this.kekLength;
    }

    @Override
    public AlgorithmIdentifier getWrapAlgorithmIdentifier() {
        return this.symWrapAlgorithm;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        try {
            byte[] oriInfoEnc = new CMSORIforKEMOtherInfo(this.symWrapAlgorithm, this.kekLength).getEncoded();
            if (this.publicKey instanceof RSAPublicKey) {
                Cipher keyEncryptionCipher = CMSUtils.createAsymmetricWrapper(this.helper, this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());
                try {
                    KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, oriInfoEnc).withKdfAlgorithm(this.kdfAlgorithm).build();
                    keyEncryptionCipher.init(3, (Key)this.publicKey, (AlgorithmParameterSpec)ktsSpec, this.random);
                    byte[] encWithKey = keyEncryptionCipher.wrap(CMSUtils.getJceKey(encryptionKey));
                    int modLength = (((RSAPublicKey)this.publicKey).getModulus().bitLength() + 7) / 8;
                    this.encapsulation = Arrays.copyOfRange((byte[])encWithKey, (int)0, (int)modLength);
                    return Arrays.copyOfRange((byte[])encWithKey, (int)modLength, (int)encWithKey.length);
                }
                catch (Exception e) {
                    throw new OperatorException("Unable to wrap contents key: " + e.getMessage(), e);
                }
            }
            Cipher keyEncryptionCipher = CMSUtils.createAsymmetricWrapper(this.helper, this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());
            try {
                KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, oriInfoEnc).withKdfAlgorithm(this.kdfAlgorithm).build();
                keyEncryptionCipher.init(3, (Key)this.publicKey, (AlgorithmParameterSpec)ktsSpec, this.random);
                byte[] encWithKey = keyEncryptionCipher.wrap(CMSUtils.getJceKey(encryptionKey));
                int encLength = this.getKemEncLength(this.publicKey);
                this.encapsulation = Arrays.copyOfRange((byte[])encWithKey, (int)0, (int)encLength);
                return Arrays.copyOfRange((byte[])encWithKey, (int)encLength, (int)encWithKey.length);
            }
            catch (Exception e) {
                throw new OperatorException("Unable to wrap contents key: " + e.getMessage(), e);
            }
        }
        catch (Exception e) {
            throw new OperatorException("unable to wrap contents key: " + e.getMessage(), e);
        }
    }

    private int getKemEncLength(PublicKey publicKey) {
        return 0;
    }
}

