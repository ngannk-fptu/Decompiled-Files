/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509.sigi;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.x509.sigi.NameOrPseudonym;

public class PersonalData
extends ASN1Object {
    private NameOrPseudonym nameOrPseudonym;
    private BigInteger nameDistinguisher;
    private ASN1GeneralizedTime dateOfBirth;
    private DirectoryString placeOfBirth;
    private String gender;
    private DirectoryString postalAddress;

    public static PersonalData getInstance(Object obj) {
        if (obj == null || obj instanceof PersonalData) {
            return (PersonalData)obj;
        }
        if (obj instanceof ASN1Sequence) {
            return new PersonalData((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private PersonalData(ASN1Sequence seq) {
        if (seq.size() < 1) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.nameOrPseudonym = NameOrPseudonym.getInstance(e.nextElement());
        block7: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement());
            int tag = o.getTagNo();
            switch (tag) {
                case 0: {
                    this.nameDistinguisher = ASN1Integer.getInstance(o, false).getValue();
                    continue block7;
                }
                case 1: {
                    this.dateOfBirth = ASN1GeneralizedTime.getInstance(o, false);
                    continue block7;
                }
                case 2: {
                    this.placeOfBirth = DirectoryString.getInstance(o, true);
                    continue block7;
                }
                case 3: {
                    this.gender = ASN1PrintableString.getInstance(o, false).getString();
                    continue block7;
                }
                case 4: {
                    this.postalAddress = DirectoryString.getInstance(o, true);
                    continue block7;
                }
            }
            throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
        }
    }

    public PersonalData(NameOrPseudonym nameOrPseudonym, BigInteger nameDistinguisher, ASN1GeneralizedTime dateOfBirth, DirectoryString placeOfBirth, String gender, DirectoryString postalAddress) {
        this.nameOrPseudonym = nameOrPseudonym;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.nameDistinguisher = nameDistinguisher;
        this.postalAddress = postalAddress;
        this.placeOfBirth = placeOfBirth;
    }

    public NameOrPseudonym getNameOrPseudonym() {
        return this.nameOrPseudonym;
    }

    public BigInteger getNameDistinguisher() {
        return this.nameDistinguisher;
    }

    public ASN1GeneralizedTime getDateOfBirth() {
        return this.dateOfBirth;
    }

    public DirectoryString getPlaceOfBirth() {
        return this.placeOfBirth;
    }

    public String getGender() {
        return this.gender;
    }

    public DirectoryString getPostalAddress() {
        return this.postalAddress;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(6);
        vec.add(this.nameOrPseudonym);
        if (this.nameDistinguisher != null) {
            vec.add(new DERTaggedObject(false, 0, (ASN1Encodable)new ASN1Integer(this.nameDistinguisher)));
        }
        if (this.dateOfBirth != null) {
            vec.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.dateOfBirth));
        }
        if (this.placeOfBirth != null) {
            vec.add(new DERTaggedObject(true, 2, (ASN1Encodable)this.placeOfBirth));
        }
        if (this.gender != null) {
            vec.add(new DERTaggedObject(false, 3, (ASN1Encodable)new DERPrintableString(this.gender, true)));
        }
        if (this.postalAddress != null) {
            vec.add(new DERTaggedObject(true, 4, (ASN1Encodable)this.postalAddress));
        }
        return new DERSequence(vec);
    }
}

