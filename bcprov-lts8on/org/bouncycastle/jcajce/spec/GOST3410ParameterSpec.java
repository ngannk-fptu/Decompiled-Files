/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;

public class GOST3410ParameterSpec
implements AlgorithmParameterSpec {
    private final ASN1ObjectIdentifier publicKeyParamSet;
    private final ASN1ObjectIdentifier digestParamSet;
    private final ASN1ObjectIdentifier encryptionParamSet;

    public GOST3410ParameterSpec(String publicKeyParamSet) {
        this(GOST3410ParameterSpec.getOid(publicKeyParamSet), GOST3410ParameterSpec.getDigestOid(publicKeyParamSet), null);
    }

    public GOST3410ParameterSpec(ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet) {
        this(publicKeyParamSet, digestParamSet, null);
    }

    public GOST3410ParameterSpec(ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet, ASN1ObjectIdentifier encryptionParamSet) {
        this.publicKeyParamSet = publicKeyParamSet;
        this.digestParamSet = digestParamSet;
        this.encryptionParamSet = encryptionParamSet;
    }

    public String getPublicKeyParamSetName() {
        return ECGOST3410NamedCurves.getName(this.getPublicKeyParamSet());
    }

    public ASN1ObjectIdentifier getPublicKeyParamSet() {
        return this.publicKeyParamSet;
    }

    public ASN1ObjectIdentifier getDigestParamSet() {
        return this.digestParamSet;
    }

    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.encryptionParamSet;
    }

    private static ASN1ObjectIdentifier getOid(String paramName) {
        return ECGOST3410NamedCurves.getOID(paramName);
    }

    private static ASN1ObjectIdentifier getDigestOid(String paramName) {
        if (paramName.indexOf("12-512") > 0) {
            return RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512;
        }
        if (paramName.indexOf("12-256") > 0) {
            return RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256;
        }
        return CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet;
    }
}

