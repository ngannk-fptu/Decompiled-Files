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
import org.bouncycastle.asn1.x509.Targets;

public class TargetInformation
extends ASN1Object {
    private ASN1Sequence targets;

    public static TargetInformation getInstance(Object obj) {
        if (obj instanceof TargetInformation) {
            return (TargetInformation)obj;
        }
        if (obj != null) {
            return new TargetInformation(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private TargetInformation(ASN1Sequence seq) {
        this.targets = seq;
    }

    public Targets[] getTargetsObjects() {
        Targets[] copy = new Targets[this.targets.size()];
        int count = 0;
        Enumeration e = this.targets.getObjects();
        while (e.hasMoreElements()) {
            copy[count++] = Targets.getInstance(e.nextElement());
        }
        return copy;
    }

    public TargetInformation(Targets targets) {
        this.targets = new DERSequence(targets);
    }

    public TargetInformation(Target[] targets) {
        this(new Targets(targets));
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.targets;
    }
}

