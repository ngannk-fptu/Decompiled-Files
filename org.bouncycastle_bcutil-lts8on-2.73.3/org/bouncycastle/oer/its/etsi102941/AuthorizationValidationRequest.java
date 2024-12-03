/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.SharedAtRequest;
import org.bouncycastle.oer.its.etsi102941.basetypes.EcSignature;

public class AuthorizationValidationRequest
extends ASN1Object {
    private final SharedAtRequest sharedAtRequest;
    private final EcSignature ecSignature;

    public AuthorizationValidationRequest(SharedAtRequest sharedAtRequest, EcSignature ecSignature) {
        this.sharedAtRequest = sharedAtRequest;
        this.ecSignature = ecSignature;
    }

    private AuthorizationValidationRequest(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.sharedAtRequest = SharedAtRequest.getInstance(seq.getObjectAt(0));
        this.ecSignature = EcSignature.getInstance(seq.getObjectAt(1));
    }

    public static AuthorizationValidationRequest getInstance(Object o) {
        if (o instanceof AuthorizationValidationRequest) {
            return (AuthorizationValidationRequest)((Object)o);
        }
        if (o != null) {
            return new AuthorizationValidationRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public SharedAtRequest getSharedAtRequest() {
        return this.sharedAtRequest;
    }

    public EcSignature getEcSignature() {
        return this.ecSignature;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.sharedAtRequest, this.ecSignature});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SharedAtRequest sharedAtRequest;
        private EcSignature ecSignature;

        public Builder setSharedAtRequest(SharedAtRequest sharedAtRequest) {
            this.sharedAtRequest = sharedAtRequest;
            return this;
        }

        public Builder setEcSignature(EcSignature ecSignature) {
            this.ecSignature = ecSignature;
            return this;
        }

        public AuthorizationValidationRequest createAuthorizationValidationRequest() {
            return new AuthorizationValidationRequest(this.sharedAtRequest, this.ecSignature);
        }
    }
}

