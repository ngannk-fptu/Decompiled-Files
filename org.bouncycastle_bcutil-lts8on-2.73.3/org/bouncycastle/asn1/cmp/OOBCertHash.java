/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1BitString
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1Util
 *  org.bouncycastle.asn1.DERBitString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Util;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OOBCertHash
extends ASN1Object {
    private final AlgorithmIdentifier hashAlg;
    private final CertId certId;
    private final ASN1BitString hashVal;

    private OOBCertHash(ASN1Sequence seq) {
        int index = seq.size() - 1;
        this.hashVal = ASN1BitString.getInstance((Object)seq.getObjectAt(index--));
        AlgorithmIdentifier hashAlg = null;
        CertId certId = null;
        for (int i = index; i >= 0; --i) {
            ASN1TaggedObject tObj = (ASN1TaggedObject)seq.getObjectAt(i);
            if (tObj.hasContextTag(0)) {
                hashAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tObj, (boolean)true);
                continue;
            }
            if (tObj.hasContextTag(1)) {
                certId = CertId.getInstance(tObj, true);
                continue;
            }
            throw new IllegalArgumentException("unknown tag " + ASN1Util.getTagText((ASN1TaggedObject)tObj));
        }
        this.hashAlg = hashAlg;
        this.certId = certId;
    }

    public OOBCertHash(AlgorithmIdentifier hashAlg, CertId certId, byte[] hashVal) {
        this(hashAlg, certId, new DERBitString(hashVal));
    }

    public OOBCertHash(AlgorithmIdentifier hashAlg, CertId certId, DERBitString hashVal) {
        this.hashAlg = hashAlg;
        this.certId = certId;
        this.hashVal = hashVal;
    }

    public static OOBCertHash getInstance(Object o) {
        if (o instanceof OOBCertHash) {
            return (OOBCertHash)((Object)o);
        }
        if (o != null) {
            return new OOBCertHash(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public AlgorithmIdentifier getHashAlg() {
        return this.hashAlg;
    }

    public CertId getCertId() {
        return this.certId;
    }

    public ASN1BitString getHashVal() {
        return this.hashVal;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        this.addOptional(v, 0, (ASN1Encodable)this.hashAlg);
        this.addOptional(v, 1, (ASN1Encodable)this.certId);
        v.add((ASN1Encodable)this.hashVal);
        return new DERSequence(v);
    }

    private void addOptional(ASN1EncodableVector v, int tagNo, ASN1Encodable obj) {
        if (obj != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, tagNo, obj));
        }
    }
}

