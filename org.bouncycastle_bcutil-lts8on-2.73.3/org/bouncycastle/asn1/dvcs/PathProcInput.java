/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.PolicyInformation
 */
package org.bouncycastle.asn1.dvcs;

import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class PathProcInput
extends ASN1Object {
    private PolicyInformation[] acceptablePolicySet;
    private boolean inhibitPolicyMapping = false;
    private boolean explicitPolicyReqd = false;
    private boolean inhibitAnyPolicy = false;

    public PathProcInput(PolicyInformation[] acceptablePolicySet) {
        this.acceptablePolicySet = this.copy(acceptablePolicySet);
    }

    public PathProcInput(PolicyInformation[] acceptablePolicySet, boolean inhibitPolicyMapping, boolean explicitPolicyReqd, boolean inhibitAnyPolicy) {
        this.acceptablePolicySet = this.copy(acceptablePolicySet);
        this.inhibitPolicyMapping = inhibitPolicyMapping;
        this.explicitPolicyReqd = explicitPolicyReqd;
        this.inhibitAnyPolicy = inhibitAnyPolicy;
    }

    private static PolicyInformation[] fromSequence(ASN1Sequence seq) {
        PolicyInformation[] tmp = new PolicyInformation[seq.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = PolicyInformation.getInstance((Object)seq.getObjectAt(i));
        }
        return tmp;
    }

    public static PathProcInput getInstance(Object obj) {
        if (obj instanceof PathProcInput) {
            return (PathProcInput)((Object)obj);
        }
        if (obj != null) {
            ASN1Sequence seq = ASN1Sequence.getInstance((Object)obj);
            ASN1Sequence policies = ASN1Sequence.getInstance((Object)seq.getObjectAt(0));
            PathProcInput result = new PathProcInput(PathProcInput.fromSequence(policies));
            block4: for (int i = 1; i < seq.size(); ++i) {
                ASN1Encodable o = seq.getObjectAt(i);
                if (o instanceof ASN1Boolean) {
                    ASN1Boolean x = ASN1Boolean.getInstance((Object)o);
                    result.setInhibitPolicyMapping(x.isTrue());
                    continue;
                }
                if (!(o instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject t = ASN1TaggedObject.getInstance((Object)o);
                switch (t.getTagNo()) {
                    case 0: {
                        ASN1Boolean x = ASN1Boolean.getInstance((ASN1TaggedObject)t, (boolean)false);
                        result.setExplicitPolicyReqd(x.isTrue());
                        continue block4;
                    }
                    case 1: {
                        ASN1Boolean x = ASN1Boolean.getInstance((ASN1TaggedObject)t, (boolean)false);
                        result.setInhibitAnyPolicy(x.isTrue());
                        continue block4;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown tag encountered: " + t.getTagNo());
                    }
                }
            }
            return result;
        }
        return null;
    }

    public static PathProcInput getInstance(ASN1TaggedObject obj, boolean explicit) {
        return PathProcInput.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        ASN1EncodableVector pV = new ASN1EncodableVector(this.acceptablePolicySet.length);
        for (int i = 0; i != this.acceptablePolicySet.length; ++i) {
            pV.add((ASN1Encodable)this.acceptablePolicySet[i]);
        }
        v.add((ASN1Encodable)new DERSequence(pV));
        if (this.inhibitPolicyMapping) {
            v.add((ASN1Encodable)ASN1Boolean.getInstance((boolean)this.inhibitPolicyMapping));
        }
        if (this.explicitPolicyReqd) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)ASN1Boolean.getInstance((boolean)this.explicitPolicyReqd)));
        }
        if (this.inhibitAnyPolicy) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)ASN1Boolean.getInstance((boolean)this.inhibitAnyPolicy)));
        }
        return new DERSequence(v);
    }

    public String toString() {
        return "PathProcInput: {\nacceptablePolicySet: " + Arrays.asList(this.acceptablePolicySet) + "\ninhibitPolicyMapping: " + this.inhibitPolicyMapping + "\nexplicitPolicyReqd: " + this.explicitPolicyReqd + "\ninhibitAnyPolicy: " + this.inhibitAnyPolicy + "\n}\n";
    }

    public PolicyInformation[] getAcceptablePolicySet() {
        return this.copy(this.acceptablePolicySet);
    }

    public boolean isInhibitPolicyMapping() {
        return this.inhibitPolicyMapping;
    }

    private void setInhibitPolicyMapping(boolean inhibitPolicyMapping) {
        this.inhibitPolicyMapping = inhibitPolicyMapping;
    }

    public boolean isExplicitPolicyReqd() {
        return this.explicitPolicyReqd;
    }

    private void setExplicitPolicyReqd(boolean explicitPolicyReqd) {
        this.explicitPolicyReqd = explicitPolicyReqd;
    }

    public boolean isInhibitAnyPolicy() {
        return this.inhibitAnyPolicy;
    }

    private void setInhibitAnyPolicy(boolean inhibitAnyPolicy) {
        this.inhibitAnyPolicy = inhibitAnyPolicy;
    }

    private PolicyInformation[] copy(PolicyInformation[] policySet) {
        PolicyInformation[] rv = new PolicyInformation[policySet.length];
        System.arraycopy(policySet, 0, rv, 0, rv.length);
        return rv;
    }
}

