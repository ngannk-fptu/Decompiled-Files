/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.cms.GenericHybridParameters
 *  org.bouncycastle.asn1.cms.RsaKemParameters
 *  org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x9.X9ObjectIdentifiers
 *  org.bouncycastle.crypto.util.DEROtherInfo
 *  org.bouncycastle.crypto.util.DEROtherInfo$Builder
 *  org.bouncycastle.jcajce.spec.KTSParameterSpec
 *  org.bouncycastle.jcajce.spec.KTSParameterSpec$Builder
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.operator.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.cms.GenericHybridParameters;
import org.bouncycastle.asn1.cms.RsaKemParameters;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.OperatorHelper;
import org.bouncycastle.operator.jcajce.OperatorUtils;
import org.bouncycastle.util.Arrays;

public class JceKTSKeyWrapper
extends AsymmetricKeyWrapper {
    private final String symmetricWrappingAlg;
    private final int keySizeInBits;
    private final byte[] partyUInfo;
    private final byte[] partyVInfo;
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private PublicKey publicKey;
    private SecureRandom random;

    public JceKTSKeyWrapper(PublicKey publicKey, String symmetricWrappingAlg, int keySizeInBits, byte[] partyUInfo, byte[] partyVInfo) {
        super(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_rsa_KEM, (ASN1Encodable)new GenericHybridParameters(new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa, (ASN1Encodable)new RsaKemParameters(new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, (ASN1Encodable)new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)), (keySizeInBits + 7) / 8)), JceSymmetricKeyWrapper.determineKeyEncAlg(symmetricWrappingAlg, keySizeInBits))));
        this.publicKey = publicKey;
        this.symmetricWrappingAlg = symmetricWrappingAlg;
        this.keySizeInBits = keySizeInBits;
        this.partyUInfo = Arrays.clone((byte[])partyUInfo);
        this.partyVInfo = Arrays.clone((byte[])partyVInfo);
    }

    public JceKTSKeyWrapper(X509Certificate certificate, String symmetricWrappingAlg, int keySizeInBits, byte[] partyUInfo, byte[] partyVInfo) {
        this(certificate.getPublicKey(), symmetricWrappingAlg, keySizeInBits, partyUInfo, partyVInfo);
    }

    public JceKTSKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceKTSKeyWrapper setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceKTSKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        Cipher keyEncryptionCipher = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());
        try {
            DEROtherInfo otherInfo = new DEROtherInfo.Builder(JceSymmetricKeyWrapper.determineKeyEncAlg(this.symmetricWrappingAlg, this.keySizeInBits), this.partyUInfo, this.partyVInfo).build();
            KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(this.symmetricWrappingAlg, this.keySizeInBits, otherInfo.getEncoded()).build();
            keyEncryptionCipher.init(3, (Key)this.publicKey, (AlgorithmParameterSpec)ktsSpec, this.random);
            return keyEncryptionCipher.wrap(OperatorUtils.getJceKey(encryptionKey));
        }
        catch (Exception e) {
            throw new OperatorException("Unable to wrap contents key: " + e.getMessage(), e);
        }
    }
}

