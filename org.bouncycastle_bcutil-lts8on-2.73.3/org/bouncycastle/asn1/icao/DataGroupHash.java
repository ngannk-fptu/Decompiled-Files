/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.icao;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class DataGroupHash
extends ASN1Object {
    ASN1Integer dataGroupNumber;
    ASN1OctetString dataGroupHashValue;

    public static DataGroupHash getInstance(Object obj) {
        if (obj instanceof DataGroupHash) {
            return (DataGroupHash)((Object)obj);
        }
        if (obj != null) {
            return new DataGroupHash(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private DataGroupHash(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        this.dataGroupNumber = ASN1Integer.getInstance(e.nextElement());
        this.dataGroupHashValue = ASN1OctetString.getInstance(e.nextElement());
    }

    public DataGroupHash(int dataGroupNumber, ASN1OctetString dataGroupHashValue) {
        this.dataGroupNumber = new ASN1Integer((long)dataGroupNumber);
        this.dataGroupHashValue = dataGroupHashValue;
    }

    public int getDataGroupNumber() {
        return this.dataGroupNumber.intValueExact();
    }

    public ASN1OctetString getDataGroupHashValue() {
        return this.dataGroupHashValue;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector(2);
        seq.add((ASN1Encodable)this.dataGroupNumber);
        seq.add((ASN1Encodable)this.dataGroupHashValue);
        return new DERSequence(seq);
    }
}

