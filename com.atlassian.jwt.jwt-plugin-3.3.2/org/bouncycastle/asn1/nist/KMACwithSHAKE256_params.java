/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.nist;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class KMACwithSHAKE256_params
extends ASN1Object {
    private static final byte[] EMPTY_STRING = new byte[0];
    private static final int DEF_LENGTH = 512;
    private final int outputLength;
    private final byte[] customizationString;

    public KMACwithSHAKE256_params(int n) {
        this.outputLength = n;
        this.customizationString = EMPTY_STRING;
    }

    public KMACwithSHAKE256_params(int n, byte[] byArray) {
        this.outputLength = n;
        this.customizationString = Arrays.clone(byArray);
    }

    public static KMACwithSHAKE256_params getInstance(Object object) {
        if (object instanceof KMACwithSHAKE256_params) {
            return (KMACwithSHAKE256_params)object;
        }
        if (object != null) {
            return new KMACwithSHAKE256_params(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private KMACwithSHAKE256_params(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() > 2) {
            throw new IllegalArgumentException("sequence size greater than 2");
        }
        if (aSN1Sequence.size() == 2) {
            this.outputLength = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
            this.customizationString = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
        } else if (aSN1Sequence.size() == 1) {
            if (aSN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
                this.outputLength = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0)).intValueExact();
                this.customizationString = EMPTY_STRING;
            } else {
                this.outputLength = 512;
                this.customizationString = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
            }
        } else {
            this.outputLength = 512;
            this.customizationString = EMPTY_STRING;
        }
    }

    public int getOutputLength() {
        return this.outputLength;
    }

    public byte[] getCustomizationString() {
        return Arrays.clone(this.customizationString);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.outputLength != 512) {
            aSN1EncodableVector.add(new ASN1Integer(this.outputLength));
        }
        if (this.customizationString.length != 0) {
            aSN1EncodableVector.add(new DEROctetString(this.getCustomizationString()));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

