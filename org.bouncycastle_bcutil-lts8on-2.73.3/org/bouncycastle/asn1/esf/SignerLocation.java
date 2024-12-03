/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x500.DirectoryString
 */
package org.bouncycastle.asn1.esf;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;

public class SignerLocation
extends ASN1Object {
    private DirectoryString countryName;
    private DirectoryString localityName;
    private ASN1Sequence postalAddress;

    private SignerLocation(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        block5: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement(), (int)128);
            switch (o.getTagNo()) {
                case 0: {
                    this.countryName = DirectoryString.getInstance((ASN1TaggedObject)o, (boolean)true);
                    continue block5;
                }
                case 1: {
                    this.localityName = DirectoryString.getInstance((ASN1TaggedObject)o, (boolean)true);
                    continue block5;
                }
                case 2: {
                    this.postalAddress = o.isExplicit() ? ASN1Sequence.getInstance((ASN1TaggedObject)o, (boolean)true) : ASN1Sequence.getInstance((ASN1TaggedObject)o, (boolean)false);
                    if (this.postalAddress == null || this.postalAddress.size() <= 6) continue block5;
                    throw new IllegalArgumentException("postal address must contain less than 6 strings");
                }
            }
            throw new IllegalArgumentException("illegal tag");
        }
    }

    private SignerLocation(DirectoryString countryName, DirectoryString localityName, ASN1Sequence postalAddress) {
        if (postalAddress != null && postalAddress.size() > 6) {
            throw new IllegalArgumentException("postal address must contain less than 6 strings");
        }
        this.countryName = countryName;
        this.localityName = localityName;
        this.postalAddress = postalAddress;
    }

    public SignerLocation(DirectoryString countryName, DirectoryString localityName, DirectoryString[] postalAddress) {
        this(countryName, localityName, (ASN1Sequence)new DERSequence((ASN1Encodable[])postalAddress));
    }

    public SignerLocation(ASN1UTF8String countryName, ASN1UTF8String localityName, ASN1Sequence postalAddress) {
        this(DirectoryString.getInstance((Object)countryName), DirectoryString.getInstance((Object)localityName), postalAddress);
    }

    public static SignerLocation getInstance(Object obj) {
        if (obj == null || obj instanceof SignerLocation) {
            return (SignerLocation)((Object)obj);
        }
        return new SignerLocation(ASN1Sequence.getInstance((Object)obj));
    }

    public DirectoryString getCountry() {
        return this.countryName;
    }

    public DirectoryString getLocality() {
        return this.localityName;
    }

    public DirectoryString[] getPostal() {
        if (this.postalAddress == null) {
            return null;
        }
        DirectoryString[] dirStrings = new DirectoryString[this.postalAddress.size()];
        for (int i = 0; i != dirStrings.length; ++i) {
            dirStrings[i] = DirectoryString.getInstance((Object)this.postalAddress.getObjectAt(i));
        }
        return dirStrings;
    }

    public ASN1Sequence getPostalAddress() {
        return this.postalAddress;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.countryName != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.countryName));
        }
        if (this.localityName != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.localityName));
        }
        if (this.postalAddress != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.postalAddress));
        }
        return new DERSequence(v);
    }
}

