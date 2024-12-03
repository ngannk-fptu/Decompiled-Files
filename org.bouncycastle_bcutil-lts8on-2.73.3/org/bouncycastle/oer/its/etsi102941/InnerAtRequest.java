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
import org.bouncycastle.oer.its.etsi102941.SharedAtRequest;
import org.bouncycastle.oer.its.etsi102941.basetypes.EcSignature;
import org.bouncycastle.oer.its.etsi102941.basetypes.PublicKeys;
import org.bouncycastle.util.Arrays;

public class InnerAtRequest
extends ASN1Object {
    private final PublicKeys publicKeys;
    private final ASN1OctetString hmacKey;
    private final SharedAtRequest sharedAtRequest;
    private final EcSignature ecSignature;

    public InnerAtRequest(PublicKeys publicKeys, ASN1OctetString hmacKey, SharedAtRequest sharedAtRequest, EcSignature ecSignature) {
        this.publicKeys = publicKeys;
        this.hmacKey = hmacKey;
        this.sharedAtRequest = sharedAtRequest;
        this.ecSignature = ecSignature;
    }

    private InnerAtRequest(ASN1Sequence seq) {
        if (seq.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.publicKeys = PublicKeys.getInstance(seq.getObjectAt(0));
        this.hmacKey = ASN1OctetString.getInstance((Object)seq.getObjectAt(1));
        this.sharedAtRequest = SharedAtRequest.getInstance(seq.getObjectAt(2));
        this.ecSignature = EcSignature.getInstance(seq.getObjectAt(3));
    }

    public static InnerAtRequest getInstance(Object o) {
        if (o instanceof InnerAtRequest) {
            return (InnerAtRequest)((Object)o);
        }
        if (o != null) {
            return new InnerAtRequest(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public PublicKeys getPublicKeys() {
        return this.publicKeys;
    }

    public ASN1OctetString getHmacKey() {
        return this.hmacKey;
    }

    public SharedAtRequest getSharedAtRequest() {
        return this.sharedAtRequest;
    }

    public EcSignature getEcSignature() {
        return this.ecSignature;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.publicKeys, this.hmacKey, this.sharedAtRequest, this.ecSignature});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PublicKeys publicKeys;
        private ASN1OctetString hmacKey;
        private SharedAtRequest sharedAtRequest;
        private EcSignature ecSignature;

        public Builder setPublicKeys(PublicKeys publicKeys) {
            this.publicKeys = publicKeys;
            return this;
        }

        public Builder setHmacKey(ASN1OctetString hmacKey) {
            this.hmacKey = hmacKey;
            return this;
        }

        public Builder setHmacKey(byte[] hmacKey) {
            this.hmacKey = new DEROctetString(Arrays.clone((byte[])hmacKey));
            return this;
        }

        public Builder setSharedAtRequest(SharedAtRequest sharedAtRequest) {
            this.sharedAtRequest = sharedAtRequest;
            return this;
        }

        public Builder setEcSignature(EcSignature ecSignature) {
            this.ecSignature = ecSignature;
            return this;
        }

        public InnerAtRequest createInnerAtRequest() {
            return new InnerAtRequest(this.publicKeys, this.hmacKey, this.sharedAtRequest, this.ecSignature);
        }
    }
}

