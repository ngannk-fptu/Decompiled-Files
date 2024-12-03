/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.x509.Certificate
 */
package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.Certificate;

public class CscaMasterList
extends ASN1Object {
    private ASN1Integer version = new ASN1Integer(0L);
    private Certificate[] certList;

    public static CscaMasterList getInstance(Object obj) {
        if (obj instanceof CscaMasterList) {
            return (CscaMasterList)((Object)obj);
        }
        if (obj != null) {
            return new CscaMasterList(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CscaMasterList(ASN1Sequence seq) {
        if (seq == null || seq.size() == 0) {
            throw new IllegalArgumentException("null or empty sequence passed.");
        }
        if (seq.size() != 2) {
            throw new IllegalArgumentException("Incorrect sequence size: " + seq.size());
        }
        this.version = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        ASN1Set certSet = ASN1Set.getInstance((Object)seq.getObjectAt(1));
        this.certList = new Certificate[certSet.size()];
        for (int i = 0; i < this.certList.length; ++i) {
            this.certList[i] = Certificate.getInstance((Object)certSet.getObjectAt(i));
        }
    }

    public CscaMasterList(Certificate[] certStructs) {
        this.certList = this.copyCertList(certStructs);
    }

    public int getVersion() {
        return this.version.intValueExact();
    }

    public Certificate[] getCertStructs() {
        return this.copyCertList(this.certList);
    }

    private Certificate[] copyCertList(Certificate[] orig) {
        Certificate[] certs = new Certificate[orig.length];
        for (int i = 0; i != certs.length; ++i) {
            certs[i] = orig[i];
        }
        return certs;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector(2);
        seq.add((ASN1Encodable)this.version);
        seq.add((ASN1Encodable)new DERSet((ASN1Encodable[])this.certList));
        return new DERSequence(seq);
    }
}

