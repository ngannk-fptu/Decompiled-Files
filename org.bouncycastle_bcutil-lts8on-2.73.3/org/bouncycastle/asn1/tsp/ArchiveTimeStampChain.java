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
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;

public class ArchiveTimeStampChain
extends ASN1Object {
    private ASN1Sequence archiveTimeStamps;

    public static ArchiveTimeStampChain getInstance(Object obj) {
        if (obj instanceof ArchiveTimeStampChain) {
            return (ArchiveTimeStampChain)((Object)obj);
        }
        if (obj != null) {
            return new ArchiveTimeStampChain(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public ArchiveTimeStampChain(ArchiveTimeStamp archiveTimeStamp) {
        this.archiveTimeStamps = new DERSequence((ASN1Encodable)archiveTimeStamp);
    }

    public ArchiveTimeStampChain(ArchiveTimeStamp[] archiveTimeStamps) {
        this.archiveTimeStamps = new DERSequence((ASN1Encodable[])archiveTimeStamps);
    }

    private ArchiveTimeStampChain(ASN1Sequence sequence) {
        ASN1EncodableVector vector = new ASN1EncodableVector(sequence.size());
        Enumeration objects = sequence.getObjects();
        while (objects.hasMoreElements()) {
            vector.add((ASN1Encodable)ArchiveTimeStamp.getInstance(objects.nextElement()));
        }
        this.archiveTimeStamps = new DERSequence(vector);
    }

    public ArchiveTimeStamp[] getArchiveTimestamps() {
        ArchiveTimeStamp[] rv = new ArchiveTimeStamp[this.archiveTimeStamps.size()];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = ArchiveTimeStamp.getInstance(this.archiveTimeStamps.getObjectAt(i));
        }
        return rv;
    }

    public ArchiveTimeStampChain append(ArchiveTimeStamp archiveTimeStamp) {
        ASN1EncodableVector v = new ASN1EncodableVector(this.archiveTimeStamps.size() + 1);
        for (int i = 0; i != this.archiveTimeStamps.size(); ++i) {
            v.add(this.archiveTimeStamps.getObjectAt(i));
        }
        v.add((ASN1Encodable)archiveTimeStamp);
        return new ArchiveTimeStampChain((ASN1Sequence)new DERSequence(v));
    }

    public ASN1Primitive toASN1Primitive() {
        return this.archiveTimeStamps;
    }
}

