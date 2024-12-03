/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class IssuerIdentifier
extends ASN1Object
implements ASN1Choice {
    public static final int sha256AndDigest = 0;
    public static final int self = 1;
    public static final int sha384AndDigest = 2;
    private final int choice;
    private final ASN1Encodable issuerIdentifier;

    public static IssuerIdentifier sha256AndDigest(HashedId8 data) {
        return new IssuerIdentifier(0, (ASN1Encodable)data);
    }

    public static IssuerIdentifier self(HashAlgorithm data) {
        return new IssuerIdentifier(1, (ASN1Encodable)data);
    }

    public static IssuerIdentifier sha384AndDigest(HashedId8 data) {
        return new IssuerIdentifier(2, (ASN1Encodable)data);
    }

    public IssuerIdentifier(int choice, ASN1Encodable value) {
        this.choice = choice;
        this.issuerIdentifier = value;
    }

    private IssuerIdentifier(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        ASN1Object o = ato.getExplicitBaseObject();
        switch (this.choice) {
            case 0: 
            case 2: {
                this.issuerIdentifier = HashedId8.getInstance(o);
                break;
            }
            case 1: {
                this.issuerIdentifier = HashAlgorithm.getInstance(o);
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static IssuerIdentifier getInstance(Object choice) {
        if (choice instanceof IssuerIdentifier) {
            return (IssuerIdentifier)((Object)choice);
        }
        if (choice != null) {
            return new IssuerIdentifier(ASN1TaggedObject.getInstance((Object)choice, (int)128));
        }
        return null;
    }

    public boolean isSelf() {
        return this.choice == 1;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getIssuerIdentifier() {
        return this.issuerIdentifier;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.issuerIdentifier);
    }
}

