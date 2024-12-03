/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1Set
extends ASN1Primitive
implements Iterable<ASN1Encodable> {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Set.class, 17){

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
            return sequence.toASN1Set();
        }
    };
    protected final ASN1Encodable[] elements;
    protected ASN1Encodable[] sortedElements;

    public static ASN1Set getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1Set) {
            return (ASN1Set)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1Set) {
                return (ASN1Set)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1Set)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct set from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Set getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1Set)TYPE.getContextInstance(taggedObject, explicit);
    }

    protected ASN1Set() {
        this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
        this.sortedElements = this.elements;
    }

    protected ASN1Set(ASN1Encodable element) {
        if (null == element) {
            throw new NullPointerException("'element' cannot be null");
        }
        this.elements = new ASN1Encodable[]{element};
        this.sortedElements = this.elements;
    }

    protected ASN1Set(ASN1EncodableVector elementVector, boolean doSort) {
        ASN1Encodable[] tmp;
        if (null == elementVector) {
            throw new NullPointerException("'elementVector' cannot be null");
        }
        if (doSort && elementVector.size() >= 2) {
            tmp = elementVector.copyElements();
            ASN1Set.sort(tmp);
        } else {
            tmp = elementVector.takeElements();
        }
        this.elements = tmp;
        this.sortedElements = doSort || tmp.length < 2 ? this.elements : null;
    }

    protected ASN1Set(ASN1Encodable[] elements, boolean doSort) {
        if (Arrays.isNullOrContainsNull(elements)) {
            throw new NullPointerException("'elements' cannot be null, or contain null");
        }
        ASN1Encodable[] tmp = ASN1EncodableVector.cloneElements(elements);
        if (doSort && tmp.length >= 2) {
            ASN1Set.sort(tmp);
        }
        this.elements = tmp;
        this.sortedElements = doSort || tmp.length < 2 ? elements : null;
    }

    ASN1Set(boolean isSorted, ASN1Encodable[] elements) {
        this.elements = elements;
        this.sortedElements = isSorted || elements.length < 2 ? elements : null;
    }

    ASN1Set(ASN1Encodable[] elements, ASN1Encodable[] sortedElements) {
        this.elements = elements;
        this.sortedElements = sortedElements;
    }

    public Enumeration getObjects() {
        return new Enumeration(){
            private int pos = 0;

            @Override
            public boolean hasMoreElements() {
                return this.pos < ASN1Set.this.elements.length;
            }

            public Object nextElement() {
                if (this.pos < ASN1Set.this.elements.length) {
                    return ASN1Set.this.elements[this.pos++];
                }
                throw new NoSuchElementException();
            }
        };
    }

    public ASN1Encodable getObjectAt(int index) {
        return this.elements[index];
    }

    public int size() {
        return this.elements.length;
    }

    public ASN1Encodable[] toArray() {
        return ASN1EncodableVector.cloneElements(this.elements);
    }

    public ASN1SetParser parser() {
        final int count = this.size();
        return new ASN1SetParser(){
            private int pos = 0;

            @Override
            public ASN1Encodable readObject() throws IOException {
                ASN1Encodable obj;
                if (count == this.pos) {
                    return null;
                }
                if ((obj = ASN1Set.this.elements[this.pos++]) instanceof ASN1Sequence) {
                    return ((ASN1Sequence)obj).parser();
                }
                if (obj instanceof ASN1Set) {
                    return ((ASN1Set)obj).parser();
                }
                return obj;
            }

            @Override
            public ASN1Primitive getLoadedObject() {
                return ASN1Set.this;
            }

            @Override
            public ASN1Primitive toASN1Primitive() {
                return ASN1Set.this;
            }
        };
    }

    @Override
    public int hashCode() {
        int i = this.elements.length;
        int hc = i + 1;
        while (--i >= 0) {
            hc += this.elements[i].toASN1Primitive().hashCode();
        }
        return hc;
    }

    @Override
    ASN1Primitive toDERObject() {
        if (this.sortedElements == null) {
            this.sortedElements = (ASN1Encodable[])this.elements.clone();
            ASN1Set.sort(this.sortedElements);
        }
        return new DERSet(true, this.sortedElements);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLSet(this.elements, this.sortedElements);
    }

    @Override
    boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1Set)) {
            return false;
        }
        ASN1Set that = (ASN1Set)other;
        int count = this.size();
        if (that.size() != count) {
            return false;
        }
        DERSet dis = (DERSet)this.toDERObject();
        DERSet dat = (DERSet)that.toDERObject();
        for (int i = 0; i < count; ++i) {
            ASN1Primitive p2;
            ASN1Primitive p1 = dis.elements[i].toASN1Primitive();
            if (p1 == (p2 = dat.elements[i].toASN1Primitive()) || p1.asn1Equals(p2)) continue;
            return false;
        }
        return true;
    }

    @Override
    boolean encodeConstructed() {
        return true;
    }

    public String toString() {
        int count = this.size();
        if (0 == count) {
            return "[]";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        int i = 0;
        while (true) {
            sb.append(this.elements[i]);
            if (++i >= count) break;
            sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator<ASN1Encodable>(this.toArray());
    }

    private static byte[] getDEREncoded(ASN1Encodable obj) {
        try {
            return obj.toASN1Primitive().getEncoded("DER");
        }
        catch (IOException e) {
            throw new IllegalArgumentException("cannot encode object added to SET");
        }
    }

    private static boolean lessThanOrEqual(byte[] a, byte[] b) {
        int a0 = a[0] & 0xDF;
        int b0 = b[0] & 0xDF;
        if (a0 != b0) {
            return a0 < b0;
        }
        int last = Math.min(a.length, b.length) - 1;
        for (int i = 1; i < last; ++i) {
            if (a[i] == b[i]) continue;
            return (a[i] & 0xFF) < (b[i] & 0xFF);
        }
        return (a[last] & 0xFF) <= (b[last] & 0xFF);
    }

    private static void sort(ASN1Encodable[] t) {
        int count = t.length;
        if (count < 2) {
            return;
        }
        ASN1Encodable eh = t[0];
        ASN1Encodable ei = t[1];
        byte[] bh = ASN1Set.getDEREncoded(eh);
        byte[] bi = ASN1Set.getDEREncoded(ei);
        if (ASN1Set.lessThanOrEqual(bi, bh)) {
            ASN1Encodable et = ei;
            ei = eh;
            eh = et;
            byte[] bt = bi;
            bi = bh;
            bh = bt;
        }
        for (int i = 2; i < count; ++i) {
            ASN1Encodable e1;
            byte[] b1;
            ASN1Encodable e2 = t[i];
            byte[] b2 = ASN1Set.getDEREncoded(e2);
            if (ASN1Set.lessThanOrEqual(bi, b2)) {
                t[i - 2] = eh;
                eh = ei;
                bh = bi;
                ei = e2;
                bi = b2;
                continue;
            }
            if (ASN1Set.lessThanOrEqual(bh, b2)) {
                t[i - 2] = eh;
                eh = e2;
                bh = b2;
                continue;
            }
            int j = i - 1;
            while (--j > 0 && !ASN1Set.lessThanOrEqual(b1 = ASN1Set.getDEREncoded(e1 = t[j - 1]), b2)) {
                t[j] = e1;
            }
            t[j] = e2;
        }
        t[count - 2] = eh;
        t[count - 1] = ei;
    }
}

