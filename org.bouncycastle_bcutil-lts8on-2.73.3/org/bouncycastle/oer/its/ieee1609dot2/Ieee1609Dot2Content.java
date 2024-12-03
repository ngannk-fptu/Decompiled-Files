/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.util.Arrays;

public class Ieee1609Dot2Content
extends ASN1Object
implements ASN1Choice {
    public static final int unsecuredData = 0;
    public static final int signedData = 1;
    public static final int encryptedData = 2;
    public static final int signedCertificateRequest = 3;
    private final int choice;
    private final ASN1Encodable ieee1609Dot2Content;

    public Ieee1609Dot2Content(int choice, ASN1Encodable object) {
        this.choice = choice;
        this.ieee1609Dot2Content = object;
    }

    public static Ieee1609Dot2Content unsecuredData(Opaque value) {
        return new Ieee1609Dot2Content(0, (ASN1Encodable)value);
    }

    public static Ieee1609Dot2Content unsecuredData(byte[] value) {
        return new Ieee1609Dot2Content(0, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])value)));
    }

    public static Ieee1609Dot2Content signedData(SignedData value) {
        return new Ieee1609Dot2Content(1, (ASN1Encodable)value);
    }

    public static Ieee1609Dot2Content encryptedData(EncryptedData value) {
        return new Ieee1609Dot2Content(2, (ASN1Encodable)value);
    }

    public static Ieee1609Dot2Content signedCertificateRequest(Opaque value) {
        return new Ieee1609Dot2Content(3, (ASN1Encodable)value);
    }

    public static Ieee1609Dot2Content signedCertificateRequest(byte[] value) {
        return new Ieee1609Dot2Content(3, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])value)));
    }

    private Ieee1609Dot2Content(ASN1TaggedObject to) {
        this.choice = to.getTagNo();
        switch (this.choice) {
            case 0: 
            case 3: {
                this.ieee1609Dot2Content = Opaque.getInstance(to.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.ieee1609Dot2Content = SignedData.getInstance(to.getExplicitBaseObject());
                return;
            }
            case 2: {
                this.ieee1609Dot2Content = EncryptedData.getInstance(to.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + to.getTagNo());
    }

    public static Ieee1609Dot2Content getInstance(Object src) {
        if (src instanceof Ieee1609Dot2Content) {
            return (Ieee1609Dot2Content)((Object)src);
        }
        if (src != null) {
            return new Ieee1609Dot2Content(ASN1TaggedObject.getInstance((Object)src, (int)128));
        }
        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.ieee1609Dot2Content);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getIeee1609Dot2Content() {
        return this.ieee1609Dot2Content;
    }
}

