/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.gm;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class GMNamedCurves {
    static X9ECParametersHolder sm2p256v1 = new X9ECParametersHolder(){

        @Override
        protected ECCurve createCurve() {
            BigInteger p = GMNamedCurves.fromHex("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF");
            BigInteger a = GMNamedCurves.fromHex("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC");
            BigInteger b = GMNamedCurves.fromHex("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93");
            BigInteger n = GMNamedCurves.fromHex("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123");
            BigInteger h = BigInteger.valueOf(1L);
            return GMNamedCurves.configureCurve(new ECCurve.Fp(p, a, b, n, h, true));
        }

        @Override
        protected X9ECParameters createParameters() {
            byte[] S = null;
            ECCurve curve = this.getCurve();
            X9ECPoint G = GMNamedCurves.configureBasepoint(curve, "0432C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0");
            return new X9ECParameters(curve, G, curve.getOrder(), curve.getCofactor(), S);
        }
    };
    static X9ECParametersHolder wapip192v1 = new X9ECParametersHolder(){

        @Override
        protected ECCurve createCurve() {
            BigInteger p = GMNamedCurves.fromHex("BDB6F4FE3E8B1D9E0DA8C0D46F4C318CEFE4AFE3B6B8551F");
            BigInteger a = GMNamedCurves.fromHex("BB8E5E8FBC115E139FE6A814FE48AAA6F0ADA1AA5DF91985");
            BigInteger b = GMNamedCurves.fromHex("1854BEBDC31B21B7AEFC80AB0ECD10D5B1B3308E6DBF11C1");
            BigInteger n = GMNamedCurves.fromHex("BDB6F4FE3E8B1D9E0DA8C0D40FC962195DFAE76F56564677");
            BigInteger h = BigInteger.valueOf(1L);
            return GMNamedCurves.configureCurve(new ECCurve.Fp(p, a, b, n, h, true));
        }

        @Override
        protected X9ECParameters createParameters() {
            byte[] S = null;
            ECCurve curve = this.getCurve();
            X9ECPoint G = GMNamedCurves.configureBasepoint(curve, "044AD5F7048DE709AD51236DE65E4D4B482C836DC6E410664002BB3A02D4AAADACAE24817A4CA3A1B014B5270432DB27D2");
            return new X9ECParameters(curve, G, curve.getOrder(), curve.getCofactor(), S);
        }
    };
    static final Hashtable objIds = new Hashtable();
    static final Hashtable curves = new Hashtable();
    static final Hashtable names = new Hashtable();

    private static X9ECPoint configureBasepoint(ECCurve curve, String encoding) {
        X9ECPoint G = new X9ECPoint(curve, Hex.decodeStrict(encoding));
        WNafUtil.configureBasepoint(G.getPoint());
        return G;
    }

    private static ECCurve configureCurve(ECCurve curve) {
        return curve;
    }

    private static BigInteger fromHex(String hex) {
        return new BigInteger(1, Hex.decodeStrict(hex));
    }

    static void defineCurve(String name, ASN1ObjectIdentifier oid, X9ECParametersHolder holder) {
        objIds.put(Strings.toLowerCase(name), oid);
        names.put(oid, name);
        curves.put(oid, holder);
    }

    public static X9ECParameters getByName(String name) {
        ASN1ObjectIdentifier oid = GMNamedCurves.getOID(name);
        return oid == null ? null : GMNamedCurves.getByOID(oid);
    }

    public static X9ECParametersHolder getByNameLazy(String name) {
        ASN1ObjectIdentifier oid = GMNamedCurves.getOID(name);
        return oid == null ? null : GMNamedCurves.getByOIDLazy(oid);
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier oid) {
        X9ECParametersHolder holder = GMNamedCurves.getByOIDLazy(oid);
        return holder == null ? null : holder.getParameters();
    }

    public static X9ECParametersHolder getByOIDLazy(ASN1ObjectIdentifier oid) {
        return (X9ECParametersHolder)curves.get(oid);
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        return (ASN1ObjectIdentifier)objIds.get(Strings.toLowerCase(name));
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return (String)names.get(oid);
    }

    public static Enumeration getNames() {
        return names.elements();
    }

    static {
        GMNamedCurves.defineCurve("wapip192v1", GMObjectIdentifiers.wapip192v1, wapip192v1);
        GMNamedCurves.defineCurve("sm2p256v1", GMObjectIdentifiers.sm2p256v1, sm2p256v1);
    }
}

