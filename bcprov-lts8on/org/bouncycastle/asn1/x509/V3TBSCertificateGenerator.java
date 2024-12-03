/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;

public class V3TBSCertificateGenerator {
    DERTaggedObject version = new DERTaggedObject(true, 0, (ASN1Encodable)new ASN1Integer(2L));
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time startDate;
    Time endDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    Extensions extensions;
    private boolean altNamePresentAndCritical;
    private DERBitString issuerUniqueID;
    private DERBitString subjectUniqueID;

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(X500Name issuer) {
        this.issuer = issuer;
    }

    public void setStartDate(ASN1UTCTime startDate) {
        this.startDate = new Time(startDate);
    }

    public void setStartDate(Time startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(ASN1UTCTime endDate) {
        this.endDate = new Time(endDate);
    }

    public void setEndDate(Time endDate) {
        this.endDate = endDate;
    }

    public void setSubject(X500Name subject) {
        this.subject = subject;
    }

    public void setIssuerUniqueID(DERBitString uniqueID) {
        this.issuerUniqueID = uniqueID;
    }

    public void setSubjectUniqueID(DERBitString uniqueID) {
        this.subjectUniqueID = uniqueID;
    }

    public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo pubKeyInfo) {
        this.subjectPublicKeyInfo = pubKeyInfo;
    }

    public void setExtensions(Extensions extensions) {
        Extension altName;
        this.extensions = extensions;
        if (extensions != null && (altName = extensions.getExtension(Extension.subjectAlternativeName)) != null && altName.isCritical()) {
            this.altNamePresentAndCritical = true;
        }
    }

    public ASN1Sequence generatePreTBSCertificate() {
        if (this.signature != null) {
            throw new IllegalStateException("signature field should not be set in PreTBSCertificate");
        }
        if (this.serialNumber == null || this.issuer == null || this.startDate == null || this.endDate == null || this.subject == null && !this.altNamePresentAndCritical || this.subjectPublicKeyInfo == null) {
            throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
        }
        return this.generateTBSStructure();
    }

    private ASN1Sequence generateTBSStructure() {
        ASN1EncodableVector v = new ASN1EncodableVector(10);
        v.add(this.version);
        v.add(this.serialNumber);
        if (this.signature != null) {
            v.add(this.signature);
        }
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
        if (this.issuerUniqueID != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.issuerUniqueID));
        }
        if (this.subjectUniqueID != null) {
            v.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.subjectUniqueID));
        }
        if (this.extensions != null) {
            v.add(new DERTaggedObject(true, 3, (ASN1Encodable)this.extensions));
        }
        return new DERSequence(v);
    }

    public TBSCertificate generateTBSCertificate() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.subject == null && !this.altNamePresentAndCritical || this.subjectPublicKeyInfo == null) {
            throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
        }
        return TBSCertificate.getInstance(this.generateTBSStructure());
    }
}

