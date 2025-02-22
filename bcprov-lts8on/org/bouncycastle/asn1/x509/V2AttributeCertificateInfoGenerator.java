/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.AttCertValidityPeriod;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.AttributeCertificateInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.Holder;

public class V2AttributeCertificateInfoGenerator {
    private ASN1Integer version = new ASN1Integer(1L);
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private ASN1EncodableVector attributes = new ASN1EncodableVector();
    private DERBitString issuerUniqueID;
    private Extensions extensions;
    private ASN1GeneralizedTime startDate;
    private ASN1GeneralizedTime endDate;

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public void addAttribute(String oid, ASN1Encodable value) {
        this.attributes.add(new Attribute(new ASN1ObjectIdentifier(oid), new DERSet(value)));
    }

    public void addAttribute(Attribute attribute) {
        this.attributes.add(attribute);
    }

    public void setSerialNumber(ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSignature(AlgorithmIdentifier signature) {
        this.signature = signature;
    }

    public void setIssuer(AttCertIssuer issuer) {
        this.issuer = issuer;
    }

    public void setStartDate(ASN1GeneralizedTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(ASN1GeneralizedTime endDate) {
        this.endDate = endDate;
    }

    public void setIssuerUniqueID(DERBitString issuerUniqueID) {
        this.issuerUniqueID = issuerUniqueID;
    }

    public void setExtensions(Extensions extensions) {
        this.extensions = extensions;
    }

    public AttributeCertificateInfo generateAttributeCertificateInfo() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.holder == null || this.attributes == null) {
            throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator");
        }
        ASN1EncodableVector v = new ASN1EncodableVector(9);
        v.add(this.version);
        v.add(this.holder);
        v.add(this.issuer);
        v.add(this.signature);
        v.add(this.serialNumber);
        AttCertValidityPeriod validity = new AttCertValidityPeriod(this.startDate, this.endDate);
        v.add(validity);
        v.add(new DERSequence(this.attributes));
        if (this.issuerUniqueID != null) {
            v.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            v.add(this.extensions);
        }
        return AttributeCertificateInfo.getInstance(new DERSequence(v));
    }
}

