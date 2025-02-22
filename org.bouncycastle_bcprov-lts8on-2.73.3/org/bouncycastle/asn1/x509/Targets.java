/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Target;

public class Targets
extends ASN1Object {
    private ASN1Sequence targets;

    public static Targets getInstance(Object obj) {
        if (obj instanceof Targets) {
            return (Targets)obj;
        }
        if (obj != null) {
            return new Targets(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private Targets(ASN1Sequence targets) {
        this.targets = targets;
    }

    public Targets(Target[] targets) {
        this.targets = new DERSequence(targets);
    }

    public Target[] getTargets() {
        Target[] targs = new Target[this.targets.size()];
        int count = 0;
        Enumeration e = this.targets.getObjects();
        while (e.hasMoreElements()) {
            targs[count++] = Target.getInstance(e.nextElement());
        }
        return targs;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}

