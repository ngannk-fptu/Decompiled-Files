/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.GOST3410NamedParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410ParamSetParameters;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class GOST3410ParameterSpec
implements AlgorithmParameterSpec,
GOST3410Params {
    private GOST3410PublicKeyParameterSetSpec keyParameters;
    private String keyParamSetOID;
    private String digestParamSetOID;
    private String encryptionParamSetOID;

    public GOST3410ParameterSpec(String keyParamSetID, String digestParamSetOID, String encryptionParamSetOID) {
        GOST3410ParamSetParameters ecP;
        block3: {
            ecP = null;
            try {
                ecP = GOST3410NamedParameters.getByOID(new ASN1ObjectIdentifier(keyParamSetID));
            }
            catch (IllegalArgumentException e) {
                ASN1ObjectIdentifier oid = GOST3410NamedParameters.getOID(keyParamSetID);
                if (oid == null) break block3;
                keyParamSetID = oid.getId();
                ecP = GOST3410NamedParameters.getByOID(oid);
            }
        }
        if (ecP == null) {
            throw new IllegalArgumentException("no key parameter set for passed in name/OID.");
        }
        this.keyParameters = new GOST3410PublicKeyParameterSetSpec(ecP.getP(), ecP.getQ(), ecP.getA());
        this.keyParamSetOID = keyParamSetID;
        this.digestParamSetOID = digestParamSetOID;
        this.encryptionParamSetOID = encryptionParamSetOID;
    }

    public GOST3410ParameterSpec(String keyParamSetID, String digestParamSetOID) {
        this(keyParamSetID, digestParamSetOID, null);
    }

    public GOST3410ParameterSpec(String keyParamSetID) {
        this(keyParamSetID, CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId(), null);
    }

    public GOST3410ParameterSpec(GOST3410PublicKeyParameterSetSpec spec) {
        this.keyParameters = spec;
        this.digestParamSetOID = CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet.getId();
        this.encryptionParamSetOID = null;
    }

    @Override
    public String getPublicKeyParamSetOID() {
        return this.keyParamSetOID;
    }

    @Override
    public GOST3410PublicKeyParameterSetSpec getPublicKeyParameters() {
        return this.keyParameters;
    }

    @Override
    public String getDigestParamSetOID() {
        return this.digestParamSetOID;
    }

    @Override
    public String getEncryptionParamSetOID() {
        return this.encryptionParamSetOID;
    }

    public boolean equals(Object o) {
        if (o instanceof GOST3410ParameterSpec) {
            GOST3410ParameterSpec other = (GOST3410ParameterSpec)o;
            return this.keyParameters.equals(other.keyParameters) && this.digestParamSetOID.equals(other.digestParamSetOID) && (this.encryptionParamSetOID == other.encryptionParamSetOID || this.encryptionParamSetOID != null && this.encryptionParamSetOID.equals(other.encryptionParamSetOID));
        }
        return false;
    }

    public int hashCode() {
        return this.keyParameters.hashCode() ^ this.digestParamSetOID.hashCode() ^ (this.encryptionParamSetOID != null ? this.encryptionParamSetOID.hashCode() : 0);
    }

    public static GOST3410ParameterSpec fromPublicKeyAlg(GOST3410PublicKeyAlgParameters params) {
        if (params.getEncryptionParamSet() != null) {
            return new GOST3410ParameterSpec(params.getPublicKeyParamSet().getId(), params.getDigestParamSet().getId(), params.getEncryptionParamSet().getId());
        }
        return new GOST3410ParameterSpec(params.getPublicKeyParamSet().getId(), params.getDigestParamSet().getId());
    }
}

