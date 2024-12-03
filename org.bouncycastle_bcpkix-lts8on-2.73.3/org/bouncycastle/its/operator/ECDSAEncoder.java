/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.sec.SECObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP256Signature
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP384Signature
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.its.operator;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP256Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP384Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.util.BigIntegers;

public class ECDSAEncoder {
    public static byte[] toX962(Signature signature) {
        byte[] s;
        byte[] r;
        if (signature.getChoice() == 0 || signature.getChoice() == 1) {
            EcdsaP256Signature sig = EcdsaP256Signature.getInstance((Object)signature.getSignature());
            r = ASN1OctetString.getInstance((Object)sig.getRSig().getEccp256CurvePoint()).getOctets();
            s = sig.getSSig().getOctets();
        } else {
            EcdsaP384Signature sig = EcdsaP384Signature.getInstance((Object)signature.getSignature());
            r = ASN1OctetString.getInstance((Object)sig.getRSig().getEccP384CurvePoint()).getOctets();
            s = sig.getSSig().getOctets();
        }
        try {
            return new DERSequence(new ASN1Encodable[]{new ASN1Integer(BigIntegers.fromUnsignedByteArray((byte[])r)), new ASN1Integer(BigIntegers.fromUnsignedByteArray((byte[])s))}).getEncoded();
        }
        catch (IOException ioException) {
            throw new RuntimeException("der encoding r & s");
        }
    }

    public static Signature toITS(ASN1ObjectIdentifier curveID, byte[] dsaEncoding) {
        ASN1Sequence asn1Sig = ASN1Sequence.getInstance((Object)dsaEncoding);
        if (curveID.equals((ASN1Primitive)SECObjectIdentifiers.secp256r1)) {
            return new Signature(0, (ASN1Encodable)new EcdsaP256Signature(new EccP256CurvePoint(0, (ASN1Encodable)new DEROctetString(BigIntegers.asUnsignedByteArray((int)32, (BigInteger)ASN1Integer.getInstance((Object)asn1Sig.getObjectAt(0)).getValue()))), (ASN1OctetString)new DEROctetString(BigIntegers.asUnsignedByteArray((int)32, (BigInteger)ASN1Integer.getInstance((Object)asn1Sig.getObjectAt(1)).getValue()))));
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            return new Signature(1, (ASN1Encodable)new EcdsaP256Signature(new EccP256CurvePoint(0, (ASN1Encodable)new DEROctetString(BigIntegers.asUnsignedByteArray((int)32, (BigInteger)ASN1Integer.getInstance((Object)asn1Sig.getObjectAt(0)).getValue()))), (ASN1OctetString)new DEROctetString(BigIntegers.asUnsignedByteArray((int)32, (BigInteger)ASN1Integer.getInstance((Object)asn1Sig.getObjectAt(1)).getValue()))));
        }
        if (curveID.equals((ASN1Primitive)TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
            return new Signature(2, (ASN1Encodable)new EcdsaP384Signature(new EccP384CurvePoint(0, (ASN1Encodable)new DEROctetString(BigIntegers.asUnsignedByteArray((int)48, (BigInteger)ASN1Integer.getInstance((Object)asn1Sig.getObjectAt(0)).getValue()))), (ASN1OctetString)new DEROctetString(BigIntegers.asUnsignedByteArray((int)48, (BigInteger)ASN1Integer.getInstance((Object)asn1Sig.getObjectAt(1)).getValue()))));
        }
        throw new IllegalArgumentException("unknown curveID");
    }
}

