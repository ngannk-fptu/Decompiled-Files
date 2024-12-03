/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.EncryptedKey;

public class PKIArchiveOptions
extends ASN1Object
implements ASN1Choice {
    public static final int encryptedPrivKey = 0;
    public static final int keyGenParameters = 1;
    public static final int archiveRemGenPrivKey = 2;
    private ASN1Encodable value;

    public static PKIArchiveOptions getInstance(Object o) {
        if (o == null || o instanceof PKIArchiveOptions) {
            return (PKIArchiveOptions)((Object)o);
        }
        if (o instanceof ASN1TaggedObject) {
            return new PKIArchiveOptions(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        throw new IllegalArgumentException("unknown object: " + o);
    }

    private PKIArchiveOptions(ASN1TaggedObject tagged) {
        switch (tagged.getTagNo()) {
            case 0: {
                this.value = EncryptedKey.getInstance(tagged.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.value = ASN1OctetString.getInstance((ASN1TaggedObject)tagged, (boolean)false);
                break;
            }
            case 2: {
                this.value = ASN1Boolean.getInstance((ASN1TaggedObject)tagged, (boolean)false);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag number: " + tagged.getTagNo());
            }
        }
    }

    public PKIArchiveOptions(EncryptedKey encKey) {
        this.value = encKey;
    }

    public PKIArchiveOptions(ASN1OctetString keyGenParameters) {
        this.value = keyGenParameters;
    }

    public PKIArchiveOptions(boolean archiveRemGenPrivKey) {
        this.value = ASN1Boolean.getInstance((boolean)archiveRemGenPrivKey);
    }

    public int getType() {
        if (this.value instanceof EncryptedKey) {
            return 0;
        }
        if (this.value instanceof ASN1OctetString) {
            return 1;
        }
        return 2;
    }

    public ASN1Encodable getValue() {
        return this.value;
    }

    public ASN1Primitive toASN1Primitive() {
        if (this.value instanceof EncryptedKey) {
            return new DERTaggedObject(true, 0, this.value);
        }
        if (this.value instanceof ASN1OctetString) {
            return new DERTaggedObject(false, 1, this.value);
        }
        return new DERTaggedObject(false, 2, this.value);
    }
}

