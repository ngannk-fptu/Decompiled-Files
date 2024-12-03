/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.esf.OcspResponsesID;

public class OcspListID
extends ASN1Object {
    private ASN1Sequence ocspResponses;

    public static OcspListID getInstance(Object obj) {
        if (obj instanceof OcspListID) {
            return (OcspListID)((Object)obj);
        }
        if (obj != null) {
            return new OcspListID(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private OcspListID(ASN1Sequence seq) {
        if (seq.size() != 1) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.ocspResponses = (ASN1Sequence)seq.getObjectAt(0);
        Enumeration e = this.ocspResponses.getObjects();
        while (e.hasMoreElements()) {
            OcspResponsesID.getInstance(e.nextElement());
        }
    }

    public OcspListID(OcspResponsesID[] ocspResponses) {
        this.ocspResponses = new DERSequence((ASN1Encodable[])ocspResponses);
    }

    public OcspResponsesID[] getOcspResponses() {
        OcspResponsesID[] result = new OcspResponsesID[this.ocspResponses.size()];
        for (int idx = 0; idx < result.length; ++idx) {
            result[idx] = OcspResponsesID.getInstance(this.ocspResponses.getObjectAt(idx));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable)this.ocspResponses);
    }
}

