/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CMSAlgorithmProtection
extends ASN1Object {
    public static final int SIGNATURE = 1;
    public static final int MAC = 2;
    private final AlgorithmIdentifier digestAlgorithm;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final AlgorithmIdentifier macAlgorithm;

    public CMSAlgorithmProtection(AlgorithmIdentifier digestAlgorithm, int type, AlgorithmIdentifier algorithmIdentifier) {
        if (digestAlgorithm == null || algorithmIdentifier == null) {
            throw new NullPointerException("AlgorithmIdentifiers cannot be null");
        }
        this.digestAlgorithm = digestAlgorithm;
        if (type == 1) {
            this.signatureAlgorithm = algorithmIdentifier;
            this.macAlgorithm = null;
        } else if (type == 2) {
            this.signatureAlgorithm = null;
            this.macAlgorithm = algorithmIdentifier;
        } else {
            throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private CMSAlgorithmProtection(ASN1Sequence sequence) {
        if (sequence.size() != 2) {
            throw new IllegalArgumentException("Sequence wrong size: One of signatureAlgorithm or macAlgorithm must be present");
        }
        this.digestAlgorithm = AlgorithmIdentifier.getInstance((Object)sequence.getObjectAt(0));
        ASN1TaggedObject tagged = ASN1TaggedObject.getInstance((Object)sequence.getObjectAt(1));
        if (tagged.getTagNo() == 1) {
            this.signatureAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tagged, (boolean)false);
            this.macAlgorithm = null;
        } else if (tagged.getTagNo() == 2) {
            this.signatureAlgorithm = null;
            this.macAlgorithm = AlgorithmIdentifier.getInstance((ASN1TaggedObject)tagged, (boolean)false);
        } else {
            throw new IllegalArgumentException("Unknown tag found: " + tagged.getTagNo());
        }
    }

    public static CMSAlgorithmProtection getInstance(Object obj) {
        if (obj instanceof CMSAlgorithmProtection) {
            return (CMSAlgorithmProtection)((Object)obj);
        }
        if (obj != null) {
            return new CMSAlgorithmProtection(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public AlgorithmIdentifier getMacAlgorithm() {
        return this.macAlgorithm;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.digestAlgorithm);
        if (this.signatureAlgorithm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.signatureAlgorithm));
        }
        if (this.macAlgorithm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.macAlgorithm));
        }
        return new DERSequence(v);
    }
}

