/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.util.Properties;

public class TBSCertificate
extends ASN1Object {
    ASN1Sequence seq;
    ASN1Integer version;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time startDate;
    Time endDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    ASN1BitString issuerUniqueId;
    ASN1BitString subjectUniqueId;
    Extensions extensions;

    public static TBSCertificate getInstance(ASN1TaggedObject obj, boolean explicit) {
        return TBSCertificate.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static TBSCertificate getInstance(Object obj) {
        if (obj instanceof TBSCertificate) {
            return (TBSCertificate)obj;
        }
        if (obj != null) {
            return new TBSCertificate(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TBSCertificate(ASN1Sequence seq) {
        int extras;
        int seqStart = 0;
        this.seq = seq;
        if (seq.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.version = ASN1Integer.getInstance((ASN1TaggedObject)seq.getObjectAt(0), true);
        } else {
            seqStart = -1;
            this.version = new ASN1Integer(0L);
        }
        boolean isV1 = false;
        boolean isV2 = false;
        if (this.version.hasValue(0)) {
            isV1 = true;
        } else if (this.version.hasValue(1)) {
            isV2 = true;
        } else if (!this.version.hasValue(2)) {
            throw new IllegalArgumentException("version number not recognised");
        }
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(seqStart + 1));
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(seqStart + 2));
        this.issuer = X500Name.getInstance(seq.getObjectAt(seqStart + 3));
        ASN1Sequence dates = (ASN1Sequence)seq.getObjectAt(seqStart + 4);
        this.startDate = Time.getInstance(dates.getObjectAt(0));
        this.endDate = Time.getInstance(dates.getObjectAt(1));
        this.subject = X500Name.getInstance(seq.getObjectAt(seqStart + 5));
        this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(seq.getObjectAt(seqStart + 6));
        if (extras != 0 && isV1) {
            throw new IllegalArgumentException("version 1 certificate contains extra data");
        }
        block5: for (extras = seq.size() - (seqStart + 6) - 1; extras > 0; --extras) {
            ASN1TaggedObject extra = (ASN1TaggedObject)seq.getObjectAt(seqStart + 6 + extras);
            switch (extra.getTagNo()) {
                case 1: {
                    this.issuerUniqueId = ASN1BitString.getInstance(extra, false);
                    continue block5;
                }
                case 2: {
                    this.subjectUniqueId = ASN1BitString.getInstance(extra, false);
                    continue block5;
                }
                case 3: {
                    if (isV2) {
                        throw new IllegalArgumentException("version 2 certificate cannot contain extensions");
                    }
                    this.extensions = Extensions.getInstance(ASN1Sequence.getInstance(extra, true));
                    continue block5;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered in structure: " + extra.getTagNo());
                }
            }
        }
    }

    public int getVersionNumber() {
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public Time getStartDate() {
        return this.startDate;
    }

    public Time getEndDate() {
        return this.endDate;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public ASN1BitString getIssuerUniqueId() {
        return this.issuerUniqueId;
    }

    public ASN1BitString getSubjectUniqueId() {
        return this.subjectUniqueId;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        if (Properties.getPropertyValue("org.bouncycastle.x509.allow_non-der_tbscert") != null) {
            if (Properties.isOverrideSet("org.bouncycastle.x509.allow_non-der_tbscert")) {
                return this.seq;
            }
        } else {
            return this.seq;
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (!this.version.hasValue(0)) {
            v.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.version));
        }
        v.add(this.serialNumber);
        v.add(this.signature);
        v.add(this.issuer);
        ASN1EncodableVector validity = new ASN1EncodableVector(2);
        validity.add(this.startDate);
        validity.add(this.endDate);
        v.add(new DERSequence(validity));
        if (this.subject != null) {
            v.add(this.subject);
        } else {
            v.add(new DERSequence());
        }
        v.add(this.subjectPublicKeyInfo);
        if (this.issuerUniqueId != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.issuerUniqueId));
        }
        if (this.subjectUniqueId != null) {
            v.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.subjectUniqueId));
        }
        if (this.extensions != null) {
            v.add(new DERTaggedObject(true, 3, (ASN1Encodable)this.extensions));
        }
        return new DERSequence(v);
    }
}

