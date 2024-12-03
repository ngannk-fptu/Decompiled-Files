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
import org.bouncycastle.oer.its.etsi102941.AuthorizationResponseCode;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.util.Arrays;

public class InnerAtResponse
extends ASN1Object {
    private final ASN1OctetString requestHash;
    private final AuthorizationResponseCode responseCode;
    private final EtsiTs103097Certificate certificate;

    public InnerAtResponse(ASN1OctetString requestHash, AuthorizationResponseCode responseCode, EtsiTs103097Certificate certificate) {
        this.requestHash = requestHash;
        this.responseCode = responseCode;
        this.certificate = certificate;
    }

    private InnerAtResponse(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.requestHash = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        this.responseCode = AuthorizationResponseCode.getInstance(seq.getObjectAt(1));
        this.certificate = OEROptional.getValue(EtsiTs103097Certificate.class, seq.getObjectAt(2));
    }

    public static InnerAtResponse getInstance(Object o) {
        if (o instanceof InnerAtResponse) {
            return (InnerAtResponse)((Object)o);
        }
        if (o != null) {
            return new InnerAtResponse(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getRequestHash() {
        return this.requestHash;
    }

    public AuthorizationResponseCode getResponseCode() {
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
        private AuthorizationResponseCode responseCode;
        private EtsiTs103097Certificate certificate;

        public Builder setRequestHash(ASN1OctetString requestHash) {
            this.requestHash = requestHash;
            return this;
        }

        public Builder setRequestHash(byte[] requestHash) {
            this.requestHash = new DEROctetString(Arrays.clone((byte[])requestHash));
            return this;
        }

        public Builder setResponseCode(AuthorizationResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder setCertificate(EtsiTs103097Certificate certificate) {
            this.certificate = certificate;
            return this;
        }

        public InnerAtResponse createInnerAtResponse() {
            return new InnerAtResponse(this.requestHash, this.responseCode, this.certificate);
        }
    }
}

