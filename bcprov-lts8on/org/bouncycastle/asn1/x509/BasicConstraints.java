/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class BasicConstraints
extends ASN1Object {
    ASN1Boolean cA = ASN1Boolean.getInstance(false);
    ASN1Integer pathLenConstraint = null;

    public static BasicConstraints getInstance(ASN1TaggedObject obj, boolean explicit) {
        return BasicConstraints.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static BasicConstraints getInstance(Object obj) {
        if (obj instanceof BasicConstraints) {
            return (BasicConstraints)obj;
        }
        if (obj != null) {
            return new BasicConstraints(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static BasicConstraints fromExtensions(Extensions extensions) {
        return BasicConstraints.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.basicConstraints));
    }

    private BasicConstraints(ASN1Sequence seq) {
        if (seq.size() == 0) {
            this.cA = null;
            this.pathLenConstraint = null;
        } else {
            if (seq.getObjectAt(0) instanceof ASN1Boolean) {
                this.cA = ASN1Boolean.getInstance(seq.getObjectAt(0));
            } else {
                this.cA = null;
                this.pathLenConstraint = ASN1Integer.getInstance(seq.getObjectAt(0));
            }
            if (seq.size() > 1) {
                if (this.cA != null) {
                    this.pathLenConstraint = ASN1Integer.getInstance(seq.getObjectAt(1));
                } else {
                    throw new IllegalArgumentException("wrong sequence in constructor");
                }
            }
        }
    }

    public BasicConstraints(boolean cA) {
        this.cA = cA ? ASN1Boolean.getInstance(true) : null;
        this.pathLenConstraint = null;
    }

    public BasicConstraints(int pathLenConstraint) {
        this.cA = ASN1Boolean.getInstance(true);
        this.pathLenConstraint = new ASN1Integer(pathLenConstraint);
    }

    public boolean isCA() {
        return this.cA != null && this.cA.isTrue();
    }

    public BigInteger getPathLenConstraint() {
        if (this.pathLenConstraint != null) {
            return this.pathLenConstraint.getValue();
        }
        return null;
    }

    public ASN1Integer getPathLenConstraintInteger() {
        return this.pathLenConstraint;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        if (this.cA != null) {
            v.add(this.cA);
        }
        if (this.pathLenConstraint != null) {
            v.add(this.pathLenConstraint);
        }
        return new DERSequence(v);
    }

    public String toString() {
        if (this.pathLenConstraint == null) {
            return "BasicConstraints: isCa(" + this.isCA() + ")";
        }
        return "BasicConstraints: isCa(" + this.isCA() + "), pathLenConstraint = " + this.pathLenConstraint.getValue();
    }
}

