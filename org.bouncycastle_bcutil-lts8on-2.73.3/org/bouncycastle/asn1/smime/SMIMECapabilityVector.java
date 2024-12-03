/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.smime;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;

public class SMIMECapabilityVector {
    private ASN1EncodableVector capabilities = new ASN1EncodableVector();

    public void addCapability(ASN1ObjectIdentifier capability) {
        this.capabilities.add((ASN1Encodable)new DERSequence((ASN1Encodable)capability));
    }

    public void addCapability(ASN1ObjectIdentifier capability, int value) {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)capability);
        v.add((ASN1Encodable)new ASN1Integer((long)value));
        this.capabilities.add((ASN1Encodable)new DERSequence(v));
    }

    public void addCapability(ASN1ObjectIdentifier capability, ASN1Encodable params) {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)capability);
        v.add(params);
        this.capabilities.add((ASN1Encodable)new DERSequence(v));
    }

    public ASN1EncodableVector toASN1EncodableVector() {
        return this.capabilities;
    }
}

