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
 *  org.bouncycastle.asn1.x509.IssuerSerial
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

public class ESSCertID
extends ASN1Object {
    private ASN1OctetString certHash;
    private IssuerSerial issuerSerial;

    public static ESSCertID getInstance(Object o) {
        if (o instanceof ESSCertID) {
            return (ESSCertID)((Object)o);
        }
        if (o != null) {
            return new ESSCertID(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private ESSCertID(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.certHash = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.issuerSerial = IssuerSerial.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public ESSCertID(byte[] hash) {
        this.certHash = new DEROctetString(Arrays.clone((byte[])hash));
    }

    public ESSCertID(byte[] hash, IssuerSerial issuerSerial) {
        this.certHash = new DEROctetString(Arrays.clone((byte[])hash));
        this.issuerSerial = issuerSerial;
    }

    public byte[] getCertHash() {
        return this.certHash.getOctets();
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.certHash);
        if (this.issuerSerial != null) {
            v.add((ASN1Encodable)this.issuerSerial);
        }
        return new DERSequence(v);
    }
}

