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
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.tsp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class ArchiveTimeStamp
extends ASN1Object {
    private final AlgorithmIdentifier digestAlgorithm;
    private final Attributes attributes;
    private final ASN1Sequence reducedHashTree;
    private final ContentInfo timeStamp;

    public static ArchiveTimeStamp getInstance(Object obj) {
        if (obj instanceof ArchiveTimeStamp) {
            return (ArchiveTimeStamp)((Object)obj);
        }
        if (obj != null) {
            return new ArchiveTimeStamp(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ArchiveTimeStamp(AlgorithmIdentifier digestAlgorithm, PartialHashtree[] reducedHashTree, ContentInfo timeStamp) {
        this(digestAlgorithm, null, reducedHashTree, timeStamp);
    }

    public ArchiveTimeStamp(ContentInfo timeStamp) {
        this(null, null, null, timeStamp);
    }

    public ArchiveTimeStamp(AlgorithmIdentifier digestAlgorithm, Attributes attributes, PartialHashtree[] reducedHashTree, ContentInfo timeStamp) {
        this.digestAlgorithm = digestAlgorithm;
        this.attributes = attributes;
        this.reducedHashTree = reducedHashTree != null ? new DERSequence((ASN1Encodable[])reducedHashTree) : null;
        this.timeStamp = timeStamp;
    }

    private ArchiveTimeStamp(ASN1Sequence sequence) {
        if (sequence.size() < 1 || sequence.size() > 4) {
            throw new IllegalArgumentException("wrong sequence size in constructor: " + sequence.size());
        }
        AlgorithmIdentifier digAlg = null;
        Attributes attrs = null;
        ASN1Sequence rHashTree = null;
        block5: for (int i = 0; i < sequence.size() - 1; ++i) {
            ASN1Encodable obj = sequence.getObjectAt(i);
            if (!(obj instanceof ASN1TaggedObject)) continue;
            ASN1TaggedObject taggedObject = ASN1TaggedObject.getInstance((Object)obj);
            switch (taggedObject.getTagNo()) {
                case 0: {
                    digAlg = AlgorithmIdentifier.getInstance((ASN1TaggedObject)taggedObject, (boolean)false);
                    continue block5;
                }
                case 1: {
                    attrs = Attributes.getInstance(taggedObject, false);
                    continue block5;
                }
                case 2: {
                    rHashTree = ASN1Sequence.getInstance((ASN1TaggedObject)taggedObject, (boolean)false);
                    continue block5;
                }
                default: {
                    throw new IllegalArgumentException("invalid tag no in constructor: " + taggedObject.getTagNo());
                }
            }
        }
        this.digestAlgorithm = digAlg;
        this.attributes = attrs;
        this.reducedHashTree = rHashTree;
        this.timeStamp = ContentInfo.getInstance(sequence.getObjectAt(sequence.size() - 1));
    }

    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        if (this.digestAlgorithm != null) {
            return this.digestAlgorithm;
        }
        return this.getTimeStampInfo().getMessageImprint().getHashAlgorithm();
    }

    public byte[] getTimeStampDigestValue() {
        return this.getTimeStampInfo().getMessageImprint().getHashedMessage();
    }

    private TSTInfo getTimeStampInfo() {
        if (this.timeStamp.getContentType().equals((ASN1Primitive)CMSObjectIdentifiers.signedData)) {
            SignedData tsData = SignedData.getInstance(this.timeStamp.getContent());
            if (tsData.getEncapContentInfo().getContentType().equals((ASN1Primitive)PKCSObjectIdentifiers.id_ct_TSTInfo)) {
                TSTInfo tstData = TSTInfo.getInstance(ASN1OctetString.getInstance((Object)tsData.getEncapContentInfo().getContent()).getOctets());
                return tstData;
            }
            throw new IllegalStateException("cannot parse time stamp");
        }
        throw new IllegalStateException("cannot identify algorithm identifier for digest");
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public PartialHashtree getHashTreeLeaf() {
        if (this.reducedHashTree == null) {
            return null;
        }
        return PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(0));
    }

    public PartialHashtree[] getReducedHashTree() {
        if (this.reducedHashTree == null) {
            return null;
        }
        PartialHashtree[] rv = new PartialHashtree[this.reducedHashTree.size()];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = PartialHashtree.getInstance(this.reducedHashTree.getObjectAt(i));
        }
        return rv;
    }

    public ContentInfo getTimeStamp() {
        return this.timeStamp;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        if (this.digestAlgorithm != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.digestAlgorithm));
        }
        if (this.attributes != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.attributes));
        }
        if (this.reducedHashTree != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 2, (ASN1Encodable)this.reducedHashTree));
        }
        v.add((ASN1Encodable)this.timeStamp);
        return new DERSequence(v);
    }
}

