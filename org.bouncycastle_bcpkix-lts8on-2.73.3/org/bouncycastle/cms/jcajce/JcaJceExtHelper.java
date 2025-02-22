/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 */
package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;

interface JcaJceExtHelper
extends JcaJceHelper {
    public JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier var1, PrivateKey var2);

    public JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier var1, PrivateKey var2, byte[] var3, byte[] var4);

    public SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier var1, SecretKey var2);

    public AsymmetricKeyUnwrapper createKEMUnwrapper(AlgorithmIdentifier var1, PrivateKey var2);
}

