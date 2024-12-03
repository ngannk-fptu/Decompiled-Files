/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm
 */
package org.bouncycastle.its;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;

public class ITSAlgorithmUtils {
    private static final Map<Object, HashAlgorithm> algoMap = new HashMap<Object, HashAlgorithm>();

    public static HashAlgorithm getHashAlgorithm(ASN1ObjectIdentifier oid) {
        return algoMap.get(oid);
    }

    static {
        algoMap.put(NISTObjectIdentifiers.id_sha256, HashAlgorithm.sha256);
        algoMap.put(NISTObjectIdentifiers.id_sha384, HashAlgorithm.sha384);
    }
}

