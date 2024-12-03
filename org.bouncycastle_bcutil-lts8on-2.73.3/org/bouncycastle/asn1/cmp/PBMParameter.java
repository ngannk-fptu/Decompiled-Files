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

public class PBMParameter
extends ASN1Object {
    private final ASN1OctetString salt;
    private final AlgorithmIdentifier owf;
    private final ASN1Integer iterationCount;
    private final AlgorithmIdentifier mac;

    private PBMParameter(ASN1Sequence seq) {
        this.salt = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        this.owf = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.iterationCount = ASN1Integer.getInstance((Object)seq.getObjectAt(2));
        this.mac = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(3));
    }

    public PBMParameter(byte[] salt, AlgorithmIdentifier owf, int iterationCount, AlgorithmIdentifier mac) {
        this((ASN1OctetString)new DEROctetString(salt), owf, new ASN1Integer((long)iterationCount), mac);
    }

    public PBMParameter(ASN1OctetString salt, AlgorithmIdentifier owf, ASN1Integer iterationCount, AlgorithmIdentifier mac) {
        this.salt = salt;
        this.owf = owf;
        this.iterationCount = iterationCount;
        this.mac = mac;
    }

    public static PBMParameter getInstance(Object o) {
        if (o instanceof PBMParameter) {
            return (PBMParameter)((Object)o);
        }
        if (o != null) {
            return new PBMParameter(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getSalt() {
        return this.salt;
    }

    public AlgorithmIdentifier getOwf() {
        return this.owf;
    }

    public ASN1Integer getIterationCount() {
        return this.iterationCount;
    }

    public AlgorithmIdentifier getMac() {
        return this.mac;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.salt);
        v.add((ASN1Encodable)this.owf);
        v.add((ASN1Encodable)this.iterationCount);
        v.add((ASN1Encodable)this.mac);
        return new DERSequence(v);
    }
}

