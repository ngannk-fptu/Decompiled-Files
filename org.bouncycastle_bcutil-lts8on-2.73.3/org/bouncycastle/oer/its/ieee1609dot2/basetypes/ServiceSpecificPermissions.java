/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BitmapSsp;

public class ServiceSpecificPermissions
extends ASN1Object
implements ASN1Choice {
    public static final int opaque = 0;
    public static final int bitmapSsp = 1;
    private final int choice;
    private final ASN1Encodable serviceSpecificPermissions;

    public ServiceSpecificPermissions(int choice, ASN1Encodable object) {
        this.choice = choice;
        this.serviceSpecificPermissions = object;
    }

    private ServiceSpecificPermissions(ASN1TaggedObject sto) {
        this.choice = sto.getTagNo();
        switch (this.choice) {
            case 0: {
                this.serviceSpecificPermissions = Opaque.getInstance(sto.getExplicitBaseObject());
                return;
            }
            case 1: {
                this.serviceSpecificPermissions = BitmapSsp.getInstance(sto.getExplicitBaseObject());
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + this.choice);
    }

    public static ServiceSpecificPermissions getInstance(Object o) {
        if (o instanceof ServiceSpecificPermissions) {
            return (ServiceSpecificPermissions)((Object)o);
        }
        if (o != null) {
            return new ServiceSpecificPermissions(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public static ServiceSpecificPermissions opaque(ASN1OctetString octetString) {
        return new ServiceSpecificPermissions(0, (ASN1Encodable)octetString);
    }

    public static ServiceSpecificPermissions opaque(byte[] octetString) {
        return new ServiceSpecificPermissions(0, (ASN1Encodable)new DEROctetString(octetString));
    }

    public static ServiceSpecificPermissions bitmapSsp(BitmapSsp ssp) {
        return new ServiceSpecificPermissions(1, (ASN1Encodable)ssp);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getServiceSpecificPermissions() {
        return this.serviceSpecificPermissions;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.serviceSpecificPermissions);
    }
}

