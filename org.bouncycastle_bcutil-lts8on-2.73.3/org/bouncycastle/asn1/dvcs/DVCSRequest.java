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
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.x509.GeneralName;

public class DVCSRequest
extends ASN1Object {
    private DVCSRequestInformation requestInformation;
    private Data data;
    private GeneralName transactionIdentifier;

    public DVCSRequest(DVCSRequestInformation requestInformation, Data data) {
        this(requestInformation, data, null);
    }

    public DVCSRequest(DVCSRequestInformation requestInformation, Data data, GeneralName transactionIdentifier) {
        this.requestInformation = requestInformation;
        this.data = data;
        this.transactionIdentifier = transactionIdentifier;
    }

    private DVCSRequest(ASN1Sequence seq) {
        this.requestInformation = DVCSRequestInformation.getInstance(seq.getObjectAt(0));
        this.data = Data.getInstance(seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.transactionIdentifier = GeneralName.getInstance((Object)seq.getObjectAt(2));
        }
    }

    public static DVCSRequest getInstance(Object obj) {
        if (obj instanceof DVCSRequest) {
            return (DVCSRequest)((Object)obj);
        }
        if (obj != null) {
            return new DVCSRequest(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static DVCSRequest getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DVCSRequest.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.requestInformation);
        v.add((ASN1Encodable)this.data);
        if (this.transactionIdentifier != null) {
            v.add((ASN1Encodable)this.transactionIdentifier);
        }
        return new DERSequence(v);
    }

    public String toString() {
        return "DVCSRequest {\nrequestInformation: " + (Object)((Object)this.requestInformation) + "\ndata: " + (Object)((Object)this.data) + "\n" + (this.transactionIdentifier != null ? "transactionIdentifier: " + this.transactionIdentifier + "\n" : "") + "}\n";
    }

    public Data getData() {
        return this.data;
    }

    public DVCSRequestInformation getRequestInformation() {
        return this.requestInformation;
    }

    public GeneralName getTransactionIdentifier() {
        return this.transactionIdentifier;
    }
}

