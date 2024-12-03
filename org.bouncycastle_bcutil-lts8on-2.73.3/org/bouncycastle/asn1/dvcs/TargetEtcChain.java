/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 */
package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.dvcs.CertEtcToken;
import org.bouncycastle.asn1.dvcs.PathProcInput;

public class TargetEtcChain
extends ASN1Object {
    private CertEtcToken target;
    private ASN1Sequence chain;
    private PathProcInput pathProcInput;

    public TargetEtcChain(CertEtcToken target) {
        this(target, null, null);
    }

    public TargetEtcChain(CertEtcToken target, CertEtcToken[] chain) {
        this(target, chain, null);
    }

    public TargetEtcChain(CertEtcToken target, PathProcInput pathProcInput) {
        this(target, null, pathProcInput);
    }

    public TargetEtcChain(CertEtcToken target, CertEtcToken[] chain, PathProcInput pathProcInput) {
        this.target = target;
        if (chain != null) {
            this.chain = new DERSequence((ASN1Encodable[])chain);
        }
        this.pathProcInput = pathProcInput;
    }

    private TargetEtcChain(ASN1Sequence seq) {
        int i = 0;
        ASN1Encodable obj = seq.getObjectAt(i++);
        this.target = CertEtcToken.getInstance(obj);
        if (seq.size() > 1) {
            if ((obj = seq.getObjectAt(i++)) instanceof ASN1TaggedObject) {
                this.extractPathProcInput(obj);
            } else {
                this.chain = ASN1Sequence.getInstance((Object)obj);
                if (seq.size() > 2) {
                    obj = seq.getObjectAt(i);
                    this.extractPathProcInput(obj);
                }
            }
        }
    }

    private void extractPathProcInput(ASN1Encodable obj) {
        ASN1TaggedObject tagged = ASN1TaggedObject.getInstance((Object)obj);
        switch (tagged.getTagNo()) {
            case 0: {
                this.pathProcInput = PathProcInput.getInstance(tagged, false);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown tag encountered: " + tagged.getTagNo());
            }
        }
    }

    public static TargetEtcChain getInstance(Object obj) {
        if (obj instanceof TargetEtcChain) {
            return (TargetEtcChain)((Object)obj);
        }
        if (obj != null) {
            return new TargetEtcChain(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static TargetEtcChain getInstance(ASN1TaggedObject obj, boolean explicit) {
        return TargetEtcChain.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);
        v.add((ASN1Encodable)this.target);
        if (this.chain != null) {
            v.add((ASN1Encodable)this.chain);
        }
        if (this.pathProcInput != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.pathProcInput));
        }
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("TargetEtcChain {\n");
        s.append("target: " + (Object)((Object)this.target) + "\n");
        if (this.chain != null) {
            s.append("chain: " + this.chain + "\n");
        }
        if (this.pathProcInput != null) {
            s.append("pathProcInput: " + (Object)((Object)this.pathProcInput) + "\n");
        }
        s.append("}\n");
        return s.toString();
    }

    public CertEtcToken getTarget() {
        return this.target;
    }

    public CertEtcToken[] getChain() {
        if (this.chain != null) {
            return CertEtcToken.arrayFromSequence(this.chain);
        }
        return null;
    }

    public PathProcInput getPathProcInput() {
        return this.pathProcInput;
    }

    public static TargetEtcChain[] arrayFromSequence(ASN1Sequence seq) {
        TargetEtcChain[] tmp = new TargetEtcChain[seq.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = TargetEtcChain.getInstance(seq.getObjectAt(i));
        }
        return tmp;
    }
}

