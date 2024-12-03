/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.nist;

import java.util.Enumeration;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class NISTNamedCurves {
    static final Hashtable objIds = new Hashtable();
    static final Hashtable names = new Hashtable();

    static void defineCurve(String name, ASN1ObjectIdentifier oid) {
        objIds.put(name, oid);
        names.put(oid, name);
    }

    public static X9ECParameters getByName(String name) {
        ASN1ObjectIdentifier oid = NISTNamedCurves.getOID(name);
        return null != oid ? SECNamedCurves.getByOID(oid) : null;
    }

    public static X9ECParametersHolder getByNameLazy(String name) {
        ASN1ObjectIdentifier oid = NISTNamedCurves.getOID(name);
        return null != oid ? SECNamedCurves.getByOIDLazy(oid) : null;
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier oid) {
        return names.containsKey(oid) ? SECNamedCurves.getByOID(oid) : null;
    }

    public static X9ECParametersHolder getByOIDLazy(ASN1ObjectIdentifier oid) {
        return names.containsKey(oid) ? SECNamedCurves.getByOIDLazy(oid) : null;
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        return (ASN1ObjectIdentifier)objIds.get(Strings.toUpperCase(name));
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return (String)names.get(oid);
    }

    public static Enumeration getNames() {
        return objIds.keys();
    }

    static {
        NISTNamedCurves.defineCurve("B-571", SECObjectIdentifiers.sect571r1);
        NISTNamedCurves.defineCurve("B-409", SECObjectIdentifiers.sect409r1);
        NISTNamedCurves.defineCurve("B-283", SECObjectIdentifiers.sect283r1);
        NISTNamedCurves.defineCurve("B-233", SECObjectIdentifiers.sect233r1);
        NISTNamedCurves.defineCurve("B-163", SECObjectIdentifiers.sect163r2);
        NISTNamedCurves.defineCurve("K-571", SECObjectIdentifiers.sect571k1);
        NISTNamedCurves.defineCurve("K-409", SECObjectIdentifiers.sect409k1);
        NISTNamedCurves.defineCurve("K-283", SECObjectIdentifiers.sect283k1);
        NISTNamedCurves.defineCurve("K-233", SECObjectIdentifiers.sect233k1);
        NISTNamedCurves.defineCurve("K-163", SECObjectIdentifiers.sect163k1);
        NISTNamedCurves.defineCurve("P-521", SECObjectIdentifiers.secp521r1);
        NISTNamedCurves.defineCurve("P-384", SECObjectIdentifiers.secp384r1);
        NISTNamedCurves.defineCurve("P-256", SECObjectIdentifiers.secp256r1);
        NISTNamedCurves.defineCurve("P-224", SECObjectIdentifiers.secp224r1);
        NISTNamedCurves.defineCurve("P-192", SECObjectIdentifiers.secp192r1);
    }
}

