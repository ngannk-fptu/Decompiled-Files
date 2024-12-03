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
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.etsi102941.EnrolmentResponseCode;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.util.Arrays;

public class InnerEcResponse
extends ASN1Object {
    private final ASN1OctetString requestHash;
    private final EnrolmentResponseCode responseCode;
    private final EtsiTs103097Certificate certificate;

    public InnerEcResponse(ASN1OctetString requestHash, EnrolmentResponseCode responseCode, EtsiTs103097Certificate certificate) {
        this.requestHash = requestHash;
        this.responseCode = responseCode;
        this.certificate = certificate;
    }

    private InnerEcResponse(ASN1Sequence sequence) {
        if (sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.requestHash = ASN1OctetString.getInstance((Object)sequence.getObjectAt(0));
        this.responseCode = EnrolmentResponseCode.getInstance(sequence.getObjectAt(1));
        this.certificate = OEROptional.getValue(EtsiTs103097Certificate.class, sequence.getObjectAt(2));
    }

    public static InnerEcResponse getInstance(Object o) {
        if (o instanceof InnerEcResponse) {
            return (InnerEcResponse)((Object)o);
        }
        if (o != null) {
            return new InnerEcResponse(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getRequestHash() {
        return this.requestHash;
    }

    public EnrolmentResponseCode getResponseCode() {
        return this.responseCode;
    }

    public EtsiTs103097Certificate getCertificate() {
        return this.certificate;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance((Object)this.certificate)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString requestHash;
        private EnrolmentResponseCode responseCode;
        private EtsiTs103097Certificate certificate;

        public Builder setRequestHash(ASN1OctetString requestHash) {
            this.requestHash = requestHash;
            return this;
        }

        public Builder setRequestHash(byte[] requestHash) {
            this.requestHash = new DEROctetString(Arrays.clone((byte[])requestHash));
            return this;
        }

        public Builder setResponseCode(EnrolmentResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder setCertificate(EtsiTs103097Certificate certificate) {
            this.certificate = certificate;
            return this;
        }

        public InnerEcResponse createInnerEcResponse() {
            return new InnerEcResponse(this.requestHash, this.responseCode, this.certificate);
        }
    }
}

