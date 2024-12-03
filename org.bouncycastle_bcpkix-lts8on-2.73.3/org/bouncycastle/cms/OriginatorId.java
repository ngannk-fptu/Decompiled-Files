/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.util.Arrays
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

class OriginatorId
implements Selector {
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;

    public OriginatorId(byte[] subjectKeyId) {
        this.setSubjectKeyID(subjectKeyId);
    }

    private void setSubjectKeyID(byte[] subjectKeyId) {
        this.subjectKeyId = subjectKeyId;
    }

    public OriginatorId(X500Name issuer, BigInteger serialNumber) {
        this.setIssuerAndSerial(issuer, serialNumber);
    }

    private void setIssuerAndSerial(X500Name issuer, BigInteger serialNumber) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
    }

    public OriginatorId(X500Name issuer, BigInteger serialNumber, byte[] subjectKeyId) {
        this.setIssuerAndSerial(issuer, serialNumber);
        this.setSubjectKeyID(subjectKeyId);
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Object clone() {
        return new OriginatorId(this.issuer, this.serialNumber, this.subjectKeyId);
    }

    public int hashCode() {
        int code = Arrays.hashCode((byte[])this.subjectKeyId);
        if (this.serialNumber != null) {
            code ^= this.serialNumber.hashCode();
        }
        if (this.issuer != null) {
            code ^= this.issuer.hashCode();
        }
        return code;
    }

    public boolean equals(Object o) {
        if (!(o instanceof OriginatorId)) {
            return false;
        }
        OriginatorId id = (OriginatorId)o;
        return Arrays.areEqual((byte[])this.subjectKeyId, (byte[])id.subjectKeyId) && this.equalsObj(this.serialNumber, id.serialNumber) && this.equalsObj(this.issuer, id.issuer);
    }

    private boolean equalsObj(Object a, Object b) {
        return a != null ? a.equals(b) : b == null;
    }

    public boolean match(Object obj) {
        return false;
    }
}

