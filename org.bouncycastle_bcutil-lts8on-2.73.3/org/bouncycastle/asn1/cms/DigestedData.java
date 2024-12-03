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
 *  org.bouncycastle.asn1.BERSequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
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
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class DigestedData
extends ASN1Object {
    private ASN1Integer version;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapContentInfo;
    private ASN1OctetString digest;

    public DigestedData(AlgorithmIdentifier digestAlgorithm, ContentInfo encapContentInfo, byte[] digest) {
        this.version = new ASN1Integer(0L);
        this.digestAlgorithm = digestAlgorithm;
        this.encapContentInfo = encapContentInfo;
        this.digest = new DEROctetString(Arrays.clone((byte[])digest));
    }

    private DigestedData(ASN1Sequence seq) {
        this.version = (ASN1Integer)seq.getObjectAt(0);
        this.digestAlgorithm = AlgorithmIdentifier.getInstance((Object)seq.getObjectAt(1));
        this.encapContentInfo = ContentInfo.getInstance(seq.getObjectAt(2));
        this.digest = ASN1OctetString.getInstance((Object)seq.getObjectAt(3));
    }

    public static DigestedData getInstance(ASN1TaggedObject ato, boolean isExplicit) {
        return DigestedData.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)ato, (boolean)isExplicit));
    }

    public static DigestedData getInstance(Object obj) {
        if (obj instanceof DigestedData) {
            return (DigestedData)((Object)obj);
        }
        if (obj != null) {
            return new DigestedData(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ASN1Integer getVersion() {
        return this.version;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public ContentInfo getEncapContentInfo() {
        return this.encapContentInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.version);
        v.add((ASN1Encodable)this.digestAlgorithm);
        v.add((ASN1Encodable)this.encapContentInfo);
        v.add((ASN1Encodable)this.digest);
        return new BERSequence(v);
    }

    public byte[] getDigest() {
        return Arrays.clone((byte[])this.digest.getOctets());
    }
}

