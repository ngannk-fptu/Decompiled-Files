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
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CertStatus
extends ASN1Object {
    private final ASN1OctetString certHash;
    private final ASN1Integer certReqId;
    private final PKIStatusInfo statusInfo;
    private final AlgorithmIdentifier hashAlg;

    private CertStatus(ASN1Sequence seq) {
        this.certHash = ASN1OctetString.getInstance((Object)seq.getObjectAt(0));
        this.certReqId = ASN1Integer.getInstance((Object)seq.getObjectAt(1));
        PKIStatusInfo statusInfo = null;
        AlgorithmIdentifier hashAlg = null;
        if (seq.size() > 2) {
            for (int t = 2; t < seq.size(); ++t) {
                ASN1Primitive p = seq.getObjectAt(t).toASN1Primitive();
                if (p instanceof ASN1Sequence) {
                    statusInfo = PKIStatusInfo.getInstance(p);
                }
                if (!(p instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject dto = (ASN1TaggedObject)p;
                if (!dto.hasContextTag(0)) {
                    throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText((ASN1TaggedObject)dto));
                }
                hashAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)dto, (boolean)true);
            }
        }
        this.statusInfo = statusInfo;
        this.hashAlg = hashAlg;
    }

    public CertStatus(byte[] certHash, BigInteger certReqId) {
        this(certHash, new ASN1Integer(certReqId));
    }

    public CertStatus(byte[] certHash, ASN1Integer certReqId) {
        this.certHash = new DEROctetString(certHash);
        this.certReqId = certReqId;
        this.statusInfo = null;
        this.hashAlg = null;
    }

    public CertStatus(byte[] certHash, BigInteger certReqId, PKIStatusInfo statusInfo) {
        this.certHash = new DEROctetString(certHash);
        this.certReqId = new ASN1Integer(certReqId);
        this.statusInfo = statusInfo;
        this.hashAlg = null;
    }

    public CertStatus(byte[] certHash, BigInteger certReqId, PKIStatusInfo statusInfo, AlgorithmIdentifier hashAlg) {
        this.certHash = new DEROctetString(certHash);
        this.certReqId = new ASN1Integer(certReqId);
        this.statusInfo = statusInfo;
        this.hashAlg = hashAlg;
    }

    public static CertStatus getInstance(Object o) {
        if (o instanceof CertStatus) {
            return (CertStatus)((Object)o);
        }
        if (o != null) {
            return new CertStatus(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public ASN1OctetString getCertHash() {
        return this.certHash;
    }

    public ASN1Integer getCertReqId() {
        return this.certReqId;
    }

    public PKIStatusInfo getStatusInfo() {
        return this.statusInfo;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.certHash);
        v.add((ASN1Encodable)this.certReqId);
        if (this.statusInfo != null) {
            v.add((ASN1Encodable)this.statusInfo);
        }
        if (this.hashAlg != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.hashAlg));
        }
        return new DERSequence(v);
    }
}

