/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.anssi.ANSSINamedCurves;
import org.bouncycastle.asn1.cryptlib.CryptlibObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ECNamedCurveTable {
    public static X9ECParameters getByName(String name) {
        X9ECParameters ecP = X962NamedCurves.getByName(name);
        if (ecP == null) {
            ecP = SECNamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = NISTNamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = TeleTrusTNamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = ANSSINamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = ECGOST3410NamedCurves.getByNameX9(name);
        }
        if (ecP == null) {
            ecP = GMNamedCurves.getByName(name);
        }
        return ecP;
    }

    public static X9ECParametersHolder getByNameLazy(String name) {
        X9ECParametersHolder holder = X962NamedCurves.getByNameLazy(name);
        if (null == holder) {
            holder = SECNamedCurves.getByNameLazy(name);
        }
        if (null == holder) {
            holder = NISTNamedCurves.getByNameLazy(name);
        }
        if (null == holder) {
            holder = TeleTrusTNamedCurves.getByNameLazy(name);
        }
        if (null == holder) {
            holder = ANSSINamedCurves.getByNameLazy(name);
        }
        if (null == holder) {
            holder = ECGOST3410NamedCurves.getByNameLazy(name);
        }
        if (null == holder) {
            holder = GMNamedCurves.getByNameLazy(name);
        }
        return holder;
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        ASN1ObjectIdentifier oid = X962NamedCurves.getOID(name);
        if (oid == null) {
            oid = SECNamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = NISTNamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = TeleTrusTNamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = ANSSINamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = ECGOST3410NamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = GMNamedCurves.getOID(name);
        }
        if (oid == null && name.equals("curve25519")) {
            oid = CryptlibObjectIdentifiers.curvey25519;
        }
        return oid;
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        String name = X962NamedCurves.getName(oid);
        if (name == null) {
            name = SECNamedCurves.getName(oid);
        }
        if (name == null) {
            name = NISTNamedCurves.getName(oid);
        }
        if (name == null) {
            name = TeleTrusTNamedCurves.getName(oid);
        }
        if (name == null) {
            name = ANSSINamedCurves.getName(oid);
        }
        if (name == null) {
            name = ECGOST3410NamedCurves.getName(oid);
        }
        if (name == null) {
            name = GMNamedCurves.getName(oid);
        }
        if (name == null) {
            name = CustomNamedCurves.getName(oid);
        }
        return name;
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier oid) {
        X9ECParameters ecP = X962NamedCurves.getByOID(oid);
        if (ecP == null) {
            ecP = SECNamedCurves.getByOID(oid);
        }
        if (ecP == null) {
            ecP = TeleTrusTNamedCurves.getByOID(oid);
        }
        if (ecP == null) {
            ecP = ANSSINamedCurves.getByOID(oid);
        }
        if (ecP == null) {
            ecP = ECGOST3410NamedCurves.getByOIDX9(oid);
        }
        if (ecP == null) {
            ecP = GMNamedCurves.getByOID(oid);
        }
        return ecP;
    }

    public static X9ECParametersHolder getByOIDLazy(ASN1ObjectIdentifier oid) {
        X9ECParametersHolder holder = X962NamedCurves.getByOIDLazy(oid);
        if (null == holder) {
            holder = SECNamedCurves.getByOIDLazy(oid);
        }
        if (null == holder) {
            holder = TeleTrusTNamedCurves.getByOIDLazy(oid);
        }
        if (null == holder) {
            holder = ANSSINamedCurves.getByOIDLazy(oid);
        }
        if (null == holder) {
            holder = ECGOST3410NamedCurves.getByOIDLazy(oid);
        }
        if (null == holder) {
            holder = GMNamedCurves.getByOIDLazy(oid);
        }
        return holder;
    }

    public static Enumeration getNames() {
        Vector v = new Vector();
        ECNamedCurveTable.addEnumeration(v, X962NamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(v, SECNamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(v, NISTNamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(v, TeleTrusTNamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(v, ANSSINamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(v, ECGOST3410NamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(v, GMNamedCurves.getNames());
        return v.elements();
    }

    private static void addEnumeration(Vector v, Enumeration e) {
        while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
        }
    }
}

