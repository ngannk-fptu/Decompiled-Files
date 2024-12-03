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
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 *  org.bouncycastle.asn1.x9.X9ECParameters
 *  org.bouncycastle.jcajce.util.DefaultJcaJceHelper
 *  org.bouncycastle.jcajce.util.JcaJceHelper
 *  org.bouncycastle.jcajce.util.NamedJcaJceHelper
 *  org.bouncycastle.jcajce.util.ProviderJcaJceHelper
 *  org.bouncycastle.math.ec.ECCurve
 *  org.bouncycastle.math.ec.ECPoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey
 */
package org.bouncycastle.its.jcajce;

import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.jcajce.ECUtil;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccCurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point256;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Point384;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class JcaITSPublicVerificationKey
extends ITSPublicVerificationKey {
    private final JcaJceHelper helper;

    JcaITSPublicVerificationKey(PublicVerificationKey encryptionKey, JcaJceHelper helper) {
        super(encryptionKey);
        this.helper = helper;
    }

    JcaITSPublicVerificationKey(PublicKey verificationKey, JcaJceHelper helper) {
        super(JcaITSPublicVerificationKey.fromKeyParameters((ECPublicKey)verificationKey));
        this.helper = helper;
    }

    static PublicVerificationKey fromKeyParameters(ECPublicKey pubKey) {
        ASN1ObjectIdentifier curveID = ASN1ObjectIdentifier.getInstance((Object)SubjectPublicKeyInfo.getInstance((Object)pubKey.getEncoded()).getAlgorithm().getParameters());
        if (curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
            return new PublicVerificationKey(0, (ASN1Encodable)EccP256CurvePoint.uncompressedP256((Point256)Point256.builder().setX(pubKey.getW().getAffineX()).setY(pubKey.getW().getAffineY()).createPoint256()));
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new PublicVerificationKey(1, (ASN1Encodable)EccP256CurvePoint.uncompressedP256((Point256)Point256.builder().setX(pubKey.getW().getAffineX()).setY(pubKey.getW().getAffineY()).createPoint256()));
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            return new PublicVerificationKey(2, (ASN1Encodable)EccP384CurvePoint.uncompressedP384((Point384)Point384.builder().setX(pubKey.getW().getAffineX()).setY(pubKey.getW().getAffineY()).createPoint384()));
        }
        throw new IllegalArgumentException("unknown curve in public encryption key");
    }

    public PublicKey getKey() {
        byte[] key;
        X9ECParameters params;
        switch (this.verificationKey.getChoice()) {
            case 0: {
                params = NISTNamedCurves.getByOID((ASN1ObjectIdentifier)SECObjectIdentifiers.secp256r1);
                break;
            }
            case 1: {
                params = TeleTrusTNamedCurves.getByOID((ASN1ObjectIdentifier)TeleTrusTObjectIdentifiers.brainpoolP256r1);
                break;
            }
            case 2: {
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
        org.bouncycastle.math.ec.ECPoint point = curve.decodePoint(key).normalize();
        try {
            KeyFactory keyFactory = this.helper.createKeyFactory("EC");
            ECParameterSpec spec = ECUtil.convertToSpec(params);
            ECPoint jPoint = ECUtil.convertPoint(point);
            return keyFactory.generatePublic(new ECPublicKeySpec(jPoint, spec));
        }
        catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static class Builder {
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String providerName) {
            this.helper = new NamedJcaJceHelper(providerName);
            return this;
        }

        public JcaITSPublicVerificationKey build(PublicVerificationKey verificationKey) {
            return new JcaITSPublicVerificationKey(verificationKey, this.helper);
        }

        public JcaITSPublicVerificationKey build(PublicKey verificationKey) {
            return new JcaITSPublicVerificationKey(verificationKey, this.helper);
        }
    }
}

