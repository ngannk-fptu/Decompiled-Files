/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.JcaJceExtHelper;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceSymmetricKeyUnwrapper;

class DefaultJcaJceExtHelper
extends DefaultJcaJceHelper
implements JcaJceExtHelper {
    DefaultJcaJceExtHelper() {
    }

    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey) {
        privateKey = CMSUtils.cleanPrivateKey(privateKey);
        return new JceAsymmetricKeyUnwrapper(algorithmIdentifier, privateKey);
    }

    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey, byte[] byArray, byte[] byArray2) {
        privateKey = CMSUtils.cleanPrivateKey(privateKey);
        return new JceKTSKeyUnwrapper(algorithmIdentifier, privateKey, byArray, byArray2);
    }

    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier algorithmIdentifier, SecretKey secretKey) {
        return new JceSymmetricKeyUnwrapper(algorithmIdentifier, secretKey);
    }
}

