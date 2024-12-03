/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo
 *  org.bouncycastle.asn1.cms.KEMRecipientInfo
 *  org.bouncycastle.asn1.pkcs.PrivateKeyInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.spec.KTSParameterSpec
 *  org.bouncycastle.jcajce.spec.KTSParameterSpec$Builder
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.JcaJceExtHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.util.Arrays;

class JceCMSKEMKeyUnwrapper
extends AsymmetricKeyUnwrapper {
    private final AlgorithmIdentifier symWrapAlgorithm;
    private final int kekLength;
    private JcaJceExtHelper helper = new DefaultJcaJceExtHelper();
    private Map extraMappings = new HashMap();
    private PrivateKey privateKey;

    public JceCMSKEMKeyUnwrapper(AlgorithmIdentifier symWrapAlg, PrivateKey privateKey) {
        super(PrivateKeyInfo.getInstance((Object)privateKey.getEncoded()).getPrivateKeyAlgorithm());
        KEMRecipientInfo gktParams = KEMRecipientInfo.getInstance((Object)symWrapAlg.getParameters());
        this.privateKey = privateKey;
        this.symWrapAlgorithm = symWrapAlg;
        this.kekLength = CMSUtils.getKekSize(gktParams.getWrap().getAlgorithm());
    }

    public JceCMSKEMKeyUnwrapper setProvider(Provider provider) {
        this.helper = new ProviderJcaJceExtHelper(provider);
        return this;
    }

    public JceCMSKEMKeyUnwrapper setProvider(String providerName) {
        this.helper = new NamedJcaJceExtHelper(providerName);
        return this;
    }

    public JceCMSKEMKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    public int getKekLength() {
        return this.kekLength;
    }

    @Override
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptionKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        KEMRecipientInfo kemInfo = KEMRecipientInfo.getInstance((Object)this.symWrapAlgorithm.getParameters());
        AlgorithmIdentifier symWrapAlgorithm = kemInfo.getWrap();
        try {
            byte[] oriInfoEnc = new CMSORIforKEMOtherInfo(symWrapAlgorithm, this.kekLength, kemInfo.getUkm()).getEncoded();
            if (this.privateKey instanceof RSAPrivateKey) {
                Cipher keyEncryptionCipher = CMSUtils.createAsymmetricWrapper(this.helper, kemInfo.getKem().getAlgorithm(), new HashMap());
                try {
                    String wrapAlgorithmName = CMSUtils.getWrapAlgorithmName(symWrapAlgorithm.getAlgorithm());
                    KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(wrapAlgorithmName, this.kekLength * 8, oriInfoEnc).withKdfAlgorithm(kemInfo.getKdf()).build();
                    keyEncryptionCipher.init(4, (Key)this.privateKey, (AlgorithmParameterSpec)ktsSpec);
                    Key wrapKey = keyEncryptionCipher.unwrap(Arrays.concatenate((byte[])kemInfo.getKemct().getOctets(), (byte[])kemInfo.getEncryptedKey().getOctets()), wrapAlgorithmName, 3);
                    return new JceGenericKey(encryptionKeyAlgorithm, wrapKey);
                }
                catch (Exception e) {
                    throw new OperatorException("Unable to wrap contents key: " + e.getMessage(), e);
                }
            }
            Cipher keyEncryptionCipher = CMSUtils.createAsymmetricWrapper(this.helper, kemInfo.getKem().getAlgorithm(), new HashMap());
            String wrapAlgorithmName = CMSUtils.getWrapAlgorithmName(symWrapAlgorithm.getAlgorithm());
            KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(wrapAlgorithmName, this.kekLength * 8, oriInfoEnc).withKdfAlgorithm(kemInfo.getKdf()).build();
            keyEncryptionCipher.init(4, (Key)this.privateKey, (AlgorithmParameterSpec)ktsSpec);
            Key wrapKey = keyEncryptionCipher.unwrap(Arrays.concatenate((byte[])kemInfo.getKemct().getOctets(), (byte[])kemInfo.getEncryptedKey().getOctets()), wrapAlgorithmName, 3);
            return new JceGenericKey(encryptionKeyAlgorithm, wrapKey);
        }
        catch (Exception e) {
            throw new OperatorException("exception encrypting key: " + e.getMessage(), e);
        }
    }
}

