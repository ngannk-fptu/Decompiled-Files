/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.LazyConstructionEnumeration;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class LazyEncodedSequence
extends ASN1Sequence {
    private byte[] encoded;

    LazyEncodedSequence(byte[] encoded) throws IOException {
        if (null == encoded) {
            throw new NullPointerException("'encoded' cannot be null");
        }
        this.encoded = encoded;
    }

    @Override
    public ASN1Encodable getObjectAt(int index) {
        this.force();
        return super.getObjectAt(index);
    }

    @Override
    public Enumeration getObjects() {
        byte[] encoded = this.getContents();
        if (null != encoded) {
            return new LazyConstructionEnumeration(encoded);
        }
        return super.getObjects();
    }

    @Override
    public int hashCode() {
        this.force();
        return super.hashCode();
    }

    @Override
    public Iterator<ASN1Encodable> iterator() {
        this.force();
        return super.iterator();
    }

    @Override
    public int size() {
        this.force();
        return super.size();
    }

    @Override
    public ASN1Encodable[] toArray() {
        this.force();
        return super.toArray();
    }

    @Override
    ASN1Encodable[] toArrayInternal() {
        this.force();
        return super.toArrayInternal();
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        byte[] encoded = this.getContents();
        if (null != encoded) {
            return ASN1OutputStream.getLengthOfEncodingDL(withTag, encoded.length);
        }
        return super.toDLObject().encodedLength(withTag);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        byte[] encoded = this.getContents();
        if (null != encoded) {
            out.writeEncodingDL(withTag, 48, encoded);
            return;
        }
        super.toDLObject().encode(out, withTag);
    }

    @Override
    ASN1BitString toASN1BitString() {
        return ((ASN1Sequence)this.toDLObject()).toASN1BitString();
    }

    @Override
    ASN1External toASN1External() {
        return ((ASN1Sequence)this.toDLObject()).toASN1External();
    }

    @Override
    ASN1OctetString toASN1OctetString() {
        return ((ASN1Sequence)this.toDLObject()).toASN1OctetString();
    }

    @Override
    ASN1Set toASN1Set() {
        return ((ASN1Sequence)this.toDLObject()).toASN1Set();
    }

    @Override
    ASN1Primitive toDERObject() {
        this.force();
        return super.toDERObject();
    }

    @Override
    ASN1Primitive toDLObject() {
        this.force();
        return super.toDLObject();
    }

    private synchronized void force() {
        if (null != this.encoded) {
            ASN1InputStream aIn = new ASN1InputStream(this.encoded, true);
            try {
                ASN1EncodableVector v = aIn.readVector();
                aIn.close();
                this.elements = v.takeElements();
                this.encoded = null;
            }
            catch (IOException e) {
                throw new ASN1ParsingException("malformed ASN.1: " + e, e);
            }
        }
    }

    private synchronized byte[] getContents() {
        return this.encoded;
    }
}

