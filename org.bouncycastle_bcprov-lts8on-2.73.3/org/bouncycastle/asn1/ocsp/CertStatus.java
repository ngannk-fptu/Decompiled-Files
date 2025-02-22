/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.RevokedInfo;

public class CertStatus
extends ASN1Object
implements ASN1Choice {
    private int tagNo;
    private ASN1Encodable value;

    public CertStatus() {
        this.tagNo = 0;
        this.value = DERNull.INSTANCE;
    }

    public CertStatus(RevokedInfo info) {
        this.tagNo = 1;
        this.value = info;
    }

    public CertStatus(int tagNo, ASN1Encodable value) {
        this.tagNo = tagNo;
        this.value = value;
    }

    private CertStatus(ASN1TaggedObject choice) {
        int tagNo = choice.getTagNo();
        switch (tagNo) {
            case 0: {
                this.value = ASN1Null.getInstance(choice, false);
                break;
            }
            case 1: {
                this.value = RevokedInfo.getInstance(choice, false);
                break;
            }
            case 2: {
                this.value = ASN1Null.getInstance(choice, false);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown tag encountered: " + ASN1Util.getTagText(choice));
            }
        }
        this.tagNo = tagNo;
    }

    public static CertStatus getInstance(Object obj) {
        if (obj == null || obj instanceof CertStatus) {
            return (CertStatus)obj;
        }
        if (obj instanceof ASN1TaggedObject) {
            return new CertStatus((ASN1TaggedObject)obj);
        }
        throw new IllegalArgumentException("unknown object in factory: " + obj.getClass().getName());
    }

    public static CertStatus getInstance(ASN1TaggedObject obj, boolean explicit) {
        return CertStatus.getInstance(obj.getExplicitBaseTagged());
    }

    public int getTagNo() {
        return this.tagNo;
    }

    public ASN1Encodable getStatus() {
        return this.value;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.value);
    }
}

