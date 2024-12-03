/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class BitmapSspRange
extends ASN1Object {
    private final ASN1OctetString sspValue;
    private final ASN1OctetString sspBitMask;

    public BitmapSspRange(ASN1OctetString sspValue, ASN1OctetString sspBitmask) {
        this.sspValue = sspValue;
        this.sspBitMask = sspBitmask;
    }

    private BitmapSspRange(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        Iterator it = seq.iterator();
        this.sspValue = ASN1OctetString.getInstance(it.next());
        this.sspBitMask = ASN1OctetString.getInstance(it.next());
    }

    public static BitmapSspRange getInstance(Object o) {
        if (o instanceof BitmapSspRange) {
            return (BitmapSspRange)((Object)o);
        }
        if (o != null) {
            return new BitmapSspRange(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getSspValue() {
        return this.sspValue;
    }

    public ASN1OctetString getSspBitMask() {
        return this.sspBitMask;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.sspValue, this.sspBitMask});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString sspValue;
        private ASN1OctetString sspBitMask;

        public Builder setSspValue(ASN1OctetString sspValue) {
            this.sspValue = sspValue;
            return this;
        }

        public Builder setSspBitMask(ASN1OctetString sspBitMask) {
            this.sspBitMask = sspBitMask;
            return this;
        }

        public Builder setSspValue(byte[] sspValue) {
            this.sspValue = new DEROctetString(Arrays.clone((byte[])sspValue));
            return this;
        }

        public Builder setSspBitMask(byte[] sspBitmask) {
            this.sspBitMask = new DEROctetString(Arrays.clone((byte[])sspBitmask));
            return this;
        }

        public BitmapSspRange createBitmapSspRange() {
            return new BitmapSspRange(this.sspValue, this.sspBitMask);
        }
    }
}

