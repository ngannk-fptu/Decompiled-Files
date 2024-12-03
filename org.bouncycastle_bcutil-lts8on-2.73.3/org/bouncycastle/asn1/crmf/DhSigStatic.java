/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.util.Arrays;

public class DhSigStatic
extends ASN1Object {
    private final IssuerAndSerialNumber issuerAndSerial;
    private final ASN1OctetString hashValue;

    public DhSigStatic(byte[] hashValue) {
        this(null, hashValue);
    }

    public DhSigStatic(IssuerAndSerialNumber issuerAndSerial, byte[] hashValue) {
        this.issuerAndSerial = issuerAndSerial;
        this.hashValue = new DEROctetString(Arrays.clone((byte[])hashValue));
    }

    public static DhSigStatic getInstance(Object o) {
        if (o instanceof DhSigStatic) {
            return (DhSigStatic)((Object)o);
        }
        if (o != null) {
            return new DhSigStatic(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private DhSigStatic(ASN1Sequence seq) {
        if (seq.size() == 1) {
            this.issuerAndSerial = null;
            this.hashValue = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        } else if (seq.size() == 2) {
            this.issuerAndSerial = IssuerAndSerialNumber.getInstance(seq.getObjectAt(0));
            this.hashValue = ASN1OctetString.getInstance((Object)seq.getObjectAt(1));
        } else {
            throw new IllegalArgumentException("sequence wrong length for DhSigStatic");
        }
    }

    public IssuerAndSerialNumber getIssuerAndSerial() {
        return this.issuerAndSerial;
    }

    public byte[] getHashValue() {
        return Arrays.clone((byte[])this.hashValue.getOctets());
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.issuerAndSerial != null) {
            v.add((ASN1Encodable)this.issuerAndSerial);
        }
        v.add((ASN1Encodable)this.hashValue);
        return new DERSequence(v);
    }
}

