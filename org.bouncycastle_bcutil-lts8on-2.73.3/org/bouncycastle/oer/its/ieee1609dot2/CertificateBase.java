/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.ExplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.ImplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CertificateBase
extends ASN1Object {
    private final UINT8 version;
    private final CertificateType type;
    private final IssuerIdentifier issuer;
    private final ToBeSignedCertificate toBeSigned;
    private final Signature signature;

    public CertificateBase(UINT8 version, CertificateType type, IssuerIdentifier issuer, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        this.version = version;
        this.type = type;
        this.issuer = issuer;
        this.toBeSigned = toBeSignedCertificate;
        this.signature = signature;
    }

    protected CertificateBase(ASN1Sequence seq) {
        if (seq.size() != 5) {
            throw new IllegalArgumentException("expected sequence size of 5");
        }
        this.version = UINT8.getInstance(seq.getObjectAt(0));
        this.type = CertificateType.getInstance(seq.getObjectAt(1));
        this.issuer = IssuerIdentifier.getInstance(seq.getObjectAt(2));
        this.toBeSigned = ToBeSignedCertificate.getInstance(seq.getObjectAt(3));
        this.signature = OEROptional.getValue(Signature.class, seq.getObjectAt(4));
    }

    public static CertificateBase getInstance(Object o) {
        if (o instanceof CertificateBase) {
            return (CertificateBase)((Object)o);
        }
        if (o != null) {
            return new CertificateBase(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UINT8 getVersion() {
        return this.version;
    }

    public CertificateType getType() {
        return this.type;
    }

    public IssuerIdentifier getIssuer() {
        return this.issuer;
    }

    public ToBeSignedCertificate getToBeSigned() {
        return this.toBeSigned;
    }

    public Signature getSignature() {
        return this.signature;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.version, this.type, this.issuer, this.toBeSigned, OEROptional.getInstance((Object)this.signature)});
    }

    public static class Builder {
        private UINT8 version;
        private CertificateType type;
        private IssuerIdentifier issuer;
        private ToBeSignedCertificate toBeSigned;
        private Signature signature;

        public Builder setVersion(UINT8 version) {
            this.version = version;
            return this;
        }

        public Builder setType(CertificateType type) {
            this.type = type;
            return this;
        }

        public Builder setIssuer(IssuerIdentifier issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder setToBeSigned(ToBeSignedCertificate toBeSigned) {
            this.toBeSigned = toBeSigned;
            return this;
        }

        public Builder setSignature(Signature signature) {
            this.signature = signature;
            return this;
        }

        public Certificate createCertificate() {
            return new Certificate(this.version, this.type, this.issuer, this.toBeSigned, this.signature);
        }

        public ExplicitCertificate createExplicitCertificate() {
            return new ExplicitCertificate(this.version, this.issuer, this.toBeSigned, this.signature);
        }

        public ImplicitCertificate createImplicitCertificate() {
            return new ImplicitCertificate(this.version, this.issuer, this.toBeSigned, this.signature);
        }

        public CertificateBase createCertificateBase() {
            return new CertificateBase(this.version, this.type, this.issuer, this.toBeSigned, this.signature);
        }

        public CertificateBase createEtsiTs103097Certificate() {
            return new EtsiTs103097Certificate(this.version, this.issuer, this.toBeSigned, this.signature);
        }
    }
}

