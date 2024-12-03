/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.SubsequentMessage;

public class POPOPrivKey
extends ASN1Object
implements ASN1Choice {
    public static final int thisMessage = 0;
    public static final int subsequentMessage = 1;
    public static final int dhMAC = 2;
    public static final int agreeMAC = 3;
    public static final int encryptedKey = 4;
    private int tagNo;
    private ASN1Encodable obj;

    private POPOPrivKey(ASN1TaggedObject obj) {
        this.tagNo = obj.getTagNo();
        switch (this.tagNo) {
            case 0: {
                this.obj = ASN1BitString.getInstance((ASN1TaggedObject)obj, (boolean)false);
                break;
            }
            case 1: {
                this.obj = SubsequentMessage.valueOf(ASN1Integer.getInstance((ASN1TaggedObject)obj, (boolean)false).intValueExact());
                break;
            }
            case 2: {
                this.obj = ASN1BitString.getInstance((ASN1TaggedObject)obj, (boolean)false);
                break;
            }
            case 3: {
                this.obj = PKMACValue.getInstance(obj, false);
                break;
            }
            case 4: {
                this.obj = EnvelopedData.getInstance(obj, false);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag in POPOPrivKey");
            }
        }
    }

    public static POPOPrivKey getInstance(Object obj) {
        if (obj instanceof POPOPrivKey) {
            return (POPOPrivKey)((Object)obj);
        }
        if (obj != null) {
            return new POPOPrivKey(ASN1TaggedObject.getInstance((Object)obj));
        }
        return null;
    }

    public static POPOPrivKey getInstance(ASN1TaggedObject obj, boolean explicit) {
        return POPOPrivKey.getInstance(ASN1TaggedObject.getInstance((ASN1TaggedObject)obj, (boolean)true));
    }

    public POPOPrivKey(PKMACValue agreeMac) {
        this.tagNo = 3;
        this.obj = agreeMac;
    }

    public POPOPrivKey(SubsequentMessage msg) {
        this.tagNo = 1;
        this.obj = msg;
    }

    public int getType() {
        return this.tagNo;
    }

    public ASN1Encodable getValue() {
        return this.obj;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.obj);
    }
}

