/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Strings;

public class SSHNamedCurves {
    private static final Map<ASN1ObjectIdentifier, String> oidToName;
    private static final Map<String, ASN1ObjectIdentifier> oidMap;
    private static final Map<String, String> curveNameToSSHName;
    private static HashMap<ECCurve, String> curveMap;

    public static ASN1ObjectIdentifier getByName(String sshName) {
        return oidMap.get(sshName);
    }

    public static X9ECParameters getParameters(String sshName) {
        return NISTNamedCurves.getByOID(oidMap.get(Strings.toLowerCase(sshName)));
    }

    public static X9ECParameters getParameters(ASN1ObjectIdentifier oid) {
        return NISTNamedCurves.getByOID(oid);
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return oidToName.get(oid);
    }

    public static String getNameForParameters(ECDomainParameters parameters) {
        if (parameters instanceof ECNamedDomainParameters) {
            return SSHNamedCurves.getName(((ECNamedDomainParameters)parameters).getName());
        }
        return SSHNamedCurves.getNameForParameters(parameters.getCurve());
    }

    public static String getNameForParameters(ECCurve curve) {
        return curveNameToSSHName.get(curveMap.get(curve));
    }

    static {
        oidMap = Collections.unmodifiableMap(new HashMap<String, ASN1ObjectIdentifier>(){
            {
                this.put("nistp256", SECObjectIdentifiers.secp256r1);
                this.put("nistp384", SECObjectIdentifiers.secp384r1);
                this.put("nistp521", SECObjectIdentifiers.secp521r1);
                this.put("nistk163", SECObjectIdentifiers.sect163k1);
                this.put("nistp192", SECObjectIdentifiers.secp192r1);
                this.put("nistp224", SECObjectIdentifiers.secp224r1);
                this.put("nistk233", SECObjectIdentifiers.sect233k1);
                this.put("nistb233", SECObjectIdentifiers.sect233r1);
                this.put("nistk283", SECObjectIdentifiers.sect283k1);
                this.put("nistk409", SECObjectIdentifiers.sect409k1);
                this.put("nistb409", SECObjectIdentifiers.sect409r1);
                this.put("nistt571", SECObjectIdentifiers.sect571k1);
            }
        });
        curveNameToSSHName = Collections.unmodifiableMap(new HashMap<String, String>(){
            {
                String[][] curves = new String[][]{{"secp256r1", "nistp256"}, {"secp384r1", "nistp384"}, {"secp521r1", "nistp521"}, {"sect163k1", "nistk163"}, {"secp192r1", "nistp192"}, {"secp224r1", "nistp224"}, {"sect233k1", "nistk233"}, {"sect233r1", "nistb233"}, {"sect283k1", "nistk283"}, {"sect409k1", "nistk409"}, {"sect409r1", "nistb409"}, {"sect571k1", "nistt571"}};
                for (int i = 0; i != curves.length; ++i) {
                    String[] item = curves[i];
                    this.put(item[0], item[1]);
                }
            }
        });
        curveMap = new HashMap<ECCurve, String>(){
            {
                Enumeration e = CustomNamedCurves.getNames();
                while (e.hasMoreElements()) {
                    String name = (String)e.nextElement();
                    ECCurve curve = CustomNamedCurves.getByNameLazy(name).getCurve();
                    this.put(curve, name);
                }
            }
        };
        oidToName = Collections.unmodifiableMap(new HashMap<ASN1ObjectIdentifier, String>(){
            {
                for (String key : oidMap.keySet()) {
                    this.put(oidMap.get(key), key);
                }
            }
        });
    }
}

