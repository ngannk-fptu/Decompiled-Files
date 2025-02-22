/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x500;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class X500Name
extends ASN1Object
implements ASN1Choice {
    private static X500NameStyle defaultStyle = BCStyle.INSTANCE;
    private boolean isHashCodeCalculated;
    private int hashCodeValue;
    private X500NameStyle style;
    private RDN[] rdns;
    private DERSequence rdnSeq;

    private X500Name(X500NameStyle style, X500Name name) {
        this.style = style;
        this.rdns = name.rdns;
        this.rdnSeq = name.rdnSeq;
    }

    public static X500Name getInstance(ASN1TaggedObject obj, boolean explicit) {
        return X500Name.getInstance(ASN1Sequence.getInstance(obj, true));
    }

    public static X500Name getInstance(Object obj) {
        if (obj instanceof X500Name) {
            return (X500Name)obj;
        }
        if (obj != null) {
            return new X500Name(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public static X500Name getInstance(X500NameStyle style, Object obj) {
        if (obj instanceof X500Name) {
            return new X500Name(style, (X500Name)obj);
        }
        if (obj != null) {
            return new X500Name(style, ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private X500Name(ASN1Sequence seq) {
        this(defaultStyle, seq);
    }

    private X500Name(X500NameStyle style, ASN1Sequence seq) {
        this.style = style;
        this.rdns = new RDN[seq.size()];
        boolean inPlace = true;
        int index = 0;
        Enumeration e = seq.getObjects();
        while (e.hasMoreElements()) {
            Object element = e.nextElement();
            RDN rdn = RDN.getInstance(element);
            inPlace &= rdn == element;
            this.rdns[index++] = rdn;
        }
        this.rdnSeq = inPlace ? DERSequence.convert(seq) : new DERSequence(this.rdns);
    }

    public X500Name(RDN[] rDNs) {
        this(defaultStyle, rDNs);
    }

    public X500Name(X500NameStyle style, RDN[] rDNs) {
        this.style = style;
        this.rdns = (RDN[])rDNs.clone();
        this.rdnSeq = new DERSequence(this.rdns);
    }

    public X500Name(String dirName) {
        this(defaultStyle, dirName);
    }

    public X500Name(X500NameStyle style, String dirName) {
        this(style.fromString(dirName));
        this.style = style;
    }

    public RDN[] getRDNs() {
        return (RDN[])this.rdns.clone();
    }

    public ASN1ObjectIdentifier[] getAttributeTypes() {
        int count = this.rdns.length;
        int totalSize = 0;
        for (int i = 0; i < count; ++i) {
            RDN rdn = this.rdns[i];
            totalSize += rdn.size();
        }
        ASN1ObjectIdentifier[] oids = new ASN1ObjectIdentifier[totalSize];
        int oidsOff = 0;
        for (int i = 0; i < count; ++i) {
            RDN rdn = this.rdns[i];
            oidsOff += rdn.collectAttributeTypes(oids, oidsOff);
        }
        return oids;
    }

    public RDN[] getRDNs(ASN1ObjectIdentifier attributeType) {
        RDN[] res = new RDN[this.rdns.length];
        int count = 0;
        for (int i = 0; i != this.rdns.length; ++i) {
            RDN rdn = this.rdns[i];
            if (!rdn.containsAttributeType(attributeType)) continue;
            res[count++] = rdn;
        }
        if (count < res.length) {
            RDN[] tmp = new RDN[count];
            System.arraycopy(res, 0, tmp, 0, tmp.length);
            res = tmp;
        }
        return res;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.rdnSeq;
    }

    @Override
    public int hashCode() {
        if (this.isHashCodeCalculated) {
            return this.hashCodeValue;
        }
        this.isHashCodeCalculated = true;
        this.hashCodeValue = this.style.calculateHashCode(this);
        return this.hashCodeValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof X500Name) && !(obj instanceof ASN1Sequence)) {
            return false;
        }
        ASN1Primitive derO = ((ASN1Encodable)obj).toASN1Primitive();
        if (this.toASN1Primitive().equals(derO)) {
            return true;
        }
        try {
            return this.style.areEqual(this, new X500Name(ASN1Sequence.getInstance(((ASN1Encodable)obj).toASN1Primitive())));
        }
        catch (Exception e) {
            return false;
        }
    }

    public String toString() {
        return this.style.toString(this);
    }

    public static void setDefaultStyle(X500NameStyle style) {
        if (style == null) {
            throw new NullPointerException("cannot set style to null");
        }
        defaultStyle = style;
    }

    public static X500NameStyle getDefaultStyle() {
        return defaultStyle;
    }
}

