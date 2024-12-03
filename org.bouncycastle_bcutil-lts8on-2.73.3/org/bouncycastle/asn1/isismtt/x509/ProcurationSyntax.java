/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1PrintableString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERPrintableString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x500.DirectoryString
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.IssuerSerial
 */
package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.DirectoryString;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class ProcurationSyntax
extends ASN1Object {
    private String country;
    private DirectoryString typeOfSubstitution;
    private GeneralName thirdPerson;
    private IssuerSerial certRef;

    public static ProcurationSyntax getInstance(Object obj) {
        if (obj == null || obj instanceof ProcurationSyntax) {
            return (ProcurationSyntax)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new ProcurationSyntax((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private ProcurationSyntax(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        block5: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement(), (int)128);
            switch (o.getTagNo()) {
                case 1: {
                    this.country = ASN1PrintableString.getInstance((ASN1TaggedObject)o, (boolean)true).getString();
                    continue block5;
                }
                case 2: {
                    this.typeOfSubstitution = DirectoryString.getInstance((ASN1TaggedObject)o, (boolean)true);
                    continue block5;
                }
                case 3: {
                    ASN1Object signingFor = o.getExplicitBaseObject();
                    if (signingFor instanceof ASN1TaggedObject) {
                        this.thirdPerson = GeneralName.getInstance((Object)signingFor);
                        continue block5;
                    }
                    this.certRef = IssuerSerial.getInstance((Object)signingFor);
                    continue block5;
                }
            }
            throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
        }
    }

    public ProcurationSyntax(String country, DirectoryString typeOfSubstitution, IssuerSerial certRef) {
        this.country = country;
        this.typeOfSubstitution = typeOfSubstitution;
        this.thirdPerson = null;
        this.certRef = certRef;
    }

    public ProcurationSyntax(String country, DirectoryString typeOfSubstitution, GeneralName thirdPerson) {
        this.country = country;
        this.typeOfSubstitution = typeOfSubstitution;
        this.thirdPerson = thirdPerson;
        this.certRef = null;
    }

    public String getCountry() {
        return this.country;
    }

    public DirectoryString getTypeOfSubstitution() {
        return this.typeOfSubstitution;
    }

    public GeneralName getThirdPerson() {
        return this.thirdPerson;
    }

    public IssuerSerial getCertRef() {
        return this.certRef;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(3);
        if (this.country != null) {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)new DERPrintableString(this.country, true)));
        }
        if (this.typeOfSubstitution != null) {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)this.typeOfSubstitution));
        }
        if (this.thirdPerson != null) {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 3, (ASN1Encodable)this.thirdPerson));
        } else {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 3, (ASN1Encodable)this.certRef));
        }
        return new DERSequence(vec);
    }
}

