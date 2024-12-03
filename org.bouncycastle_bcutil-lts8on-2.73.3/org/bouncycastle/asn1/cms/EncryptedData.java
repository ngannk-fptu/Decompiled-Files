/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.BERTaggedObject
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;

public class EncryptedData
extends ASN1Object {
    private ASN1Integer version;
    private EncryptedContentInfo encryptedContentInfo;
    private ASN1Set unprotectedAttrs;

    public static EncryptedData getInstance(Object o) {
        if (o instanceof EncryptedData) {
            return (EncryptedData)((Object)o);
        }
        if (o != null) {
            return new EncryptedData(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public EncryptedData(EncryptedContentInfo encInfo) {
        this(encInfo, null);
    }

    public EncryptedData(EncryptedContentInfo encInfo, ASN1Set unprotectedAttrs) {
        this.version = new ASN1Integer(unprotectedAttrs == null ? 0L : 2L);
        this.encryptedContentInfo = encInfo;
        this.unprotectedAttrs = unprotectedAttrs;
    }

    private EncryptedData(ASN1Sequence seq) {
        this.version = ASN1Integer.getInstance((Object)seq.getObjectAt(0));
        this.encryptedContentInfo = EncryptedContentInfo.getInstance(seq.getObjectAt(1));
        if (seq.size() == 3) {
            this.unprotectedAttrs = ASN1Set.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(2)), (boolean)false);
        }
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public EncryptedContentInfo getEncryptedContentInfo() {
        return this.encryptedContentInfo;
    }

    public ASN1Set getUnprotectedAttrs() {
        return this.unprotectedAttrs;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.encryptedContentInfo);
        if (this.unprotectedAttrs != null) {
            v.add((ASN1Encodable)new BERTaggedObject(false, 1, (ASN1Encodable)this.unprotectedAttrs));
        }
        return new BERSequence(v);
    }
}

