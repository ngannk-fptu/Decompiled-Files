/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 */
package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.JcaJceExtHelper;
import org.bouncycastle.cms.jcajce.JceCMSKEMKeyUnwrapper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyUnwrapper;

class ProviderJcaJceExtHelper
extends ProviderJcaJceHelper
implements JcaJceExtHelper {
    public ProviderJcaJceExtHelper(Provider provider) {
        super(provider);
    }

    @Override
    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey) {
        keyEncryptionKey = CMSUtils.cleanPrivateKey(keyEncryptionKey);
        return new JceAsymmetricKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(this.provider);
    }

    @Override
    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey, byte[] partyUInfo, byte[] partyVInfo) {
        keyEncryptionKey = CMSUtils.cleanPrivateKey(keyEncryptionKey);
        return new JceKTSKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey, partyUInfo, partyVInfo).setProvider(this.provider);
    }

    @Override
    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, SecretKey keyEncryptionKey) {
        return new JceSymmetricKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(this.provider);
    }

    @Override
    public AsymmetricKeyUnwrapper createKEMUnwrapper(AlgorithmIdentifier keyEncryptionAlgorithm, PrivateKey keyEncryptionKey) {
        return new JceCMSKEMKeyUnwrapper(keyEncryptionAlgorithm, keyEncryptionKey).setProvider(this.provider);
    }
}

