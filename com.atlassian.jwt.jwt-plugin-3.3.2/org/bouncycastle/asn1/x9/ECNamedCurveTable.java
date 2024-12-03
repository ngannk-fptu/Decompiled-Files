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
import org.bouncycastle.crypto.ec.CustomNamedCurves;

public class ECNamedCurveTable {
    public static X9ECParameters getByName(String string) {
        X9ECParameters x9ECParameters = X962NamedCurves.getByName(string);
        if (x9ECParameters == null) {
            x9ECParameters = SECNamedCurves.getByName(string);
        }
        if (x9ECParameters == null) {
            x9ECParameters = NISTNamedCurves.getByName(string);
        }
        if (x9ECParameters == null) {
            x9ECParameters = TeleTrusTNamedCurves.getByName(string);
        }
        if (x9ECParameters == null) {
            x9ECParameters = ANSSINamedCurves.getByName(string);
        }
        if (x9ECParameters == null) {
            x9ECParameters = ECGOST3410NamedCurves.getByNameX9(string);
        }
        if (x9ECParameters == null) {
            x9ECParameters = GMNamedCurves.getByName(string);
        }
        return x9ECParameters;
    }

    public static ASN1ObjectIdentifier getOID(String string) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = X962NamedCurves.getOID(string);
        if (aSN1ObjectIdentifier == null) {
            aSN1ObjectIdentifier = SECNamedCurves.getOID(string);
        }
        if (aSN1ObjectIdentifier == null) {
            aSN1ObjectIdentifier = NISTNamedCurves.getOID(string);
        }
        if (aSN1ObjectIdentifier == null) {
            aSN1ObjectIdentifier = TeleTrusTNamedCurves.getOID(string);
        }
        if (aSN1ObjectIdentifier == null) {
            aSN1ObjectIdentifier = ANSSINamedCurves.getOID(string);
        }
        if (aSN1ObjectIdentifier == null) {
            aSN1ObjectIdentifier = ECGOST3410NamedCurves.getOID(string);
        }
        if (aSN1ObjectIdentifier == null) {
            aSN1ObjectIdentifier = GMNamedCurves.getOID(string);
        }
        if (aSN1ObjectIdentifier == null && string.equals("curve25519")) {
            aSN1ObjectIdentifier = CryptlibObjectIdentifiers.curvey25519;
        }
        return aSN1ObjectIdentifier;
    }

    public static String getName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String string = X962NamedCurves.getName(aSN1ObjectIdentifier);
        if (string == null) {
            string = SECNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (string == null) {
            string = NISTNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (string == null) {
            string = TeleTrusTNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (string == null) {
            string = ANSSINamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (string == null) {
            string = ECGOST3410NamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (string == null) {
            string = GMNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (string == null) {
            string = CustomNamedCurves.getName(aSN1ObjectIdentifier);
        }
        return string;
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        X9ECParameters x9ECParameters = X962NamedCurves.getByOID(aSN1ObjectIdentifier);
        if (x9ECParameters == null) {
            x9ECParameters = SECNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = TeleTrusTNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = ANSSINamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = ECGOST3410NamedCurves.getByOIDX9(aSN1ObjectIdentifier);
        }
        if (x9ECParameters == null) {
            x9ECParameters = GMNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        return x9ECParameters;
    }

    public static Enumeration getNames() {
        Vector vector = new Vector();
        ECNamedCurveTable.addEnumeration(vector, X962NamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(vector, SECNamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(vector, NISTNamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(vector, TeleTrusTNamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(vector, ANSSINamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(vector, ECGOST3410NamedCurves.getNames());
        ECNamedCurveTable.addEnumeration(vector, GMNamedCurves.getNames());
        return vector.elements();
    }

    private static void addEnumeration(Vector vector, Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
    }
}

