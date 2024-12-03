/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;

public class RecipientInfo
extends ASN1Object
implements ASN1Choice {
    ASN1Encodable info;

    public RecipientInfo(KeyTransRecipientInfo info) {
        this.info = info;
    }

    public RecipientInfo(KeyAgreeRecipientInfo info) {
        this.info = new DERTaggedObject(false, 1, (ASN1Encodable)info);
    }

    public RecipientInfo(KEKRecipientInfo info) {
        this.info = new DERTaggedObject(false, 2, (ASN1Encodable)info);
    }

    public RecipientInfo(PasswordRecipientInfo info) {
        this.info = new DERTaggedObject(false, 3, (ASN1Encodable)info);
    }

    public RecipientInfo(OtherRecipientInfo info) {
        this.info = new DERTaggedObject(false, 4, (ASN1Encodable)info);
    }

    public RecipientInfo(ASN1Primitive info) {
        this.info = info;
    }

    public static RecipientInfo getInstance(Object o) {
        if (o == null || o instanceof RecipientInfo) {
            return (RecipientInfo)((Object)o);
        }
        if (o instanceof ASN1Sequence) {
            return new RecipientInfo((ASN1Primitive)((ASN1Sequence)o));
        }
        if (o instanceof ASN1TaggedObject) {
            return new RecipientInfo((ASN1Primitive)((ASN1TaggedObject)o));
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public ASN1Integer getVersion() {
        if (this.info instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject)this.info;
            switch (o.getTagNo()) {
                case 1: {
                    return KeyAgreeRecipientInfo.getInstance(o, false).getVersion();
                }
                case 2: {
                    return this.getKEKInfo(o).getVersion();
                }
                case 3: {
                    return PasswordRecipientInfo.getInstance(o, false).getVersion();
                }
                case 4: {
                    return new ASN1Integer(0L);
                }
            }
            throw new IllegalStateException("unknown tag");
        }
        return KeyTransRecipientInfo.getInstance(this.info).getVersion();
    }

    public boolean isTagged() {
        return this.info instanceof ASN1TaggedObject;
    }

    public ASN1Encodable getInfo() {
        if (this.info instanceof ASN1TaggedObject) {
            ASN1TaggedObject o = (ASN1TaggedObject)this.info;
            switch (o.getTagNo()) {
                case 1: {
                    return KeyAgreeRecipientInfo.getInstance(o, false);
                }
                case 2: {
                    return this.getKEKInfo(o);
                }
                case 3: {
                    return PasswordRecipientInfo.getInstance(o, false);
                }
                case 4: {
                    return OtherRecipientInfo.getInstance(o, false);
                }
            }
            throw new IllegalStateException("unknown tag");
        }
        return KeyTransRecipientInfo.getInstance(this.info);
    }

    private KEKRecipientInfo getKEKInfo(ASN1TaggedObject o) {
        if (o.isExplicit()) {
            return KEKRecipientInfo.getInstance(o, true);
        }
        return KEKRecipientInfo.getInstance(o, false);
    }

    public ASN1Primitive toASN1Primitive() {
        return this.info.toASN1Primitive();
    }
}

