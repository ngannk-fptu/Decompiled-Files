/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1Sequence
extends ASN1Primitive
implements Iterable<ASN1Encodable> {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Sequence.class, 16){

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
            return sequence;
        }
    };
    ASN1Encodable[] elements;

    public static ASN1Sequence getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1Sequence) {
            return (ASN1Sequence)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1Sequence) {
                return (ASN1Sequence)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1Sequence)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct sequence from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("unknown object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Sequence getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1Sequence)TYPE.getContextInstance(taggedObject, explicit);
    }

    protected ASN1Sequence() {
        this.elements = ASN1EncodableVector.EMPTY_ELEMENTS;
    }

    protected ASN1Sequence(ASN1Encodable element) {
        if (null == element) {
            throw new NullPointerException("'element' cannot be null");
        }
        this.elements = new ASN1Encodable[]{element};
    }

    protected ASN1Sequence(ASN1EncodableVector elementVector) {
        if (null == elementVector) {
            throw new NullPointerException("'elementVector' cannot be null");
        }
        this.elements = elementVector.takeElements();
    }

    protected ASN1Sequence(ASN1Encodable[] elements) {
        if (Arrays.isNullOrContainsNull(elements)) {
            throw new NullPointerException("'elements' cannot be null, or contain null");
        }
        this.elements = ASN1EncodableVector.cloneElements(elements);
    }

    ASN1Sequence(ASN1Encodable[] elements, boolean clone) {
        this.elements = clone ? ASN1EncodableVector.cloneElements(elements) : elements;
    }

    public ASN1Encodable[] toArray() {
        return ASN1EncodableVector.cloneElements(this.elements);
    }

    ASN1Encodable[] toArrayInternal() {
        return this.elements;
    }

    public Enumeration getObjects() {
        return new Enumeration(){
            private int pos = 0;

            @Override
            public boolean hasMoreElements() {
                return this.pos < ASN1Sequence.this.elements.length;
            }

            public Object nextElement() {
                if (this.pos < ASN1Sequence.this.elements.length) {
                    return ASN1Sequence.this.elements[this.pos++];
                }
                throw new NoSuchElementException();
            }
        };
    }

    public ASN1SequenceParser parser() {
        final int count = this.size();
        return new ASN1SequenceParser(){
            private int pos = 0;

            @Override
            public ASN1Encodable readObject() throws IOException {
                ASN1Encodable obj;
                if (count == this.pos) {
                    return null;
                }
                if ((obj = ASN1Sequence.this.elements[this.pos++]) instanceof ASN1Sequence) {
                    return ((ASN1Sequence)obj).parser();
                }
                if (obj instanceof ASN1Set) {
                    return ((ASN1Set)obj).parser();
                }
                return obj;
            }

            @Override
            public ASN1Primitive getLoadedObject() {
                return ASN1Sequence.this;
            }

            @Override
            public ASN1Primitive toASN1Primitive() {
                return ASN1Sequence.this;
            }
        };
    }

    public ASN1Encodable getObjectAt(int index) {
        return this.elements[index];
    }

    public int size() {
        return this.elements.length;
    }

    @Override
    public int hashCode() {
        int i = this.elements.length;
        int hc = i + 1;
        while (--i >= 0) {
            hc *= 257;
            hc ^= this.elements[i].toASN1Primitive().hashCode();
        }
        return hc;
    }

    @Override
    boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1Sequence)) {
            return false;
        }
        ASN1Sequence that = (ASN1Sequence)other;
        int count = this.size();
        if (that.size() != count) {
            return false;
        }
        for (int i = 0; i < count; ++i) {
            ASN1Primitive p2;
            ASN1Primitive p1 = this.elements[i].toASN1Primitive();
            if (p1 == (p2 = that.elements[i].toASN1Primitive()) || p1.asn1Equals(p2)) continue;
            return false;
        }
        return true;
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERSequence(this.elements, false);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLSequence(this.elements, false);
    }

    abstract ASN1BitString toASN1BitString();

    abstract ASN1External toASN1External();

    abstract ASN1OctetString toASN1OctetString();

    abstract ASN1Set toASN1Set();

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
        return new Arrays.Iterator<ASN1Encodable>(this.elements);
    }

    ASN1BitString[] getConstructedBitStrings() {
        int count = this.size();
        ASN1BitString[] bitStrings = new ASN1BitString[count];
        for (int i = 0; i < count; ++i) {
            bitStrings[i] = ASN1BitString.getInstance(this.elements[i]);
        }
        return bitStrings;
    }

    ASN1OctetString[] getConstructedOctetStrings() {
        int count = this.size();
        ASN1OctetString[] octetStrings = new ASN1OctetString[count];
        for (int i = 0; i < count; ++i) {
            octetStrings[i] = ASN1OctetString.getInstance(this.elements[i]);
        }
        return octetStrings;
    }
}

