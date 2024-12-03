/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.DisplayText
 *  org.bouncycastle.asn1.x509.NoticeReference
 */
package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.NoticeReference;

public class SPUserNotice
extends ASN1Object {
    private NoticeReference noticeRef;
    private DisplayText explicitText;

    public static SPUserNotice getInstance(Object obj) {
        if (obj instanceof SPUserNotice) {
            return (SPUserNotice)((Object)obj);
        }
        if (obj != null) {
            return new SPUserNotice(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private SPUserNotice(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            ASN1Encodable object = (ASN1Encodable)e.nextElement();
            if (object instanceof DisplayText || object instanceof ASN1String) {
                this.explicitText = DisplayText.getInstance((Object)object);
                continue;
            }
            if (object instanceof NoticeReference || object instanceof ASN1Sequence) {
                this.noticeRef = NoticeReference.getInstance((Object)object);
                continue;
            }
            throw new IllegalArgumentException("Invalid element in 'SPUserNotice': " + object.getClass().getName());
        }
    }

    public SPUserNotice(NoticeReference noticeRef, DisplayText explicitText) {
        this.noticeRef = noticeRef;
        this.explicitText = explicitText;
    }

    public NoticeReference getNoticeRef() {
        return this.noticeRef;
    }

    public DisplayText getExplicitText() {
        return this.explicitText;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.noticeRef != null) {
            v.add((ASN1Encodable)this.noticeRef);
        }
        if (this.explicitText != null) {
            v.add((ASN1Encodable)this.explicitText);
        }
        return new DERSequence(v);
    }
}

