/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

public class ECNamedCurveTable {
    public static ECNamedCurveParameterSpec getParameterSpec(String name) {
        ASN1ObjectIdentifier oid;
        try {
            oid = ECNamedCurveTable.possibleOID(name) ? new ASN1ObjectIdentifier(name) : null;
        }
        catch (IllegalArgumentException e) {
            oid = null;
        }
        X9ECParameters ecP = oid != null ? CustomNamedCurves.getByOID(oid) : CustomNamedCurves.getByName(name);
        if (ecP == null) {
            ecP = oid != null ? org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(oid) : org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(name);
        }
        if (ecP == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(name, ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
    }

    public static Enumeration getNames() {
        return org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
    }

    private static boolean possibleOID(String identifier) {
        if (identifier.length() < 3 || identifier.charAt(1) != '.') {
            return false;
        }
        char first = identifier.charAt(0);
        return first >= '0' && first <= '2';
    }
}

