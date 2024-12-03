/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampChain;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.CryptoInfos;
import org.bouncycastle.asn1.tsp.EncryptionInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class EvidenceRecord
extends ASN1Object {
    private static final ASN1ObjectIdentifier OID = new ASN1ObjectIdentifier("1.3.6.1.5.5.11.0.2.1");
    private ASN1Integer version = new ASN1Integer(1L);
    private ASN1Sequence digestAlgorithms;
    private CryptoInfos cryptoInfos;
    private EncryptionInfo encryptionInfo;
    private ArchiveTimeStampSequence archiveTimeStampSequence;

    public static EvidenceRecord getInstance(Object obj) {
        if (obj instanceof EvidenceRecord) {
            return (EvidenceRecord)((Object)obj);
        }
        if (obj != null) {
            return new EvidenceRecord(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static EvidenceRecord getInstance(ASN1TaggedObject tagged, boolean explicit) {
        return EvidenceRecord.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)tagged, (boolean)explicit));
    }

    private EvidenceRecord(EvidenceRecord evidenceRecord, ArchiveTimeStampSequence replacementSequence, ArchiveTimeStamp newChainTimeStamp) {
        this.version = evidenceRecord.version;
        if (newChainTimeStamp != null) {
            AlgorithmIdentifier algId = newChainTimeStamp.getDigestAlgorithmIdentifier();
            ASN1EncodableVector vector = new ASN1EncodableVector();
            Enumeration enumeration = evidenceRecord.digestAlgorithms.getObjects();
            boolean found = false;
            while (enumeration.hasMoreElements()) {
                AlgorithmIdentifier algorithmIdentifier = AlgorithmIdentifier.getInstance(enumeration.nextElement());
                vector.add((ASN1Encodable)algorithmIdentifier);
                if (!algorithmIdentifier.equals((Object)algId)) continue;
                found = true;
                break;
            }
            if (!found) {
                vector.add((ASN1Encodable)algId);
                this.digestAlgorithms = new DERSequence(vector);
            } else {
                this.digestAlgorithms = evidenceRecord.digestAlgorithms;
            }
        } else {
            this.digestAlgorithms = evidenceRecord.digestAlgorithms;
        }
        this.cryptoInfos = evidenceRecord.cryptoInfos;
        this.encryptionInfo = evidenceRecord.encryptionInfo;
        this.archiveTimeStampSequence = replacementSequence;
    }

    public EvidenceRecord(CryptoInfos cryptoInfos, EncryptionInfo encryptionInfo, ArchiveTimeStamp archiveTimeStamp) {
        this.digestAlgorithms = new DERSequence((ASN1Encodable)archiveTimeStamp.getDigestAlgorithmIdentifier());
        this.cryptoInfos = cryptoInfos;
        this.encryptionInfo = encryptionInfo;
        this.archiveTimeStampSequence = new ArchiveTimeStampSequence(new ArchiveTimeStampChain(archiveTimeStamp));
    }

    public EvidenceRecord(AlgorithmIdentifier[] digestAlgorithms, CryptoInfos cryptoInfos, EncryptionInfo encryptionInfo, ArchiveTimeStampSequence archiveTimeStampSequence) {
        this.digestAlgorithms = new DERSequence((ASN1Encodable[])digestAlgorithms);
        this.cryptoInfos = cryptoInfos;
        this.encryptionInfo = encryptionInfo;
        this.archiveTimeStampSequence = archiveTimeStampSequence;
    }

    private EvidenceRecord(ASN1Sequence sequence) {
        if (sequence.size() < 3 && sequence.size() > 5) {
            throw new IllegalArgumentException("wrong sequence size in constructor: " + sequence.size());
        }
        ASN1Integer versionNumber = ASN1Integer.getInstance((Object)sequence.getObjectAt(0));
        if (!versionNumber.hasValue(1)) {
            throw new IllegalArgumentException("incompatible version");
        }
        this.version = versionNumber;
        this.digestAlgorithms = ASN1Sequence.getInstance((Object)sequence.getObjectAt(1));
        for (int i = 2; i != sequence.size() - 1; ++i) {
            ASN1Encodable object = sequence.getObjectAt(i);
            if (object instanceof ASN1TaggedObject) {
                ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)object;
                switch (asn1TaggedObject.getTagNo()) {
                    case 0: {
                        this.cryptoInfos = CryptoInfos.getInstance(asn1TaggedObject, false);
                        break;
                    }
                    case 1: {
                        this.encryptionInfo = EncryptionInfo.getInstance(asn1TaggedObject, false);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unknown tag in getInstance: " + asn1TaggedObject.getTagNo());
                    }
                }
                continue;
            }
            throw new IllegalArgumentException("unknown object in getInstance: " + object.getClass().getName());
        }
        this.archiveTimeStampSequence = ArchiveTimeStampSequence.getInstance(sequence.getObjectAt(sequence.size() - 1));
    }

    public AlgorithmIdentifier[] getDigestAlgorithms() {
        AlgorithmIdentifier[] rv = new AlgorithmIdentifier[this.digestAlgorithms.size()];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = AlgorithmIdentifier.getInstance((Object)this.digestAlgorithms.getObjectAt(i));
        }
        return rv;
    }

    public ArchiveTimeStampSequence getArchiveTimeStampSequence() {
        return this.archiveTimeStampSequence;
    }

    public EvidenceRecord addArchiveTimeStamp(ArchiveTimeStamp ats, boolean newChain) {
        if (newChain) {
            ArchiveTimeStampChain chain = new ArchiveTimeStampChain(ats);
            return new EvidenceRecord(this, this.archiveTimeStampSequence.append(chain), ats);
        }
        ArchiveTimeStampChain[] chains = this.archiveTimeStampSequence.getArchiveTimeStampChains();
        AlgorithmIdentifier digAlg = chains[chains.length - 1].getArchiveTimestamps()[0].getDigestAlgorithmIdentifier();
        if (!digAlg.equals((Object)ats.getDigestAlgorithmIdentifier())) {
            throw new IllegalArgumentException("mismatch of digest algorithm in addArchiveTimeStamp");
        }
        chains[chains.length - 1] = chains[chains.length - 1].append(ats);
        return new EvidenceRecord(this, new ArchiveTimeStampSequence(chains), null);
    }

    public String toString() {
        return "EvidenceRecord: Oid(" + OID + ")";
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vector = new ASN1EncodableVector(5);
        vector.add((ASN1Encodable)this.version);
        vector.add((ASN1Encodable)this.digestAlgorithms);
        if (null != this.cryptoInfos) {
            vector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.cryptoInfos));
        }
        if (null != this.encryptionInfo) {
            vector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)this.encryptionInfo));
        }
        vector.add((ASN1Encodable)this.archiveTimeStampSequence);
        return new DERSequence(vector);
    }
}

