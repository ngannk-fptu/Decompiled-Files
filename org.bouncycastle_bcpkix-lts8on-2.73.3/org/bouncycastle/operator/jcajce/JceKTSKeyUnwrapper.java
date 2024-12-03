/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.GenericHybridParameters
 *  org.bouncycastle.asn1.cms.RsaKemParameters
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
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
import java.security.PrivateKey;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.cms.GenericHybridParameters;
import org.bouncycastle.asn1.cms.RsaKemParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.operator.jcajce.OperatorHelper;
import org.bouncycastle.util.Arrays;

public class JceKTSKeyUnwrapper
extends AsymmetricKeyUnwrapper {
    private OperatorHelper helper = new OperatorHelper((JcaJceHelper)new DefaultJcaJceHelper());
    private Map extraMappings = new HashMap();
    private PrivateKey privKey;
    private byte[] partyUInfo;
    private byte[] partyVInfo;

    public JceKTSKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privKey, byte[] partyUInfo, byte[] partyVInfo) {
        super(algorithmIdentifier);
        this.privKey = privKey;
        this.partyUInfo = Arrays.clone((byte[])partyUInfo);
        this.partyVInfo = Arrays.clone((byte[])partyVInfo);
    }

    public JceKTSKeyUnwrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper((JcaJceHelper)new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceKTSKeyUnwrapper setProvider(String providerName) {
        this.helper = new OperatorHelper((JcaJceHelper)new NamedJcaJceHelper(providerName));
        return this;
    }

    @Override
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        Key sKey;
        GenericHybridParameters params = GenericHybridParameters.getInstance((Object)this.getAlgorithmIdentifier().getParameters());
        Cipher keyCipher = this.helper.createAsymmetricWrapper(this.getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
        String symmetricWrappingAlg = this.helper.getWrappingAlgorithmName(params.getDem().getAlgorithm());
        RsaKemParameters kemParameters = RsaKemParameters.getInstance((Object)params.getKem().getParameters());
        int keySizeInBits = kemParameters.getKeyLength().intValue() * 8;
        try {
            DEROtherInfo otherInfo = new DEROtherInfo.Builder(params.getDem(), this.partyUInfo, this.partyVInfo).build();
            KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(symmetricWrappingAlg, keySizeInBits, otherInfo.getEncoded()).withKdfAlgorithm(kemParameters.getKeyDerivationFunction()).build();
            keyCipher.init(4, (Key)this.privKey, (AlgorithmParameterSpec)ktsSpec);
            sKey = keyCipher.unwrap(encryptedKey, this.helper.getKeyAlgorithmName(encryptedKeyAlgorithm.getAlgorithm()), 3);
        }
        catch (Exception e) {
            throw new OperatorException("Unable to unwrap contents key: " + e.getMessage(), e);
        }
        return new JceGenericKey(encryptedKeyAlgorithm, sKey);
    }
}

