/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;

public class V2Form
extends ASN1Object {
    GeneralNames issuerName;
    IssuerSerial baseCertificateID;
    ObjectDigestInfo objectDigestInfo;

    public static V2Form getInstance(ASN1TaggedObject obj, boolean explicit) {
        return V2Form.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static V2Form getInstance(Object obj) {
        if (obj instanceof V2Form) {
            return (V2Form)obj;
        }
        if (obj != null) {
            return new V2Form(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public V2Form(GeneralNames issuerName) {
        this(issuerName, null, null);
    }

    public V2Form(GeneralNames issuerName, IssuerSerial baseCertificateID) {
        this(issuerName, baseCertificateID, null);
    }

    public V2Form(GeneralNames issuerName, ObjectDigestInfo objectDigestInfo) {
        this(issuerName, null, objectDigestInfo);
    }

    public V2Form(GeneralNames issuerName, IssuerSerial baseCertificateID, ObjectDigestInfo objectDigestInfo) {
        this.issuerName = issuerName;
        this.baseCertificateID = baseCertificateID;
        this.objectDigestInfo = objectDigestInfo;
    }

    private V2Form(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int index = 0;
        if (!(seq.getObjectAt(0) instanceof ASN1TaggedObject)) {
            ++index;
            this.issuerName = GeneralNames.getInstance(seq.getObjectAt(0));
        }
        for (int i = index; i != seq.size(); ++i) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(seq.getObjectAt(i));
            if (o.getTagNo() == 0) {
                this.baseCertificateID = IssuerSerial.getInstance(o, false);
                continue;
            }
            if (o.getTagNo() == 1) {
                this.objectDigestInfo = ObjectDigestInfo.getInstance(o, false);
                continue;
            }
            throw new IllegalArgumentException("Bad tag number: " + o.getTagNo());
        }
    }

    public GeneralNames getIssuerName() {
        return this.issuerName;
    }

    public IssuerSerial getBaseCertificateID() {
        return this.baseCertificateID;
    }

    public ObjectDigestInfo getObjectDigestInfo() {
        return this.objectDigestInfo;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (this.issuerName != null) {
            v.add(this.issuerName);
        }
        if (this.baseCertificateID != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.baseCertificateID));
        }
        if (this.objectDigestInfo != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.objectDigestInfo));
        }
        return new DERSequence(v);
    }
}

