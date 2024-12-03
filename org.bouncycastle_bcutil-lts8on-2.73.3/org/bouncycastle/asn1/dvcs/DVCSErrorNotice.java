/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.GeneralName;

public class DVCSErrorNotice
extends ASN1Object {
    private PKIStatusInfo transactionStatus;
    private GeneralName transactionIdentifier;

    public DVCSErrorNotice(PKIStatusInfo status) {
        this(status, null);
    }

    public DVCSErrorNotice(PKIStatusInfo status, GeneralName transactionIdentifier) {
        this.transactionStatus = status;
        this.transactionIdentifier = transactionIdentifier;
    }

    private DVCSErrorNotice(ASN1Sequence seq) {
        this.transactionStatus = PKIStatusInfo.getInstance(seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.transactionIdentifier = GeneralName.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public static DVCSErrorNotice getInstance(Object obj) {
        if (obj instanceof DVCSErrorNotice) {
            return (DVCSErrorNotice)((Object)obj);
        }
        if (obj != null) {
            return new DVCSErrorNotice(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static DVCSErrorNotice getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DVCSErrorNotice.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.transactionStatus);
        if (this.transactionIdentifier != null) {
            v.add((ASN1Encodable)this.transactionIdentifier);
        }
        return new DERSequence(v);
    }

    public String toString() {
        return "DVCSErrorNotice {\ntransactionStatus: " + (Object)((Object)this.transactionStatus) + "\n" + (this.transactionIdentifier != null ? "transactionIdentifier: " + this.transactionIdentifier + "\n" : "") + "}\n";
    }

    public PKIStatusInfo getTransactionStatus() {
        return this.transactionStatus;
    }

    public GeneralName getTransactionIdentifier() {
        return this.transactionIdentifier;
    }
}

