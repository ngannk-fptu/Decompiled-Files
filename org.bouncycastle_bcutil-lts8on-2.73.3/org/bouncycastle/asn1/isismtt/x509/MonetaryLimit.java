/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1PrintableString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERPrintableString
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.isismtt.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;

public class MonetaryLimit
extends ASN1Object {
    ASN1PrintableString currency;
    ASN1Integer amount;
    ASN1Integer exponent;

    public static MonetaryLimit getInstance(Object obj) {
        if (obj == null || obj instanceof MonetaryLimit) {
            return (MonetaryLimit)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new MonetaryLimit(ASN1Sequence.getInstance((Object)obj));
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }

    private MonetaryLimit(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.currency = ASN1PrintableString.getInstance(e.nextElement());
        this.amount = ASN1Integer.getInstance(e.nextElement());
        this.exponent = ASN1Integer.getInstance(e.nextElement());
    }

    public MonetaryLimit(String currency, int amount, int exponent) {
        this.currency = new DERPrintableString(currency, true);
        this.amount = new ASN1Integer((long)amount);
        this.exponent = new ASN1Integer((long)exponent);
    }

    public String getCurrency() {
        return this.currency.getString();
    }

    public BigInteger getAmount() {
        return this.amount.getValue();
    }

    public BigInteger getExponent() {
        return this.exponent.getValue();
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector(3);
        seq.add((ASN1Encodable)this.currency);
        seq.add((ASN1Encodable)this.amount);
        seq.add((ASN1Encodable)this.exponent);
        return new DERSequence(seq);
    }
}

