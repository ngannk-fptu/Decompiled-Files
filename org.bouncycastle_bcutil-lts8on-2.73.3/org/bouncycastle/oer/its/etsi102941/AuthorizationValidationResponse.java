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
import org.bouncycastle.oer.its.etsi102941.AuthorizationValidationResponseCode;
import org.bouncycastle.oer.its.etsi102941.basetypes.CertificateSubjectAttributes;
import org.bouncycastle.util.Arrays;

public class AuthorizationValidationResponse
extends ASN1Object {
    private final ASN1OctetString requestHash;
    private final AuthorizationValidationResponseCode responseCode;
    private final CertificateSubjectAttributes confirmedSubjectAttributes;

    public AuthorizationValidationResponse(ASN1OctetString requestHash, AuthorizationValidationResponseCode responseCode, CertificateSubjectAttributes confirmedSubjectAttributes) {
        this.requestHash = requestHash;
        this.responseCode = responseCode;
        this.confirmedSubjectAttributes = confirmedSubjectAttributes;
    }

    private AuthorizationValidationResponse(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.requestHash = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        this.responseCode = AuthorizationValidationResponseCode.getInstance(seq.getObjectAt(1));
        this.confirmedSubjectAttributes = OEROptional.getValue(CertificateSubjectAttributes.class, seq.getObjectAt(2));
    }

    public static AuthorizationValidationResponse getInstance(Object o) {
        if (o instanceof AuthorizationValidationResponse) {
            return (AuthorizationValidationResponse)((Object)o);
        }
        if (o != null) {
            return new AuthorizationValidationResponse(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getRequestHash() {
        return this.requestHash;
    }

    public AuthorizationValidationResponseCode getResponseCode() {
        return this.responseCode;
    }

    public CertificateSubjectAttributes getConfirmedSubjectAttributes() {
        return this.confirmedSubjectAttributes;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.requestHash, this.responseCode, OEROptional.getInstance((Object)this.confirmedSubjectAttributes)});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString requestHash;
        private AuthorizationValidationResponseCode responseCode;
        private CertificateSubjectAttributes confirmedSubjectAttributes;

        public Builder setRequestHash(ASN1OctetString requestHash) {
            this.requestHash = requestHash;
            return this;
        }

        public Builder setRequestHash(byte[] requestHash) {
            this.requestHash = new DEROctetString(Arrays.clone((byte[])requestHash));
            return this;
        }

        public Builder setResponseCode(AuthorizationValidationResponseCode responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public Builder setConfirmedSubjectAttributes(CertificateSubjectAttributes confirmedSubjectAttributes) {
            this.confirmedSubjectAttributes = confirmedSubjectAttributes;
            return this;
        }

        public AuthorizationValidationResponse createAuthorizationValidationResponse() {
            return new AuthorizationValidationResponse(this.requestHash, this.responseCode, this.confirmedSubjectAttributes);
        }
    }
}

