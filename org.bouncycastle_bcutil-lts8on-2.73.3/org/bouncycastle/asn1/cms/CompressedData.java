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
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class CompressedData
extends ASN1Object {
    private ASN1Integer version;
    private AlgorithmIdentifier compressionAlgorithm;
    private ContentInfo encapContentInfo;

    public CompressedData(AlgorithmIdentifier compressionAlgorithm, ContentInfo encapContentInfo) {
        this.version = new ASN1Integer(0L);
        this.compressionAlgorithm = compressionAlgorithm;
        this.encapContentInfo = encapContentInfo;
    }

    private CompressedData(ASN1Sequence seq) {
        this.version = (ASN1Integer)seq.getObjectAt(0);
        this.compressionAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.encapContentInfo = ContentInfo.getInstance(seq.getObjectAt(2));
    }

    public static CompressedData getInstance(ASN1TaggedObject ato, boolean isExplicit) {
        return CompressedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)ato, (boolean)isExplicit));
    }

    public static CompressedData getInstance(Object obj) {
        if (obj instanceof CompressedData) {
            return (CompressedData)((Object)obj);
        }
        if (obj != null) {
            return new CompressedData(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
        return this.compressionAlgorithm;
    }

    public ContentInfo getEncapContentInfo() {
        return this.encapContentInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.compressionAlgorithm);
        v.add((ASN1Encodable)this.encapContentInfo);
        return new BERSequence(v);
    }
}

