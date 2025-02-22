/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.NoticeReference;

public class UserNotice
extends ASN1Object {
    private final NoticeReference noticeRef;
    private final DisplayText explicitText;

    public UserNotice(NoticeReference noticeRef, DisplayText explicitText) {
        this.noticeRef = noticeRef;
        this.explicitText = explicitText;
    }

    public UserNotice(NoticeReference noticeRef, String str) {
        this(noticeRef, new DisplayText(str));
    }

    private UserNotice(ASN1Sequence as) {
        if (as.size() == 2) {
            this.noticeRef = NoticeReference.getInstance(as.getObjectAt(0));
            this.explicitText = DisplayText.getInstance(as.getObjectAt(1));
        } else if (as.size() == 1) {
            if (as.getObjectAt(0).toASN1Primitive() instanceof ASN1Sequence) {
                this.noticeRef = NoticeReference.getInstance(as.getObjectAt(0));
                this.explicitText = null;
            } else {
                this.explicitText = DisplayText.getInstance(as.getObjectAt(0));
                this.noticeRef = null;
            }
        } else if (as.size() == 0) {
            this.noticeRef = null;
            this.explicitText = null;
        } else {
            throw new IllegalArgumentException("Bad sequence size: " + as.size());
        }
    }

    public static UserNotice getInstance(Object obj) {
        if (obj instanceof UserNotice) {
            return (UserNotice)obj;
        }
        if (obj != null) {
            return new UserNotice(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public NoticeReference getNoticeRef() {
        return this.noticeRef;
    }

    public DisplayText getExplicitText() {
        return this.explicitText;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector av = new ASN1EncodableVector(2);
        if (this.noticeRef != null) {
            av.add(this.noticeRef);
        }
        if (this.explicitText != null) {
            av.add(this.explicitText);
        }
        return new DERSequence(av);
    }
}

