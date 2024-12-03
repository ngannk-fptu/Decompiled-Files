/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class CertTemplateBuilder {
    private ASN1Integer version;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signingAlg;
    private X500Name issuer;
    private OptionalValidity validity;
    private X500Name subject;
    private SubjectPublicKeyInfo publicKey;
    private DERBitString issuerUID;
    private DERBitString subjectUID;
    private Extensions extensions;

    public CertTemplateBuilder setVersion(int ver) {
        this.version = new ASN1Integer((long)ver);
        return this;
    }

    public CertTemplateBuilder setSerialNumber(ASN1Integer ser) {
        this.serialNumber = ser;
        return this;
    }

    public CertTemplateBuilder setSigningAlg(AlgorithmIdentifier aid) {
        this.signingAlg = aid;
        return this;
    }

    public CertTemplateBuilder setIssuer(X500Name name) {
        this.issuer = name;
        return this;
    }

    public CertTemplateBuilder setValidity(OptionalValidity v) {
        this.validity = v;
        return this;
    }

    public CertTemplateBuilder setSubject(X500Name name) {
        this.subject = name;
        return this;
    }

    public CertTemplateBuilder setPublicKey(SubjectPublicKeyInfo spki) {
        this.publicKey = spki;
        return this;
    }

    public CertTemplateBuilder setIssuerUID(DERBitString uid) {
        this.issuerUID = uid;
        return this;
    }

    public CertTemplateBuilder setSubjectUID(DERBitString uid) {
        this.subjectUID = uid;
        return this;
    }

    public CertTemplateBuilder setExtensions(Extensions extens) {
        this.extensions = extens;
        return this;
    }

    public CertTemplate build() {
        ASN1EncodableVector v = new ASN1EncodableVector(10);
        this.addOptional(v, 0, false, (ASN1Encodable)this.version);
        this.addOptional(v, 1, false, (ASN1Encodable)this.serialNumber);
        this.addOptional(v, 2, false, (ASN1Encodable)this.signingAlg);
        this.addOptional(v, 3, true, (ASN1Encodable)this.issuer);
        this.addOptional(v, 4, false, (ASN1Encodable)this.validity);
        this.addOptional(v, 5, true, (ASN1Encodable)this.subject);
        this.addOptional(v, 6, false, (ASN1Encodable)this.publicKey);
        this.addOptional(v, 7, false, (ASN1Encodable)this.issuerUID);
        this.addOptional(v, 8, false, (ASN1Encodable)this.subjectUID);
        this.addOptional(v, 9, false, (ASN1Encodable)this.extensions);
        return CertTemplate.getInstance(new DERSequence(v));
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, boolean isExplicit, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(isExplicit, tagNo, obj));
        }
    }
}

