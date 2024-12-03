/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.anssi;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.anssi.ANSSIObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class ANSSINamedCurves {
    static X9ECParametersHolder FRP256v1 = new X9ECParametersHolder(){

        protected X9ECParameters createParameters() {
            BigInteger bigInteger = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C03");
            BigInteger bigInteger2 = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C00");
            BigInteger bigInteger3 = ANSSINamedCurves.fromHex("EE353FCA5428A9300D4ABA754A44C00FDFEC0C9AE4B1A1803075ED967B7BB73F");
            byte[] byArray = null;
            BigInteger bigInteger4 = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B53DC67E140D2BF941FFDD459C6D655E1");
            BigInteger bigInteger5 = BigInteger.valueOf(1L);
            ECCurve eCCurve = ANSSINamedCurves.configureCurve(new ECCurve.Fp(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5));
            X9ECPoint x9ECPoint = ANSSINamedCurves.configureBasepoint(eCCurve, "04B6B3D4C356C139EB31183D4749D423958C27D2DCAF98B70164C97A2DD98F5CFF6142E0F7C8B204911F9271F0F3ECEF8C2701C307E8E4C9E183115A1554062CFB");
            return new X9ECParameters(eCCurve, x9ECPoint, bigInteger4, bigInteger5, byArray);
        }
    };
    static final Hashtable objIds = new Hashtable();
    static final Hashtable curves = new Hashtable();
    static final Hashtable names = new Hashtable();

    private static X9ECPoint configureBasepoint(ECCurve eCCurve, String string) {
        X9ECPoint x9ECPoint = new X9ECPoint(eCCurve, Hex.decodeStrict(string));
        WNafUtil.configureBasepoint(x9ECPoint.getPoint());
        return x9ECPoint;
    }

    private static ECCurve configureCurve(ECCurve eCCurve) {
        return eCCurve;
    }

    private static BigInteger fromHex(String string) {
        return new BigInteger(1, Hex.decodeStrict(string));
    }

    static void defineCurve(String string, ASN1ObjectIdentifier aSN1ObjectIdentifier, X9ECParametersHolder x9ECParametersHolder) {
        objIds.put(Strings.toLowerCase(string), aSN1ObjectIdentifier);
        names.put(aSN1ObjectIdentifier, string);
        curves.put(aSN1ObjectIdentifier, x9ECParametersHolder);
    }

    public static X9ECParameters getByName(String string) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = ANSSINamedCurves.getOID(string);
        return aSN1ObjectIdentifier == null ? null : ANSSINamedCurves.getByOID(aSN1ObjectIdentifier);
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        X9ECParametersHolder x9ECParametersHolder = (X9ECParametersHolder)curves.get(aSN1ObjectIdentifier);
        return x9ECParametersHolder == null ? null : x9ECParametersHolder.getParameters();
    }

    public static ASN1ObjectIdentifier getOID(String string) {
        return (ASN1ObjectIdentifier)objIds.get(Strings.toLowerCase(string));
    }

    public static String getName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (String)names.get(aSN1ObjectIdentifier);
    }

    public static Enumeration getNames() {
        return names.elements();
    }

    static /* synthetic */ BigInteger access$000(String string) {
        return ANSSINamedCurves.fromHex(string);
    }

    static /* synthetic */ ECCurve access$100(ECCurve eCCurve) {
        return ANSSINamedCurves.configureCurve(eCCurve);
    }

    static /* synthetic */ X9ECPoint access$200(ECCurve eCCurve, String string) {
        return ANSSINamedCurves.configureBasepoint(eCCurve, string);
    }

    static {
        ANSSINamedCurves.defineCurve("FRP256v1", ANSSIObjectIdentifiers.FRP256v1, FRP256v1);
    }
}

