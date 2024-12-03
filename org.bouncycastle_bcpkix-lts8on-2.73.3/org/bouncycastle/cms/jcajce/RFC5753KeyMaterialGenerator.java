/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Pack
 */
package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.jcajce.KeyMaterialGenerator;
import org.bouncycastle.util.Pack;

class RFC5753KeyMaterialGenerator
implements KeyMaterialGenerator {
    RFC5753KeyMaterialGenerator() {
    }

    @Override
    public byte[] generateKDFMaterial(AlgorithmIdentifier keyAlgorithm, int keySize, byte[] userKeyMaterialParameters) {
        ECCCMSSharedInfo eccInfo = new ECCCMSSharedInfo(keyAlgorithm, userKeyMaterialParameters, Pack.intToBigEndian((int)keySize));
        try {
            return eccInfo.getEncoded("DER");
        }
        catch (IOException e) {
            throw new IllegalStateException("Unable to create KDF material: " + e);
        }
    }
}

