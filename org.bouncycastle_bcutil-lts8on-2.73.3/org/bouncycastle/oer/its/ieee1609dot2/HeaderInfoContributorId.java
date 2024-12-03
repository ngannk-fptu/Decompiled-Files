/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class HeaderInfoContributorId
extends ASN1Object {
    private final BigInteger contributorId;
    private static final BigInteger MAX = BigInteger.valueOf(255L);

    public HeaderInfoContributorId(long value) {
        this(BigInteger.valueOf(value));
    }

    public HeaderInfoContributorId(BigInteger value) {
        if (value.signum() < 0 && value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("contributor id " + value + " is out of range 0..255");
        }
        this.contributorId = value;
    }

    private HeaderInfoContributorId(ASN1Integer integer) {
        this(integer.getValue());
    }

    public static HeaderInfoContributorId getInstance(Object src) {
        if (src instanceof HeaderInfoContributorId) {
            return (HeaderInfoContributorId)((Object)src);
        }
        if (src != null) {
            return new HeaderInfoContributorId(ASN1Integer.getInstance((Object)src));
        }
        return null;
    }

    public BigInteger getContributorId() {
        return this.contributorId;
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.contributorId);
    }
}

