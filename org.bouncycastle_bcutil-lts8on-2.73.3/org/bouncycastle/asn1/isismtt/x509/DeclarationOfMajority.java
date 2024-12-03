/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERPrintableString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class DeclarationOfMajority
extends ASN1Object
implements ASN1Choice {
    public static final int notYoungerThan = 0;
    public static final int fullAgeAtCountry = 1;
    public static final int dateOfBirth = 2;
    private ASN1TaggedObject declaration;

    public DeclarationOfMajority(int notYoungerThan) {
        this.declaration = new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer((long)notYoungerThan));
    }

    public DeclarationOfMajority(boolean fullAge, String country) {
        if (country.length() > 2) {
            throw new IllegalArgumentException("country can only be 2 characters");
        }
        if (fullAge) {
            this.declaration = new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence((ASN1Encodable)new DERPrintableString(country, true)));
        } else {
            ASN1EncodableVector v = new ASN1EncodableVector(2);
            v.add((ASN1Encodable)ASN1Boolean.FALSE);
            v.add((ASN1Encodable)new DERPrintableString(country, true));
            this.declaration = new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence(v));
        }
    }

    public DeclarationOfMajority(ASN1GeneralizedTime dateOfBirth) {
        this.declaration = new DERTaggedObject(false, 2, (ASN1Encodable)dateOfBirth);
    }

    public static DeclarationOfMajority getInstance(Object obj) {
        if (obj == null || obj instanceof DeclarationOfMajority) {
            return (DeclarationOfMajority)((Object)obj);
        }
        if (obj instanceof ASN1TaggedObject) {
            return new DeclarationOfMajority(ASN1TaggedObject.getInstance((Object)obj, (int)128));
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private DeclarationOfMajority(ASN1TaggedObject o) {
        if (o.getTagNo() > 2) {
            throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
        }
        this.declaration = o;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.declaration;
    }

    public int getType() {
        return this.declaration.getTagNo();
    }

    public int notYoungerThan() {
        if (this.declaration.getTagNo() != 0) {
            return -1;
        }
        return ASN1Integer.getInstance((ASN1TaggedObject)this.declaration, (boolean)false).intValueExact();
    }

    public ASN1Sequence fullAgeAtCountry() {
        if (this.declaration.getTagNo() != 1) {
            return null;
        }
        return ASN1Sequence.getInstance((ASN1TaggedObject)this.declaration, (boolean)false);
    }

    public ASN1GeneralizedTime getDateOfBirth() {
        if (this.declaration.getTagNo() != 2) {
            return null;
        }
        return ASN1GeneralizedTime.getInstance((ASN1TaggedObject)this.declaration, (boolean)false);
    }
}

