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
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class HashedData
extends ASN1Object
implements ASN1Choice {
    public static final int sha256HashedData = 0;
    public static final int sha384HashedData = 1;
    public static final int reserved = 2;
    private final int choice;
    private final ASN1Encodable hashedData;

    public HashedData(int choice, ASN1Encodable sha256HashedData) {
        this.choice = choice;
        this.hashedData = sha256HashedData;
    }

    private HashedData(ASN1TaggedObject dto) {
        switch (dto.getTagNo()) {
            case 0: 
            case 1: 
            case 2: {
                this.choice = dto.getTagNo();
                this.hashedData = DEROctetString.getInstance((Object)dto.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + dto.getTagNo());
            }
        }
    }

    public static HashedData sha256HashedData(ASN1OctetString sha256HashedData) {
        return new HashedData(0, (ASN1Encodable)sha256HashedData);
    }

    public static HashedData sha256HashedData(byte[] sha256HashedData) {
        return new HashedData(0, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])sha256HashedData)));
    }

    public static HashedData sha384HashedData(ASN1OctetString sha384HashedData) {
        return new HashedData(1, (ASN1Encodable)sha384HashedData);
    }

    public static HashedData sha384HashedData(byte[] sha384HashedData) {
        return new HashedData(1, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])sha384HashedData)));
    }

    public static HashedData reserved(ASN1OctetString reserved) {
        return new HashedData(2, (ASN1Encodable)reserved);
    }

    public static HashedData reserved(byte[] reserved) {
        return new HashedData(2, (ASN1Encodable)new DEROctetString(Arrays.clone((byte[])reserved)));
    }

    public static HashedData getInstance(Object o) {
        if (o instanceof HashedData) {
            return (HashedData)((Object)o);
        }
        if (o != null) {
            return new HashedData(ASN1TaggedObject.getInstance((Object)o, (int)128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getHashedData() {
        return this.hashedData;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.hashedData);
    }
}

