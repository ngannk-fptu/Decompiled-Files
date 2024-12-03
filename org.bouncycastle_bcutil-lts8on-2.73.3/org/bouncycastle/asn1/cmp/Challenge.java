/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

public class Challenge
extends ASN1Object {
    private final AlgorithmIdentifier owf;
    private final ASN1OctetString witness;
    private final ASN1OctetString challenge;

    private Challenge(ASN1Sequence seq) {
        int index = 0;
        this.owf = seq.size() == 3 ? AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(index++)) : null;
        this.witness = ASN1OctetString.getInstance((Object)seq.getObjectAt(index++));
        this.challenge = ASN1OctetString.getInstance((Object)seq.getObjectAt(index));
    }

    public Challenge(byte[] witness, byte[] challenge) {
        this(null, witness, challenge);
    }

    public Challenge(AlgorithmIdentifier owf, byte[] witness, byte[] challenge) {
        this.owf = owf;
        this.witness = new DEROctetString(witness);
        this.challenge = new DEROctetString(challenge);
    }

    public static Challenge getInstance(Object o) {
        if (o instanceof Challenge) {
            return (Challenge)((Object)o);
        }
        if (o != null) {
            return new Challenge(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }

    public byte[] getWitness() {
        return this.witness.getOctets();
    }

    public byte[] getChallenge() {
        return this.challenge.getOctets();
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        this.addOptional(v, (ASN1Encodable)this.owf);
        v.add((ASN1Encodable)this.witness);
        v.add((ASN1Encodable)this.challenge);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, ASN1Encodable obj) {
        if (obj != null) {
            v.add(obj);
        }
    }

    public static class Rand
    extends ASN1Object {
        private final ASN1Integer _int;
        private final GeneralName sender;

        public Rand(ASN1Integer _int, GeneralName sender) {
            this._int = _int;
            this.sender = sender;
        }

        public Rand(ASN1Sequence seq) {
            if (seq.size() != 2) {
                throw new IllegalArgumentException("expected sequence size of 2");
            }
            this._int = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
            this.sender = GeneralName.getInstance((Object)seq.getObjectAt(1));
        }

        public static Rand getInstance(Object o) {
            if (o instanceof Rand) {
                return (Rand)((Object)o);
            }
            if (o != null) {
                return new Rand(ASN1Sequence.getInstance((Object)o));
            }
            return null;
        }

        public ASN1Integer getInt() {
            return this._int;
        }

        public GeneralName getSender() {
            return this.sender;
        }

        public ASN1Primitive toASN1Primitive() {
            return new DERSequence(new ASN1Encodable[]{this._int, this.sender});
        }
    }
}

