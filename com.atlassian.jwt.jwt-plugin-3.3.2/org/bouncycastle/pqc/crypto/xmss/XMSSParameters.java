/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.crypto.xmss.DefaultXMSSOid;
import org.bouncycastle.pqc.crypto.xmss.DigestUtil;
import org.bouncycastle.pqc.crypto.xmss.WOTSPlus;
import org.bouncycastle.pqc.crypto.xmss.WOTSPlusParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSOid;
import org.bouncycastle.util.Integers;

public final class XMSSParameters {
    private static final Map<Integer, XMSSParameters> paramsLookupTable;
    private final XMSSOid oid;
    private final int height;
    private final int k;
    private final ASN1ObjectIdentifier treeDigestOID;
    private final int winternitzParameter;
    private final String treeDigest;
    private final int treeDigestSize;
    private final WOTSPlusParameters wotsPlusParams;

    public XMSSParameters(int n, Digest digest) {
        this(n, DigestUtil.getDigestOID(digest.getAlgorithmName()));
    }

    public XMSSParameters(int n, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (n < 2) {
            throw new IllegalArgumentException("height must be >= 2");
        }
        if (aSN1ObjectIdentifier == null) {
            throw new NullPointerException("digest == null");
        }
        this.height = n;
        this.k = this.determineMinK();
        this.treeDigest = DigestUtil.getDigestName(aSN1ObjectIdentifier);
        this.treeDigestOID = aSN1ObjectIdentifier;
        this.wotsPlusParams = new WOTSPlusParameters(aSN1ObjectIdentifier);
        this.treeDigestSize = this.wotsPlusParams.getTreeDigestSize();
        this.winternitzParameter = this.wotsPlusParams.getWinternitzParameter();
        this.oid = DefaultXMSSOid.lookup(this.treeDigest, this.treeDigestSize, this.winternitzParameter, this.wotsPlusParams.getLen(), n);
    }

    private int determineMinK() {
        for (int i = 2; i <= this.height; ++i) {
            if ((this.height - i) % 2 != 0) continue;
            return i;
        }
        throw new IllegalStateException("should never happen...");
    }

    public int getTreeDigestSize() {
        return this.treeDigestSize;
    }

    public ASN1ObjectIdentifier getTreeDigestOID() {
        return this.treeDigestOID;
    }

    public int getHeight() {
        return this.height;
    }

    String getTreeDigest() {
        return this.treeDigest;
    }

    int getLen() {
        return this.wotsPlusParams.getLen();
    }

    int getWinternitzParameter() {
        return this.winternitzParameter;
    }

    WOTSPlus getWOTSPlus() {
        return new WOTSPlus(this.wotsPlusParams);
    }

    XMSSOid getOid() {
        return this.oid;
    }

    int getK() {
        return this.k;
    }

    public static XMSSParameters lookupByOID(int n) {
        return paramsLookupTable.get(Integers.valueOf(n));
    }

    static {
        HashMap<Integer, XMSSParameters> hashMap = new HashMap<Integer, XMSSParameters>();
        hashMap.put(Integers.valueOf(1), new XMSSParameters(10, NISTObjectIdentifiers.id_sha256));
        hashMap.put(Integers.valueOf(2), new XMSSParameters(16, NISTObjectIdentifiers.id_sha256));
        hashMap.put(Integers.valueOf(3), new XMSSParameters(20, NISTObjectIdentifiers.id_sha256));
        hashMap.put(Integers.valueOf(4), new XMSSParameters(10, NISTObjectIdentifiers.id_sha512));
        hashMap.put(Integers.valueOf(5), new XMSSParameters(16, NISTObjectIdentifiers.id_sha512));
        hashMap.put(Integers.valueOf(6), new XMSSParameters(20, NISTObjectIdentifiers.id_sha512));
        hashMap.put(Integers.valueOf(7), new XMSSParameters(10, NISTObjectIdentifiers.id_shake128));
        hashMap.put(Integers.valueOf(8), new XMSSParameters(16, NISTObjectIdentifiers.id_shake128));
        hashMap.put(Integers.valueOf(9), new XMSSParameters(20, NISTObjectIdentifiers.id_shake128));
        hashMap.put(Integers.valueOf(10), new XMSSParameters(10, NISTObjectIdentifiers.id_shake256));
        hashMap.put(Integers.valueOf(11), new XMSSParameters(16, NISTObjectIdentifiers.id_shake256));
        hashMap.put(Integers.valueOf(12), new XMSSParameters(20, NISTObjectIdentifiers.id_shake256));
        paramsLookupTable = Collections.unmodifiableMap(hashMap);
    }
}

