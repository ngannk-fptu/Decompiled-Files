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
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.isismtt.x509.NamingAuthority;
import org.bouncycastle.asn1.isismtt.x509.ProfessionInfo;
import org.bouncycastle.asn1.x509.GeneralName;

public class Admissions
extends ASN1Object {
    private GeneralName admissionAuthority;
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionInfos;

    public static Admissions getInstance(Object obj) {
        if (obj == null || obj instanceof Admissions) {
            return (Admissions)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new Admissions((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private Admissions(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        ASN1Encodable o = (ASN1Encodable)e.nextElement();
        if (o instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject)o).getTagNo()) {
                case 0: {
                    this.admissionAuthority = GeneralName.getInstance((ASN1TaggedObject)((ASN1TaggedObject)o), (boolean)true);
                    break;
                }
                case 1: {
                    this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)o, true);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)o).getTagNo());
                }
            }
            o = (ASN1Encodable)e.nextElement();
        }
        if (o instanceof ASN1TaggedObject) {
            switch (((ASN1TaggedObject)o).getTagNo()) {
                case 1: {
                    this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)o, true);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)o).getTagNo());
                }
            }
            o = (ASN1Encodable)e.nextElement();
        }
        this.professionInfos = ASN1Sequence.getInstance((Object)o);
        if (e.hasMoreElements()) {
            throw new IllegalArgumentException("Bad object encountered: " + e.nextElement().getClass());
        }
    }

    public Admissions(GeneralName admissionAuthority, NamingAuthority namingAuthority, ProfessionInfo[] professionInfos) {
        this.admissionAuthority = admissionAuthority;
        this.namingAuthority = namingAuthority;
        this.professionInfos = new DERSequence((ASN1Encodable[])professionInfos);
    }

    public GeneralName getAdmissionAuthority() {
        return this.admissionAuthority;
    }

    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }

    public ProfessionInfo[] getProfessionInfos() {
        ProfessionInfo[] infos = new ProfessionInfo[this.professionInfos.size()];
        int count = 0;
        Enumeration e = this.professionInfos.getObjects();
        while (e.hasMoreElements()) {
            infos[count++] = ProfessionInfo.getInstance(e.nextElement());
        }
        return infos;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(3);
        if (this.admissionAuthority != null) {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.admissionAuthority));
        }
        if (this.namingAuthority != null) {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.namingAuthority));
        }
        vec.add((ASN1Encodable)this.professionInfos);
        return new DERSequence(vec);
    }
}

