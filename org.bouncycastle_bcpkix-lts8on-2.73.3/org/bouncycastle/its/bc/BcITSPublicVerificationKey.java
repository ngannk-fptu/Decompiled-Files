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
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey
 */
package org.bouncycastle.its.bc;

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
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class BcITSPublicVerificationKey
extends ITSPublicVerificationKey {
    public BcITSPublicVerificationKey(PublicVerificationKey verificationKey) {
        super(verificationKey);
    }

    static PublicVerificationKey fromKeyParameters(ECPublicKeyParameters pubKey) {
        ASN1ObjectIdentifier curveID = ((ECNamedDomainParameters)pubKey.getParameters()).getName();
        ECPoint q = pubKey.getQ();
        if (curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
            return new PublicVerificationKey(0, (ASN1Encodable)EccP256CurvePoint.uncompressedP256((Point256)Point256.builder().setX(q.getAffineXCoord().toBigInteger()).setY(q.getAffineYCoord().toBigInteger()).createPoint256()));
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new PublicVerificationKey(1, (ASN1Encodable)EccP256CurvePoint.uncompressedP256((Point256)Point256.builder().setX(q.getAffineXCoord().toBigInteger()).setY(q.getAffineYCoord().toBigInteger()).createPoint256()));
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            return new PublicVerificationKey(2, (ASN1Encodable)EccP384CurvePoint.uncompressedP384((Point384)Point384.builder().setX(q.getAffineXCoord().toBigInteger()).setY(q.getAffineYCoord().toBigInteger()).createPoint384()));
        }
        throw new IllegalArgumentException("unknown curve in public encryption key");
    }

    public BcITSPublicVerificationKey(AsymmetricKeyParameter verificationKey) {
        super(BcITSPublicVerificationKey.fromKeyParameters((ECPublicKeyParameters)verificationKey));
    }

    public AsymmetricKeyParameter getKey() {
        byte[] key;
        X9ECParameters params;
        ASN1ObjectIdentifier curveID;
        switch (this.verificationKey.getChoice()) {
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
            case 2: {
                curveID = TeleTrusTObjectIdentifiers.brainpoolP384r1;
                params = TeleTrusTNamedCurves.getByOID((ASN1ObjectIdentifier)TeleTrusTObjectIdentifiers.brainpoolP384r1);
                break;
            }
            default: {
                throw new IllegalStateException("unknown key type");
            }
        }
        ECCurve curve = params.getCurve();
        ASN1Encodable pviCurvePoint = this.verificationKey.getPublicVerificationKey();
        if (!(pviCurvePoint instanceof EccCurvePoint)) {
            throw new IllegalStateException("extension to public verification key not supported");
        }
        EccCurvePoint itsPoint = (EccCurvePoint)this.verificationKey.getPublicVerificationKey();
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

