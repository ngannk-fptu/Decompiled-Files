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
import org.bouncycastle.asn1.esf.CrlValidatedID;

public class CrlListID
extends ASN1Object {
    private ASN1Sequence crls;

    public static CrlListID getInstance(Object obj) {
        if (obj instanceof CrlListID) {
            return (CrlListID)((Object)obj);
        }
        if (obj != null) {
            return new CrlListID(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CrlListID(ASN1Sequence seq) {
        this.crls = (ASN1Sequence)seq.getObjectAt(0);
        Enumeration e = this.crls.getObjects();
        while (e.hasMoreElements()) {
            CrlValidatedID.getInstance(e.nextElement());
        }
    }

    public CrlListID(CrlValidatedID[] crls) {
        this.crls = new DERSequence((ASN1Encodable[])crls);
    }

    public CrlValidatedID[] getCrls() {
        CrlValidatedID[] result = new CrlValidatedID[this.crls.size()];
        for (int idx = 0; idx < result.length; ++idx) {
            result[idx] = CrlValidatedID.getInstance(this.crls.getObjectAt(idx));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence((ASN1Encodable)this.crls);
    }
}

