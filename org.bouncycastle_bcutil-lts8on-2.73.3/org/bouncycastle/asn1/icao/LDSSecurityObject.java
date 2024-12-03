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
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.asn1.icao;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.icao.DataGroupHash;
import org.bouncycastle.asn1.icao.ICAOObjectIdentifiers;
import org.bouncycastle.asn1.icao.LDSVersionInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class LDSSecurityObject
extends ASN1Object
implements ICAOObjectIdentifiers {
    public static final int ub_DataGroups = 16;
    private ASN1Integer version = new ASN1Integer(0L);
    private AlgorithmIdentifier digestAlgorithmIdentifier;
    private DataGroupHash[] datagroupHash;
    private LDSVersionInfo versionInfo;

    public static LDSSecurityObject getInstance(Object obj) {
        if (obj instanceof LDSSecurityObject) {
            return (LDSSecurityObject)obj;
        }
        if (obj != null) {
            return new LDSSecurityObject(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private LDSSecurityObject(ASN1Sequence seq) {
        if (seq == null || seq.size() == 0) {
            throw new IllegalArgumentException("null or empty sequence passed.");
        }
        Enumeration e = seq.getObjects();
        this.version = ASN1Integer.getInstance(e.nextElement());
        this.digestAlgorithmIdentifier = AlgorithmIdentifier.getInstance(e.nextElement());
        ASN1Sequence datagroupHashSeq = ASN1Sequence.getInstance(e.nextElement());
        if (this.version.hasValue(1)) {
            this.versionInfo = LDSVersionInfo.getInstance(e.nextElement());
        }
        this.checkDatagroupHashSeqSize(datagroupHashSeq.size());
        this.datagroupHash = new DataGroupHash[datagroupHashSeq.size()];
        for (int i = 0; i < datagroupHashSeq.size(); ++i) {
            this.datagroupHash[i] = DataGroupHash.getInstance(datagroupHashSeq.getObjectAt(i));
        }
    }

    public LDSSecurityObject(AlgorithmIdentifier digestAlgorithmIdentifier, DataGroupHash[] datagroupHash) {
        this.version = new ASN1Integer(0L);
        this.digestAlgorithmIdentifier = digestAlgorithmIdentifier;
        this.datagroupHash = this.copy(datagroupHash);
        this.checkDatagroupHashSeqSize(datagroupHash.length);
    }

    public LDSSecurityObject(AlgorithmIdentifier digestAlgorithmIdentifier, DataGroupHash[] datagroupHash, LDSVersionInfo versionInfo) {
        this.version = new ASN1Integer(1L);
        this.digestAlgorithmIdentifier = digestAlgorithmIdentifier;
        this.datagroupHash = this.copy(datagroupHash);
        this.versionInfo = versionInfo;
        this.checkDatagroupHashSeqSize(datagroupHash.length);
    }

    private void checkDatagroupHashSeqSize(int size) {
        if (size < 2 || size > 16) {
            throw new IllegalArgumentException("wrong size in DataGroupHashValues : not in (2..16)");
        }
    }

    public int getVersion() {
        return this.version.intValueExact();
    }

    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.digestAlgorithmIdentifier;
    }

    public DataGroupHash[] getDatagroupHash() {
        return this.copy(this.datagroupHash);
    }

    public LDSVersionInfo getVersionInfo() {
        return this.versionInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector seq = new ASN1EncodableVector(4);
        seq.add((ASN1Encodable)this.version);
        seq.add((ASN1Encodable)this.digestAlgorithmIdentifier);
        seq.add((ASN1Encodable)new DERSequence((ASN1Encodable[])this.datagroupHash));
        if (this.versionInfo != null) {
            seq.add((ASN1Encodable)this.versionInfo);
        }
        return new DERSequence(seq);
    }

    private DataGroupHash[] copy(DataGroupHash[] dgHash) {
        DataGroupHash[] rv = new DataGroupHash[dgHash.length];
        System.arraycopy(dgHash, 0, rv, 0, rv.length);
        return rv;
    }
}

