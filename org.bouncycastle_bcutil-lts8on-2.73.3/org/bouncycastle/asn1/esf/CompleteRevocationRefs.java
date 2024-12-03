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
import org.bouncycastle.asn1.esf.CrlOcspRef;

public class CompleteRevocationRefs
extends ASN1Object {
    private ASN1Sequence crlOcspRefs;

    public static CompleteRevocationRefs getInstance(Object obj) {
        if (obj instanceof CompleteRevocationRefs) {
            return (CompleteRevocationRefs)((Object)obj);
        }
        if (obj != null) {
            return new CompleteRevocationRefs(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private CompleteRevocationRefs(ASN1Sequence seq) {
        Enumeration seqEnum = seq.getObjects();
        while (seqEnum.hasMoreElements()) {
            CrlOcspRef.getInstance(seqEnum.nextElement());
        }
        this.crlOcspRefs = seq;
    }

    public CompleteRevocationRefs(CrlOcspRef[] crlOcspRefs) {
        this.crlOcspRefs = new DERSequence((ASN1Encodable[])crlOcspRefs);
    }

    public CrlOcspRef[] getCrlOcspRefs() {
        CrlOcspRef[] result = new CrlOcspRef[this.crlOcspRefs.size()];
        for (int idx = 0; idx < result.length; ++idx) {
            result[idx] = CrlOcspRef.getInstance(this.crlOcspRefs.getObjectAt(idx));
        }
        return result;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.crlOcspRefs;
    }
}

