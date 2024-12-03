/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.isismtt.x509.Admissions;
import org.bouncycastle.asn1.x509.GeneralName;

public class AdmissionSyntax
extends ASN1Object {
    private GeneralName admissionAuthority;
    private ASN1Sequence contentsOfAdmissions;

    public static AdmissionSyntax getInstance(Object obj) {
        if (obj == null || obj instanceof AdmissionSyntax) {
            return (AdmissionSyntax)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new AdmissionSyntax((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private AdmissionSyntax(ASN1Sequence seq) {
        switch (seq.size()) {
            case 1: {
                this.contentsOfAdmissions = DERSequence.getInstance((Object)seq.getObjectAt(0));
                break;
            }
            case 2: {
                this.admissionAuthority = GeneralName.getInstance((Object)seq.getObjectAt(0));
                this.contentsOfAdmissions = DERSequence.getInstance((Object)seq.getObjectAt(1));
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad sequence size: " + seq.size());
            }
        }
    }

    public AdmissionSyntax(GeneralName admissionAuthority, ASN1Sequence contentsOfAdmissions) {
        this.admissionAuthority = admissionAuthority;
        this.contentsOfAdmissions = contentsOfAdmissions;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(2);
        if (this.admissionAuthority != null) {
            vec.add((ASN1Encodable)this.admissionAuthority);
        }
        vec.add((ASN1Encodable)this.contentsOfAdmissions);
        return new DERSequence(vec);
    }

    public GeneralName getAdmissionAuthority() {
        return this.admissionAuthority;
    }

    public Admissions[] getContentsOfAdmissions() {
        Admissions[] admissions = new Admissions[this.contentsOfAdmissions.size()];
        int count = 0;
        Enumeration e = this.contentsOfAdmissions.getObjects();
        while (e.hasMoreElements()) {
            admissions[count++] = Admissions.getInstance(e.nextElement());
        }
        return admissions;
    }
}

