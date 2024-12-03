/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.BERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EncryptedContentInfo
extends ASN1Object {
    private ASN1ObjectIdentifier contentType;
    private AlgorithmIdentifier contentEncryptionAlgorithm;
    private ASN1OctetString encryptedContent;

    public EncryptedContentInfo(ASN1ObjectIdentifier contentType, AlgorithmIdentifier contentEncryptionAlgorithm, ASN1OctetString encryptedContent) {
        this.contentType = contentType;
        this.contentEncryptionAlgorithm = contentEncryptionAlgorithm;
        this.encryptedContent = encryptedContent;
    }

    private EncryptedContentInfo(ASN1Sequence seq) {
        if (seq.size() < 2) {
            throw new IllegalArgumentException("Truncated Sequence Found");
        }
        this.contentType = (ASN1ObjectIdentifier)seq.getObjectAt(0);
        this.contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        if (seq.size() > 2) {
            this.encryptedContent = ASN1OctetString.getInstance((ASN1TaggedObject)((ASN1TaggedObject)seq.getObjectAt(2)), (boolean)false);
        }
    }

    public static EncryptedContentInfo getInstance(Object obj) {
        if (obj instanceof EncryptedContentInfo) {
            return (EncryptedContentInfo)((Object)obj);
        }
        if (obj != null) {
            return new EncryptedContentInfo(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.contentEncryptionAlgorithm;
    }

    public ASN1OctetString getEncryptedContent() {
        return this.encryptedContent;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.contentType);
        v.add((ASN1Encodable)this.contentEncryptionAlgorithm);
        if (this.encryptedContent != null) {
            v.add((ASN1Encodable)new BERTaggedObject(false, 0, (ASN1Encodable)this.encryptedContent));
        }
        return new BERSequence(v);
    }
}

