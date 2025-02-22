/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;

public class GostR3410KeyTransport
extends ASN1Object {
    private final Gost2814789EncryptedKey sessionEncryptedKey;
    private final GostR3410TransportParameters transportParameters;

    private GostR3410KeyTransport(ASN1Sequence seq) {
        this.sessionEncryptedKey = Gost2814789EncryptedKey.getInstance(seq.getObjectAt(0));
        this.transportParameters = GostR3410TransportParameters.getInstance(ASN1TaggedObject.getInstance(seq.getObjectAt(1)), false);
    }

    public GostR3410KeyTransport(Gost2814789EncryptedKey sessionEncryptedKey, GostR3410TransportParameters transportParameters) {
        this.sessionEncryptedKey = sessionEncryptedKey;
        this.transportParameters = transportParameters;
    }

    public static GostR3410KeyTransport getInstance(Object obj) {
        if (obj instanceof GostR3410KeyTransport) {
            return (GostR3410KeyTransport)obj;
        }
        if (obj != null) {
            return new GostR3410KeyTransport(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public Gost2814789EncryptedKey getSessionEncryptedKey() {
        return this.sessionEncryptedKey;
    }

    public GostR3410TransportParameters getTransportParameters() {
        return this.transportParameters;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.sessionEncryptedKey);
        if (this.transportParameters != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)this.transportParameters));
        }
        return new DERSequence(v);
    }
}

