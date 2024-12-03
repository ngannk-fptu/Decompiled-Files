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
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KEMRecipientInfo
extends ASN1Object {
    private final ASN1Integer cmsVersion;
    private final RecipientIdentifier rid;
    private final AlgorithmIdentifier kem;
    private final ASN1OctetString kemct;
    private final AlgorithmIdentifier kdf;
    private final ASN1Integer kekLength;
    private final ASN1OctetString ukm;
    private final AlgorithmIdentifier wrap;
    private final ASN1OctetString encryptedKey;

    public KEMRecipientInfo(RecipientIdentifier rid, AlgorithmIdentifier kem, ASN1OctetString kemct, AlgorithmIdentifier kdf, ASN1Integer kekLength, ASN1OctetString ukm, AlgorithmIdentifier wrap, ASN1OctetString encryptedKey) {
        if (kem == null) {
            throw new NullPointerException("kem cannot be null");
        }
        if (wrap == null) {
            throw new NullPointerException("wrap cannot be null");
        }
        this.cmsVersion = new ASN1Integer(0L);
        this.rid = rid;
        this.kem = kem;
        this.kemct = kemct;
        this.kdf = kdf;
        this.kekLength = kekLength;
        this.ukm = ukm;
        this.wrap = wrap;
        this.encryptedKey = encryptedKey;
    }

    public static KEMRecipientInfo getInstance(Object o) {
        if (o instanceof KEMRecipientInfo) {
            return (KEMRecipientInfo)((Object)o);
        }
        if (o != null) {
            return new KEMRecipientInfo(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    private KEMRecipientInfo(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("sequence must consist of 3 elements");
        }
        this.cmsVersion = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        this.rid = RecipientIdentifier.getInstance(seq.getObjectAt(1));
        this.kem = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(2));
        this.kemct = ASN1OctetString.getInstance((Object)seq.getObjectAt(3));
        this.kdf = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(4));
        this.kekLength = ASN1Integer.getInstance((Object)seq.getObjectAt(5));
        int elt = 6;
        this.ukm = seq.getObjectAt(6) instanceof ASN1TaggedObject ? ASN1OctetString.getInstance((ASN1TaggedObject)ASN1TaggedObject.getInstance((Object)seq.getObjectAt(elt++)), (boolean)true) : null;
        this.wrap = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(elt++));
        this.encryptedKey = ASN1OctetString.getInstance((Object)seq.getObjectAt(elt++));
    }

    public RecipientIdentifier getRecipientIdentifier() {
        return this.rid;
    }

    public AlgorithmIdentifier getKem() {
        return this.kem;
    }

    public ASN1OctetString getKemct() {
        return this.kemct;
    }

    public AlgorithmIdentifier getKdf() {
        return this.kdf;
    }

    public AlgorithmIdentifier getWrap() {
        return this.wrap;
    }

    public byte[] getUkm() {
        if (this.ukm == null) {
            return null;
        }
        return this.ukm.getOctets();
    }

    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add((ASN1Encodable)this.cmsVersion);
        v.add((ASN1Encodable)this.rid);
        v.add((ASN1Encodable)this.kem);
        v.add((ASN1Encodable)this.kemct);
        v.add((ASN1Encodable)this.kdf);
        v.add((ASN1Encodable)this.kekLength);
        if (this.ukm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.ukm));
        }
        v.add((ASN1Encodable)this.wrap);
        v.add((ASN1Encodable)this.encryptedKey);
        return new DERSequence(v);
    }
}

