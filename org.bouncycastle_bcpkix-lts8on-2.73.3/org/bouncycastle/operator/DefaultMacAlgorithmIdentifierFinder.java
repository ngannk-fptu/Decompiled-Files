/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Strings
 */
package org.bouncycastle.operator;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.MacAlgorithmIdentifierFinder;
import org.bouncycastle.util.Strings;

public class DefaultMacAlgorithmIdentifierFinder
implements MacAlgorithmIdentifierFinder {
    private static Map macNameToAlgIds = new HashMap();

    @Override
    public AlgorithmIdentifier find(String macAlgName) {
        return (AlgorithmIdentifier)macNameToAlgIds.get(Strings.toUpperCase((String)macAlgName));
    }

    static {
        macNameToAlgIds.put("HMACSHA1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        macNameToAlgIds.put("HMACSHA224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, (ASN1Encodable)DERNull.INSTANCE));
        macNameToAlgIds.put("HMACSHA256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, (ASN1Encodable)DERNull.INSTANCE));
        macNameToAlgIds.put("HMACSHA384", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, (ASN1Encodable)DERNull.INSTANCE));
        macNameToAlgIds.put("HMACSHA512", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE));
        macNameToAlgIds.put("HMACSHA512-224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512_224, (ASN1Encodable)DERNull.INSTANCE));
        macNameToAlgIds.put("HMACSHA512-256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512_256, (ASN1Encodable)DERNull.INSTANCE));
        macNameToAlgIds.put("HMACSHA3-224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224));
        macNameToAlgIds.put("HMACSHA3-256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256));
        macNameToAlgIds.put("HMACSHA3-384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384));
        macNameToAlgIds.put("HMACSHA3-512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512));
    }
}

