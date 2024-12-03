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
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
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
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.util.Arrays;

public class ESSCertIDv2
extends ASN1Object {
    private AlgorithmIdentifier hashAlgorithm;
    private byte[] certHash;
    private IssuerSerial issuerSerial;
    private static final AlgorithmIdentifier DEFAULT_ALG_ID = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);

    public static ESSCertIDv2 getInstance(Object o) {
        if (o instanceof ESSCertIDv2) {
            return (ESSCertIDv2)((Object)o);
        }
        if (o != null) {
            return new ESSCertIDv2(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private ESSCertIDv2(ASN1Sequence seq) {
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        int count = 0;
        this.hashAlgorithm = seq.getObjectAt(0) instanceof ASN1OctetString ? DEFAULT_ALG_ID : AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(count++).toASN1Primitive());
        this.certHash = ASN1OctetString.getInstance((Object)seq.getObjectAt(count++).toASN1Primitive()).getOctets();
        if (seq.size() > count) {
            this.issuerSerial = IssuerSerial.getInstance((Object)seq.getObjectAt(count));
        }
    }

    public ESSCertIDv2(byte[] certHash) {
        this(null, certHash, null);
    }

    public ESSCertIDv2(AlgorithmIdentifier algId, byte[] certHash) {
        this(algId, certHash, null);
    }

    public ESSCertIDv2(byte[] certHash, IssuerSerial issuerSerial) {
        this(null, certHash, issuerSerial);
    }

    public ESSCertIDv2(AlgorithmIdentifier algId, byte[] certHash, IssuerSerial issuerSerial) {
        this.hashAlgorithm = algId == null ? DEFAULT_ALG_ID : algId;
        this.certHash = Arrays.clone((byte[])certHash);
        this.issuerSerial = issuerSerial;
    }

    public AlgorithmIdentifier getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public byte[] getCertHash() {
        return Arrays.clone((byte[])this.certHash);
    }

    public IssuerSerial getIssuerSerial() {
        return this.issuerSerial;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        if (!this.hashAlgorithm.equals((Object)DEFAULT_ALG_ID)) {
            v.add((ASN1Encodable)this.hashAlgorithm);
        }
        v.add((ASN1Encodable)new DEROctetString(this.certHash).toASN1Primitive());
        if (this.issuerSerial != null) {
            v.add((ASN1Encodable)this.issuerSerial);
        }
        return new DERSequence(v);
    }
}

