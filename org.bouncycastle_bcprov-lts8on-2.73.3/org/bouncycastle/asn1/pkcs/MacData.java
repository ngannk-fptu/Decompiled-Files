/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.util.Arrays;

public class MacData
extends ASN1Object {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    DigestInfo digInfo;
    byte[] salt;
    BigInteger iterationCount;

    public static MacData getInstance(Object obj) {
        if (obj instanceof MacData) {
            return (MacData)obj;
        }
        if (obj != null) {
            return new MacData(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private MacData(ASN1Sequence seq) {
        this.digInfo = DigestInfo.getInstance(seq.getObjectAt(0));
        this.salt = Arrays.clone(ASN1OctetString.getInstance(seq.getObjectAt(1)).getOctets());
        this.iterationCount = seq.size() == 3 ? ASN1Integer.getInstance(seq.getObjectAt(2)).getValue() : ONE;
    }

    public MacData(DigestInfo digInfo, byte[] salt, int iterationCount) {
        this.digInfo = digInfo;
        this.salt = Arrays.clone(salt);
        this.iterationCount = BigInteger.valueOf(iterationCount);
    }

    public DigestInfo getMac() {
        return this.digInfo;
    }

    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }

    public BigInteger getIterationCount() {
        return this.iterationCount;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add(this.digInfo);
        v.add(new DEROctetString(this.salt));
        if (!this.iterationCount.equals(ONE)) {
            v.add(new ASN1Integer(this.iterationCount));
        }
        return new DERSequence(v);
    }
}

