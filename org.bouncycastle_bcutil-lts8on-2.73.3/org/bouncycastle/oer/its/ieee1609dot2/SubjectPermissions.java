/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Choice
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Null
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERNull
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSspRange;

public class SubjectPermissions
extends ASN1Object
implements ASN1Choice {
    public static final int explicit = 0;
    public static final int all = 1;
    private final ASN1Encodable subjectPermissions;
    private final int choice;

    SubjectPermissions(int choice, ASN1Encodable value) {
        this.subjectPermissions = value;
        this.choice = choice;
    }

    public static SubjectPermissions explicit(SequenceOfPsidSspRange range) {
        return new SubjectPermissions(0, (ASN1Encodable)range);
    }

    public static SubjectPermissions all() {
        return new SubjectPermissions(1, (ASN1Encodable)DERNull.INSTANCE);
    }

    private SubjectPermissions(ASN1TaggedObject ato) {
        this.choice = ato.getTagNo();
        switch (this.choice) {
            case 0: {
                this.subjectPermissions = SequenceOfPsidSspRange.getInstance(ato.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.subjectPermissions = ASN1Null.getInstance((Object)ato.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static SubjectPermissions getInstance(Object src) {
        if (src instanceof SubjectPermissions) {
            return (SubjectPermissions)((Object)src);
        }
        if (src != null) {
            return new SubjectPermissions(ASN1TaggedObject.getInstance((Object)src, (int)128));
        }
        return null;
    }

    public ASN1Encodable getSubjectPermissions() {
        return this.subjectPermissions;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.subjectPermissions);
    }
}

