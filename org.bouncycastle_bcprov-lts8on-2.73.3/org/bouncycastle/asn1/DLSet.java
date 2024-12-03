/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DLOutputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLSet
extends ASN1Set {
    private int contentsLength = -1;

    public DLSet() {
    }

    public DLSet(ASN1Encodable element) {
        super(element);
    }

    public DLSet(ASN1EncodableVector elementVector) {
        super(elementVector, false);
    }

    public DLSet(ASN1Encodable[] elements) {
        super(elements, false);
    }

    DLSet(boolean isSorted, ASN1Encodable[] elements) {
        super(isSorted, elements);
    }

    DLSet(ASN1Encodable[] elements, ASN1Encodable[] sortedElements) {
        super(elements, sortedElements);
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
        out.writeIdentifier(withTag, 49);
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
    ASN1Primitive toDLObject() {
        return this;
    }
}

