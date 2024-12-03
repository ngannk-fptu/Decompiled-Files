/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.dvcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.dvcs.DVCSCertInfo;
import org.bouncycastle.asn1.dvcs.DVCSErrorNotice;

public class DVCSResponse
extends ASN1Object
implements ASN1Choice {
    private DVCSCertInfo dvCertInfo;
    private DVCSErrorNotice dvErrorNote;

    public DVCSResponse(DVCSCertInfo dvCertInfo) {
        this.dvCertInfo = dvCertInfo;
    }

    public DVCSResponse(DVCSErrorNotice dvErrorNote) {
        this.dvErrorNote = dvErrorNote;
    }

    public static DVCSResponse getInstance(Object obj) {
        if (obj == null || obj instanceof DVCSResponse) {
            return (DVCSResponse)((Object)obj);
        }
        if (obj instanceof byte[]) {
            try {
                return DVCSResponse.getInstance(ASN1Primitive.fromByteArray((byte[])((byte[])obj)));
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + e.getMessage());
            }
        }
        if (obj instanceof ASN1Sequence) {
            DVCSCertInfo dvCertInfo = DVCSCertInfo.getInstance(obj);
            return new DVCSResponse(dvCertInfo);
        }
        if (obj instanceof ASN1TaggedObject) {
            ASN1TaggedObject t = ASN1TaggedObject.getInstance((Object)obj);
            DVCSErrorNotice dvErrorNote = DVCSErrorNotice.getInstance(t, false);
            return new DVCSResponse(dvErrorNote);
        }
        throw new IllegalArgumentException("Couldn't convert from object to DVCSResponse: " + obj.getClass().getName());
    }

    public static DVCSResponse getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DVCSResponse.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public DVCSCertInfo getCertInfo() {
        return this.dvCertInfo;
    }

    public DVCSErrorNotice getErrorNotice() {
        return this.dvErrorNote;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.dvCertInfo != null) {
            return this.dvCertInfo.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, (ASN1Encodable)this.dvErrorNote);
    }

    public String toString() {
        if (this.dvCertInfo != null) {
            return "DVCSResponse {\ndvCertInfo: " + this.dvCertInfo.toString() + "}\n";
        }
        return "DVCSResponse {\ndvErrorNote: " + this.dvErrorNote.toString() + "}\n";
    }
}

