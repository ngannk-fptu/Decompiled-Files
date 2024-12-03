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
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.util.Arrays;

public class AesCcmCiphertext
extends ASN1Object {
    private final ASN1OctetString nonce;
    private final Opaque ccmCiphertext;

    public AesCcmCiphertext(ASN1OctetString nonce, Opaque opaque) {
        this.nonce = nonce;
        this.ccmCiphertext = opaque;
    }

    private AesCcmCiphertext(ASN1Sequence seq) {
        if (seq.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        Iterator it = seq.iterator();
        this.nonce = ASN1OctetString.getInstance(it.next());
        this.ccmCiphertext = Opaque.getInstance(it.next());
    }

    public static AesCcmCiphertext getInstance(Object o) {
        if (o instanceof AesCcmCiphertext) {
            return (AesCcmCiphertext)((Object)o);
        }
        if (o != null) {
            return new AesCcmCiphertext(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getNonce() {
        return this.nonce;
    }

    public Opaque getCcmCiphertext() {
        return this.ccmCiphertext;
    }

    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(new ASN1Encodable[]{this.nonce, this.ccmCiphertext});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString nonce;
        private Opaque opaque;

        public Builder setNonce(ASN1OctetString nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder setNonce(byte[] nonce) {
            return this.setNonce((ASN1OctetString)new DEROctetString(Arrays.clone((byte[])nonce)));
        }

        public Builder setCcmCiphertext(Opaque opaque) {
            this.opaque = opaque;
            return this;
        }

        public Builder setCcmCiphertext(byte[] opaque) {
            return this.setCcmCiphertext(new Opaque(opaque));
        }

        public AesCcmCiphertext createAesCcmCiphertext() {
            return new AesCcmCiphertext(this.nonce, this.opaque);
        }
    }
}

