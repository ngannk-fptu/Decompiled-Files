/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.CRLReason
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.cmc;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.util.Arrays;

public class RevokeRequest
extends ASN1Object {
    private final X500Name name;
    private final ASN1Integer serialNumber;
    private final CRLReason reason;
    private ASN1GeneralizedTime invalidityDate;
    private ASN1OctetString passphrase;
    private ASN1UTF8String comment;

    public RevokeRequest(X500Name name, ASN1Integer serialNumber, CRLReason reason, ASN1GeneralizedTime invalidityDate, ASN1OctetString passphrase, ASN1UTF8String comment) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.reason = reason;
        this.invalidityDate = invalidityDate;
        this.passphrase = passphrase;
        this.comment = comment;
    }

    private RevokeRequest(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 6) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.name = X500Name.getInstance((Object)seq.getObjectAt(0));
        this.serialNumber = ASN1Integer.getInstance((Object)seq.getObjectAt(1));
        this.reason = CRLReason.getInstance((Object)seq.getObjectAt(2));
        int index = 3;
        if (seq.size() > index && seq.getObjectAt(index).toASN1Primitive() instanceof ASN1GeneralizedTime) {
            this.invalidityDate = ASN1GeneralizedTime.getInstance((Object)seq.getObjectAt(index++));
        }
        if (seq.size() > index && seq.getObjectAt(index).toASN1Primitive() instanceof ASN1OctetString) {
            this.passphrase = ASN1OctetString.getInstance((Object)seq.getObjectAt(index++));
        }
        if (seq.size() > index && seq.getObjectAt(index).toASN1Primitive() instanceof ASN1UTF8String) {
            this.comment = ASN1UTF8String.getInstance((Object)seq.getObjectAt(index));
        }
    }

    public static RevokeRequest getInstance(Object o) {
        if (o instanceof RevokeRequest) {
            return (RevokeRequest)((Object)o);
        }
        if (o != null) {
            return new RevokeRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public X500Name getName() {
        return this.name;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber.getValue();
    }

    public CRLReason getReason() {
        return this.reason;
    }

    public ASN1GeneralizedTime getInvalidityDate() {
        return this.invalidityDate;
    }

    public void setInvalidityDate(ASN1GeneralizedTime invalidityDate) {
        this.invalidityDate = invalidityDate;
    }

    public ASN1OctetString getPassphrase() {
        return this.passphrase;
    }

    public void setPassphrase(ASN1OctetString passphrase) {
        this.passphrase = passphrase;
    }

    public ASN1UTF8String getCommentUTF8() {
        return this.comment;
    }

    public void setComment(ASN1UTF8String comment) {
        this.comment = comment;
    }

    public byte[] getPassPhrase() {
        if (this.passphrase != null) {
            return Arrays.clone((byte[])this.passphrase.getOctets());
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(6);
        v.add((ASN1Encodable)this.name);
        v.add((ASN1Encodable)this.serialNumber);
        v.add((ASN1Encodable)this.reason);
        if (this.invalidityDate != null) {
            v.add((ASN1Encodable)this.invalidityDate);
        }
        if (this.passphrase != null) {
            v.add((ASN1Encodable)this.passphrase);
        }
        if (this.comment != null) {
            v.add((ASN1Encodable)this.comment);
        }
        return new DERSequence(v);
    }
}

