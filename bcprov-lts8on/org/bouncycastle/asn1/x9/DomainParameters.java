/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x9.ValidationParams;

public class DomainParameters
extends ASN1Object {
    private final ASN1Integer p;
    private final ASN1Integer g;
    private final ASN1Integer q;
    private final ASN1Integer j;
    private final ValidationParams validationParams;

    public static DomainParameters getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DomainParameters.getInstance(ASN1Sequence.getInstance(obj, explicit));
    }

    public static DomainParameters getInstance(Object obj) {
        if (obj instanceof DomainParameters) {
            return (DomainParameters)obj;
        }
        if (obj != null) {
            return new DomainParameters(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public DomainParameters(BigInteger p, BigInteger g, BigInteger q, BigInteger j, ValidationParams validationParams) {
        if (p == null) {
            throw new IllegalArgumentException("'p' cannot be null");
        }
        if (g == null) {
            throw new IllegalArgumentException("'g' cannot be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("'q' cannot be null");
        }
        this.p = new ASN1Integer(p);
        this.g = new ASN1Integer(g);
        this.q = new ASN1Integer(q);
        this.j = j != null ? new ASN1Integer(j) : null;
        this.validationParams = validationParams;
    }

    private DomainParameters(ASN1Sequence seq) {
        if (seq.size() < 3 || seq.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        this.p = ASN1Integer.getInstance(e.nextElement());
        this.g = ASN1Integer.getInstance(e.nextElement());
        this.q = ASN1Integer.getInstance(e.nextElement());
        ASN1Encodable next = DomainParameters.getNext(e);
        if (next != null && next instanceof ASN1Integer) {
            this.j = ASN1Integer.getInstance(next);
            next = DomainParameters.getNext(e);
        } else {
            this.j = null;
        }
        this.validationParams = next != null ? ValidationParams.getInstance(next.toASN1Primitive()) : null;
    }

    private static ASN1Encodable getNext(Enumeration e) {
        return e.hasMoreElements() ? (ASN1Encodable)e.nextElement() : null;
    }

    public BigInteger getP() {
        return this.p.getPositiveValue();
    }

    public BigInteger getG() {
        return this.g.getPositiveValue();
    }

    public BigInteger getQ() {
        return this.q.getPositiveValue();
    }

    public BigInteger getJ() {
        if (this.j == null) {
            return null;
        }
        return this.j.getPositiveValue();
    }

    public ValidationParams getValidationParams() {
        return this.validationParams;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);
        v.add(this.p);
        v.add(this.g);
        v.add(this.q);
        if (this.j != null) {
            v.add(this.j);
        }
        if (this.validationParams != null) {
            v.add(this.validationParams);
        }
        return new DERSequence(v);
    }
}

