/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateFormat;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;
import org.bouncycastle.util.Arrays;

public class InnerEcRequest
extends ASN1Object {
    private final ASN1OctetString itsId;
    private final CertificateFormat certificateFormat;
    private final PublicKeys publicKeys;
    private final CertificateSubjectAttributes requestedSubjectAttributes;

    public InnerEcRequest(ASN1OctetString itsId, CertificateFormat certificateFormat, PublicKeys publicKeys, CertificateSubjectAttributes requestedSubjectAttributes) {
        this.itsId = itsId;
        this.certificateFormat = certificateFormat;
        this.publicKeys = publicKeys;
        this.requestedSubjectAttributes = requestedSubjectAttributes;
    }

    private InnerEcRequest(ASN1Sequence seq) {
        if (seq.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.itsId = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        this.certificateFormat = CertificateFormat.getInstance(seq.getObjectAt(1));
        this.publicKeys = PublicKeys.getInstance(seq.getObjectAt(2));
        this.requestedSubjectAttributes = CertificateSubjectAttributes.getInstance(seq.getObjectAt(3));
    }

    public static InnerEcRequest getInstance(Object o) {
        if (o instanceof InnerEcRequest) {
            return (InnerEcRequest)((Object)o);
        }
        if (o != null) {
            return new InnerEcRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getItsId() {
        return this.itsId;
    }

    public CertificateFormat getCertificateFormat() {
        return this.certificateFormat;
    }

    public PublicKeys getPublicKeys() {
        return this.publicKeys;
    }

    public CertificateSubjectAttributes getRequestedSubjectAttributes() {
        return this.requestedSubjectAttributes;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.itsId, this.certificateFormat, this.publicKeys, this.requestedSubjectAttributes});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString itsId;
        private CertificateFormat certificateFormat;
        private PublicKeys publicKeys;
        private CertificateSubjectAttributes requestedSubjectAttributes;

        public Builder setItsId(ASN1OctetString itsId) {
            this.itsId = itsId;
            return this;
        }

        public Builder setItsId(byte[] itsId) {
            this.itsId = new DEROctetString(Arrays.clone((byte[])itsId));
            return this;
        }

        public Builder setCertificateFormat(CertificateFormat certificateFormat) {
            this.certificateFormat = certificateFormat;
            return this;
        }

        public Builder setPublicKeys(PublicKeys publicKeys) {
            this.publicKeys = publicKeys;
            return this;
        }

        public Builder setRequestedSubjectAttributes(CertificateSubjectAttributes requestedSubjectAttributes) {
            this.requestedSubjectAttributes = requestedSubjectAttributes;
            return this;
        }

        public InnerEcRequest createInnerEcRequest() {
            return new InnerEcRequest(this.itsId, this.certificateFormat, this.publicKeys, this.requestedSubjectAttributes);
        }
    }
}

