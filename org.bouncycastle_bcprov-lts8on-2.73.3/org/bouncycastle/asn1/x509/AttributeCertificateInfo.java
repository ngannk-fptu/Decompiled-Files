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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AttCertIssuer;
import org.bouncycastle.asn1.x509.AttCertValidityPeriod;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.Holder;

public class AttributeCertificateInfo
extends ASN1Object {
    private ASN1Integer version;
    private Holder holder;
    private AttCertIssuer issuer;
    private AlgorithmIdentifier signature;
    private ASN1Integer serialNumber;
    private AttCertValidityPeriod attrCertValidityPeriod;
    private ASN1Sequence attributes;
    private ASN1BitString issuerUniqueID;
    private Extensions extensions;

    public static AttributeCertificateInfo getInstance(ASN1TaggedObject obj, boolean explicit) {
        return AttributeCertificateInfo.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static AttributeCertificateInfo getInstance(Object obj) {
        if (obj instanceof AttributeCertificateInfo) {
            return (AttributeCertificateInfo)obj;
        }
        if (obj != null) {
            return new AttributeCertificateInfo(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private AttributeCertificateInfo(ASN1Sequence seq) {
        int start;
        if (seq.size() < 6 || seq.size() > 9) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        if (seq.getObjectAt(0) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(seq.getObjectAt(0));
            start = 1;
        } else {
            this.version = new ASN1Integer(0L);
            start = 0;
        }
        this.holder = Holder.getInstance(seq.getObjectAt(start));
        this.issuer = AttCertIssuer.getInstance(seq.getObjectAt(start + 1));
        this.signature = AlgorithmIdentifier.getInstance(seq.getObjectAt(start + 2));
        this.serialNumber = ASN1Integer.getInstance(seq.getObjectAt(start + 3));
        this.attrCertValidityPeriod = AttCertValidityPeriod.getInstance(seq.getObjectAt(start + 4));
        this.attributes = ASN1Sequence.getInstance(seq.getObjectAt(start + 5));
        for (int i = start + 6; i < seq.size(); ++i) {
            ASN1Encodable obj = seq.getObjectAt(i);
            if (obj instanceof ASN1BitString) {
                this.issuerUniqueID = ASN1BitString.getInstance(seq.getObjectAt(i));
                continue;
            }
            if (!(obj instanceof ASN1Sequence) && !(obj instanceof Extensions)) continue;
            this.extensions = Extensions.getInstance(seq.getObjectAt(i));
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public Holder getHolder() {
        return this.holder;
    }

    public AttCertIssuer getIssuer() {
        return this.issuer;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AttCertValidityPeriod getAttrCertValidityPeriod() {
        return this.attrCertValidityPeriod;
    }

    public ASN1Sequence getAttributes() {
        return this.attributes;
    }

    public ASN1BitString getIssuerUniqueID() {
        return this.issuerUniqueID;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(9);
        if (!this.version.hasValue(0)) {
            v.add(this.version);
        }
        v.add(this.holder);
        v.add(this.issuer);
        v.add(this.signature);
        v.add(this.serialNumber);
        v.add(this.attrCertValidityPeriod);
        v.add(this.attributes);
        if (this.issuerUniqueID != null) {
            v.add(this.issuerUniqueID);
        }
        if (this.extensions != null) {
            v.add(this.extensions);
        }
        return new DERSequence(v);
    }
}

