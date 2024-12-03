/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.tsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampChain;

public class ArchiveTimeStampSequence
extends ASN1Object {
    private ASN1Sequence archiveTimeStampChains;

    public static ArchiveTimeStampSequence getInstance(Object obj) {
        if (obj instanceof ArchiveTimeStampSequence) {
            return (ArchiveTimeStampSequence)((Object)obj);
        }
        if (obj != null) {
            return new ArchiveTimeStampSequence(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    private ArchiveTimeStampSequence(ASN1Sequence sequence) {
        ASN1EncodableVector vector = new ASN1EncodableVector(sequence.size());
        Enumeration objects = sequence.getObjects();
        while (objects.hasMoreElements()) {
            vector.add((ASN1Encodable)ArchiveTimeStampChain.getInstance(objects.nextElement()));
        }
        this.archiveTimeStampChains = new DERSequence(vector);
    }

    public ArchiveTimeStampSequence(ArchiveTimeStampChain archiveTimeStampChain) {
        this.archiveTimeStampChains = new DERSequence((ASN1Encodable)archiveTimeStampChain);
    }

    public ArchiveTimeStampSequence(ArchiveTimeStampChain[] archiveTimeStampChains) {
        this.archiveTimeStampChains = new DERSequence((ASN1Encodable[])archiveTimeStampChains);
    }

    public ArchiveTimeStampChain[] getArchiveTimeStampChains() {
        ArchiveTimeStampChain[] rv = new ArchiveTimeStampChain[this.archiveTimeStampChains.size()];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = ArchiveTimeStampChain.getInstance(this.archiveTimeStampChains.getObjectAt(i));
        }
        return rv;
    }

    public int size() {
        return this.archiveTimeStampChains.size();
    }

    public ArchiveTimeStampSequence append(ArchiveTimeStampChain chain) {
        ASN1EncodableVector v = new ASN1EncodableVector(this.archiveTimeStampChains.size() + 1);
        for (int i = 0; i != this.archiveTimeStampChains.size(); ++i) {
            v.add(this.archiveTimeStampChains.getObjectAt(i));
        }
        v.add((ASN1Encodable)chain);
        return new ArchiveTimeStampSequence((ASN1Sequence)new DERSequence(v));
    }

    public ASN1Primitive toASN1Primitive() {
        return this.archiveTimeStampChains;
    }
}

