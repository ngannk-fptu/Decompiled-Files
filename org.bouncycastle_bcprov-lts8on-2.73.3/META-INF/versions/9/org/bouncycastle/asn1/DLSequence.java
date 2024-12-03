/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERBitString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DLBitString;
import org.bouncycastle.asn1.DLExternal;
import org.bouncycastle.asn1.DLOutputStream;
import org.bouncycastle.asn1.DLSet;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLSequence
extends ASN1Sequence {
    private int contentsLength = -1;

    public DLSequence() {
    }

    public DLSequence(ASN1Encodable element) {
        super(element);
    }

    public DLSequence(ASN1EncodableVector elementVector) {
        super(elementVector);
    }

    public DLSequence(ASN1Encodable[] elements) {
        super(elements);
    }

    DLSequence(ASN1Encodable[] elements, boolean clone) {
        super(elements, clone);
    }

    private int getContentsLength() throws IOException {
        if (this.contentsLength < 0) {
            int count = this.elements.length;
            int totalLength = 0;
            for (int i = 0; i < count; ++i) {
                ASN1Primitive dlObject = this.elements[i].toASN1Primitive().toDLObject();
                totalLength += dlObject.encodedLength(true);
            }
            this.contentsLength = totalLength;
        }
        return this.contentsLength;
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.getContentsLength());
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeIdentifier(withTag, 48);
        DLOutputStream dlOut = out.getDLSubStream();
        int count = this.elements.length;
        if (this.contentsLength >= 0 || count > 16) {
            out.writeDL(this.getContentsLength());
            for (int i = 0; i < count; ++i) {
                ((ASN1OutputStream)dlOut).writePrimitive(this.elements[i].toASN1Primitive(), true);
            }
        } else {
            int i;
            int totalLength = 0;
            ASN1Primitive[] dlObjects = new ASN1Primitive[count];
            for (i = 0; i < count; ++i) {
                ASN1Primitive dlObject;
                dlObjects[i] = dlObject = this.elements[i].toASN1Primitive().toDLObject();
                totalLength += dlObject.encodedLength(true);
            }
            this.contentsLength = totalLength;
            out.writeDL(totalLength);
            for (i = 0; i < count; ++i) {
                ((ASN1OutputStream)dlOut).writePrimitive(dlObjects[i], true);
            }
        }
    }

    @Override
    ASN1BitString toASN1BitString() {
        return new DLBitString(BERBitString.flattenBitStrings(this.getConstructedBitStrings()), false);
    }

    @Override
    ASN1External toASN1External() {
        return new DLExternal(this);
    }

    @Override
    ASN1OctetString toASN1OctetString() {
        return new DEROctetString(BEROctetString.flattenOctetStrings(this.getConstructedOctetStrings()));
    }

    @Override
    ASN1Set toASN1Set() {
        return new DLSet(false, this.toArrayInternal());
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

