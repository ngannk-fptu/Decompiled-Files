/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.LinkageData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Hostname;

public class CertificateId
extends ASN1Object
implements ASN1Choice {
    public static final int linkageData = 0;
    public static final int name = 1;
    public static final int binaryId = 2;
    public static final int none = 3;
    private final int choice;
    private final ASN1Encodable certificateId;

    public CertificateId(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.certificateId = value;
    }

    private CertificateId(ASN1TaggedObject asn1TaggedObject) {
        this.choice = asn1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.certificateId = LinkageData.getInstance(asn1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.certificateId = Hostname.getInstance(asn1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.certificateId = DEROctetString.getInstance((Object)asn1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 3: {
                this.certificateId = ASN1Null.getInstance((Object)asn1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static CertificateId linkageData(LinkageData linkageData) {
        return new CertificateId(0, (ASN1Encodable)linkageData);
    }

    public static CertificateId name(Hostname hostname) {
        return new CertificateId(1, (ASN1Encodable)hostname);
    }

    public static CertificateId binaryId(ASN1OctetString stream) {
        return new CertificateId(2, (ASN1Encodable)stream);
    }

    public static CertificateId binaryId(byte[] stream) {
        return new CertificateId(2, (ASN1Encodable)new DEROctetString(stream));
    }

    public static CertificateId none() {
        return new CertificateId(3, (ASN1Encodable)DERNull.INSTANCE);
    }

    public static CertificateId getInstance(Object o) {
        if (o instanceof CertificateId) {
            return (CertificateId)((Object)o);
        }
        if (o != null) {
            return new CertificateId(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.certificateId).toASN1Primitive();
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCertificateId() {
        return this.certificateId;
    }
}

