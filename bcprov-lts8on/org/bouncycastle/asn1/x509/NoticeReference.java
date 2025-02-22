/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DisplayText;

public class NoticeReference
extends ASN1Object {
    private DisplayText organization;
    private ASN1Sequence noticeNumbers;

    private static ASN1EncodableVector convertVector(Vector numbers) {
        ASN1EncodableVector av = new ASN1EncodableVector(numbers.size());
        Enumeration it = numbers.elements();
        while (it.hasMoreElements()) {
            ASN1Integer di;
            Object o = it.nextElement();
            if (o instanceof BigInteger) {
                di = new ASN1Integer((BigInteger)o);
            } else if (o instanceof Integer) {
                di = new ASN1Integer(((Integer)o).intValue());
            } else {
                throw new IllegalArgumentException();
            }
            av.add(di);
        }
        return av;
    }

    public NoticeReference(String organization, Vector numbers) {
        this(organization, NoticeReference.convertVector(numbers));
    }

    public NoticeReference(String organization, ASN1EncodableVector noticeNumbers) {
        this(new DisplayText(organization), noticeNumbers);
    }

    public NoticeReference(DisplayText organization, ASN1EncodableVector noticeNumbers) {
        this.organization = organization;
        this.noticeNumbers = new DERSequence(noticeNumbers);
    }

    private NoticeReference(ASN1Sequence as) {
        if (as.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + as.size());
        }
        this.organization = DisplayText.getInstance(as.getObjectAt(0));
        this.noticeNumbers = ASN1Sequence.getInstance(as.getObjectAt(1));
    }

    public static NoticeReference getInstance(Object as) {
        if (as instanceof NoticeReference) {
            return (NoticeReference)as;
        }
        if (as != null) {
            return new NoticeReference(ASN1Sequence.getInstance(as));
        }
        return null;
    }

    public DisplayText getOrganization() {
        return this.organization;
    }

    public ASN1Integer[] getNoticeNumbers() {
        ASN1Integer[] tmp = new ASN1Integer[this.noticeNumbers.size()];
        for (int i = 0; i != this.noticeNumbers.size(); ++i) {
            tmp[i] = ASN1Integer.getInstance(this.noticeNumbers.getObjectAt(i));
        }
        return tmp;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector av = new ASN1EncodableVector(2);
        av.add(this.organization);
        av.add(this.noticeNumbers);
        return new DERSequence(av);
    }
}

