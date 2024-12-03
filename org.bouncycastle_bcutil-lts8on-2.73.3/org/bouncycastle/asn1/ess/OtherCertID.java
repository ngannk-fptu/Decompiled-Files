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
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.asn1.x509.DigestInfo
 *  org.bouncycastle.asn1.x509.IssuerSerial
 */
package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.IssuerSerial;

public class OtherCertID
extends ASN1Object {
    private ASN1Encodable otherCertHash;
    private IssuerSerial issuerSerial;

    public static OtherCertID getInstance(Object o) {
        if (o instanceof OtherCertID) {
            return (OtherCertID)((Object)o);
        }
        if (o != null) {
            return new OtherCertID(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private OtherCertID(ASN1Sequence seq) {
        if (seq.size() < 1 || seq.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        this.otherCertHash = seq.getObjectAt(0).toASN1Primitive() instanceof ASN1OctetString ? ASN1OctetString.getInstance((Object)seq.getObjectAt(0)) : DigestInfo.getInstance((Object)seq.getObjectAt(0));
        if (seq.size() > 1) {
            this.issuerSerial = IssuerSerial.getInstance((Object)seq.getObjectAt(1));
        }
    }

    public OtherCertID(AlgorithmIdentifier algId, byte[] digest) {
        this.otherCertHash = new DigestInfo(algId, digest);
    }

    public OtherCertID(AlgorithmIdentifier algId, byte[] digest, IssuerSerial issuerSerial) {
        this.otherCertHash = new DigestInfo(algId, digest);
        this.issuerSerial = issuerSerial;
    }

    public AlgorithmIdentifier getAlgorithmHash() {
        if (this.otherCertHash.toASN1Primitive() instanceof ASN1OctetString) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }
        return DigestInfo.getInstance((Object)this.otherCertHash).getAlgorithmId();
    }

    public byte[] getCertHash() {
        if (this.otherCertHash.toASN1Primitive() instanceof ASN1OctetString) {
            return ((ASN1OctetString)this.otherCertHash.toASN1Primitive()).getOctets();
        }
        return DigestInfo.getInstance((Object)this.otherCertHash).getDigest();
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.otherCertHash);
        if (this.issuerSerial != null) {
            v.add((ASN1Encodable)this.issuerSerial);
        }
        return new DERSequence(v);
    }
}

