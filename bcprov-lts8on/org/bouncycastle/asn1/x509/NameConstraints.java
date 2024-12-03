/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.GeneralSubtree;

public class NameConstraints
extends ASN1Object {
    private GeneralSubtree[] permitted;
    private GeneralSubtree[] excluded;

    public static NameConstraints getInstance(Object obj) {
        if (obj instanceof NameConstraints) {
            return (NameConstraints)obj;
        }
        if (obj != null) {
            return new NameConstraints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private NameConstraints(ASN1Sequence seq) {
        Enumeration e = seq.getObjects();
        block4: while (e.hasMoreElements()) {
            ASN1TaggedObject o = ASN1TaggedObject.getInstance(e.nextElement());
            switch (o.getTagNo()) {
                case 0: {
                    this.permitted = this.createArray(ASN1Sequence.getInstance(o, false));
                    continue block4;
                }
                case 1: {
                    this.excluded = this.createArray(ASN1Sequence.getInstance(o, false));
                    continue block4;
                }
            }
            throw new IllegalArgumentException("Unknown tag encountered: " + o.getTagNo());
        }
    }

    public NameConstraints(GeneralSubtree[] permitted, GeneralSubtree[] excluded) {
        this.permitted = NameConstraints.cloneSubtree(permitted);
        this.excluded = NameConstraints.cloneSubtree(excluded);
    }

    private GeneralSubtree[] createArray(ASN1Sequence subtree) {
        GeneralSubtree[] ar = new GeneralSubtree[subtree.size()];
        for (int i = 0; i != ar.length; ++i) {
            ar[i] = GeneralSubtree.getInstance(subtree.getObjectAt(i));
        }
        return ar;
    }

    public GeneralSubtree[] getPermittedSubtrees() {
        return NameConstraints.cloneSubtree(this.permitted);
    }

    public GeneralSubtree[] getExcludedSubtrees() {
        return NameConstraints.cloneSubtree(this.excluded);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.permitted != null) {
            v.add(new DERTaggedObject(false, 0, (ASN1Encodable)new DERSequence(this.permitted)));
        }
        if (this.excluded != null) {
            v.add(new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence(this.excluded)));
        }
        return new DERSequence(v);
    }

    private static GeneralSubtree[] cloneSubtree(GeneralSubtree[] subtrees) {
        if (subtrees != null) {
            GeneralSubtree[] rv = new GeneralSubtree[subtrees.length];
            System.arraycopy(subtrees, 0, rv, 0, rv.length);
            return rv;
        }
        return null;
    }
}

