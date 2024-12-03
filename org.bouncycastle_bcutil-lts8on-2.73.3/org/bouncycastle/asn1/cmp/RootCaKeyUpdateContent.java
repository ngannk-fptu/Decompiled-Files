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
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cmp;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.CMPCertificate;

public class RootCaKeyUpdateContent
extends ASN1Object {
    private final CMPCertificate newWithNew;
    private final CMPCertificate newWithOld;
    private final CMPCertificate oldWithNew;

    public RootCaKeyUpdateContent(CMPCertificate newWithNew, CMPCertificate newWithOld, CMPCertificate oldWithNew) {
        if (newWithNew == null) {
            throw new NullPointerException("'newWithNew' cannot be null");
        }
        this.newWithNew = newWithNew;
        this.newWithOld = newWithOld;
        this.oldWithNew = oldWithNew;
    }

    private RootCaKeyUpdateContent(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 3) {
            throw new IllegalArgumentException("expected sequence of 1 to 3 elements only");
        }
        CMPCertificate newWithOld = null;
        CMPCertificate oldWithNew = null;
        Iterator encodable = seq.iterator();
        CMPCertificate newWithNew = CMPCertificate.getInstance(encodable.next());
        while (encodable.hasNext()) {
            ASN1TaggedObject ato = ASN1TaggedObject.getInstance(encodable.next());
            if (ato.hasContextTag(0)) {
                newWithOld = CMPCertificate.getInstance(ato, true);
                continue;
            }
            if (!ato.hasContextTag(1)) continue;
            oldWithNew = CMPCertificate.getInstance(ato, true);
        }
        this.newWithNew = newWithNew;
        this.newWithOld = newWithOld;
        this.oldWithNew = oldWithNew;
    }

    public static RootCaKeyUpdateContent getInstance(Object o) {
        if (o instanceof RootCaKeyUpdateContent) {
            return (RootCaKeyUpdateContent)((Object)o);
        }
        if (o != null) {
            return new RootCaKeyUpdateContent(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public CMPCertificate getNewWithNew() {
        return this.newWithNew;
    }

    public CMPCertificate getNewWithOld() {
        return this.newWithOld;
    }

    public CMPCertificate getOldWithNew() {
        return this.oldWithNew;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.newWithNew);
        if (this.newWithOld != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.newWithOld));
        }
        if (this.oldWithNew != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.oldWithNew));
        }
        return new DERSequence(v);
    }
}

