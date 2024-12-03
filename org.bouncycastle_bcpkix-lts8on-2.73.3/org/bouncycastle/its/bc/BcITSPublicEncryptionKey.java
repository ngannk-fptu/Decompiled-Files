/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.nist.NISTNamedCurves
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x9.X9ECParameters
 *  org.bouncycastle.crypto.params.AsymmetricKeyParameter
 *  org.bouncycastle.crypto.params.ECDomainParameters
 *  org.bouncycastle.crypto.params.ECNamedDomainParameters
 *  org.bouncycastle.crypto.params.ECPublicKeyParameters
 *  org.bouncycastle.math.ec.ECCurve
 *  org.bouncycastle.math.ec.ECPoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm
 */
package org.bouncycastle.its.bc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.its.ITSPublicEncryptionKey;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BasePublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmAlgorithm;

public class BcITSPublicEncryptionKey
extends ITSPublicEncryptionKey {
    public BcITSPublicEncryptionKey(PublicEncryptionKey encryptionKey) {
        super(encryptionKey);
    }

    static PublicEncryptionKey fromKeyParameters(ECPublicKeyParameters pubKey) {
        ASN1ObjectIdentifier curveID = ((ECNamedDomainParameters)pubKey.getParameters()).getName();
        ECPoint q = pubKey.getQ();
        if (curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
            return new PublicEncryptionKey(SymmAlgorithm.aes128Ccm, new BasePublicEncryptionKey.Builder().setChoice(0).setValue((EccCurvePoint)EccP256CurvePoint.uncompressedP256((BigInteger)q.getAffineXCoord().toBigInteger(), (BigInteger)q.getAffineYCoord().toBigInteger())).createBasePublicEncryptionKey());
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new PublicEncryptionKey(SymmAlgorithm.aes128Ccm, new BasePublicEncryptionKey.Builder().setChoice(1).setValue((EccCurvePoint)EccP256CurvePoint.uncompressedP256((BigInteger)q.getAffineXCoord().toBigInteger(), (BigInteger)q.getAffineYCoord().toBigInteger())).createBasePublicEncryptionKey());
        }
        throw new IllegalArgumentException("unknown curve in public encryption key");
    }

    public BcITSPublicEncryptionKey(AsymmetricKeyParameter encryptionKey) {
        super(BcITSPublicEncryptionKey.fromKeyParameters((ECPublicKeyParameters)encryptionKey));
    }

    public AsymmetricKeyParameter getKey() {
        byte[] key;
        X9ECParameters params;
        ASN1ObjectIdentifier curveID;
        BasePublicEncryptionKey baseKey = this.encryptionKey.getPublicKey();
        switch (baseKey.getChoice()) {
            case 0: {
                curveID = SECObjectIdentifiers.secp256r1;
                params = NISTNamedCurves.getByOID((ASN1ObjectIdentifier)SECObjectIdentifiers.secp256r1);
                break;
            }
            case 1: {
                curveID = TeleTrusTObjectIdentifiers.brainpoolP256r1;
                params = TeleTrusTNamedCurves.getByOID((ASN1ObjectIdentifier)TeleTrusTObjectIdentifiers.brainpoolP256r1);
                break;
            }
            default: {
                throw new IllegalStateException("unknown key type");
            }
        }
        ECCurve curve = params.getCurve();
        ASN1Encodable pviCurvePoint = this.encryptionKey.getPublicKey().getBasePublicEncryptionKey();
        if (!(pviCurvePoint instanceof EccCurvePoint)) {
            throw new IllegalStateException("extension to public verification key not supported");
        }
        EccCurvePoint itsPoint = (EccCurvePoint)baseKey.getBasePublicEncryptionKey();
        if (itsPoint instanceof EccP256CurvePoint) {
            key = itsPoint.getEncodedPoint();
        } else if (itsPoint instanceof EccP384CurvePoint) {
            key = itsPoint.getEncodedPoint();
        } else {
            throw new IllegalStateException("unknown key type");
        }
        ECPoint point = curve.decodePoint(key).normalize();
        return new ECPublicKeyParameters(point, (ECDomainParameters)new ECNamedDomainParameters(curveID, params));
    }
}

