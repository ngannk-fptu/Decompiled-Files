/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.BigIntegers;

public class Point256
extends ASN1Object {
    private final ASN1OctetString x;
    private final ASN1OctetString y;

    public Point256(ASN1OctetString x, ASN1OctetString y) {
        if (x == null || x.getOctets().length != 32) {
            throw new IllegalArgumentException("x must be 32 bytes long");
        }
        if (y == null || y.getOctets().length != 32) {
            throw new IllegalArgumentException("y must be 32 bytes long");
        }
        this.x = x;
        this.y = y;
    }

    private Point256(ASN1Sequence instance) {
        if (instance.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.x = ASN1OctetString.getInstance((Object)instance.getObjectAt(0));
        this.y = ASN1OctetString.getInstance((Object)instance.getObjectAt(1));
        if (this.x.getOctets().length != 32) {
            throw new IllegalArgumentException("x must be 32 bytes long");
        }
        if (this.y.getOctets().length != 32) {
            throw new IllegalArgumentException("y must be 32 bytes long");
        }
    }

    public static Point256 getInstance(Object object) {
        if (object instanceof Point256) {
            return (Point256)((Object)object);
        }
        if (object != null) {
            return new Point256(ASN1Sequence.getInstance((Object)object));
        }
        return null;
    }

    public ASN1OctetString getX() {
        return this.x;
    }

    public ASN1OctetString getY() {
        return this.y;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.x, this.y});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString x;
        private ASN1OctetString y;

        public Builder setX(ASN1OctetString x) {
            this.x = x;
            return this;
        }

        public Builder setY(ASN1OctetString y) {
            this.y = y;
            return this;
        }

        public Builder setX(byte[] x) {
            this.x = new DEROctetString(x);
            return this;
        }

        public Builder setY(byte[] y) {
            this.y = new DEROctetString(y);
            return this;
        }

        public Builder setX(BigInteger x) {
            return this.setX(BigIntegers.asUnsignedByteArray((int)32, (BigInteger)x));
        }

        public Builder setY(BigInteger y) {
            return this.setY(BigIntegers.asUnsignedByteArray((int)32, (BigInteger)y));
        }

        public Point256 createPoint256() {
            return new Point256(this.x, this.y);
        }
    }
}

