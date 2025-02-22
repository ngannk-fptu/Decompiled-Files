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
import org.bouncycastle.asn1.DEROutputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERSet
extends ASN1Set {
    private int contentsLength = -1;

    public static DERSet convert(ASN1Set set) {
        return (DERSet)set.toDERObject();
    }

    public DERSet() {
    }

    public DERSet(ASN1Encodable element) {
        super(element);
    }

    public DERSet(ASN1EncodableVector elementVector) {
        super(elementVector, true);
    }

    public DERSet(ASN1Encodable[] elements) {
        super(elements, true);
    }

    DERSet(boolean isSorted, ASN1Encodable[] elements) {
        super(DERSet.checkSorted(isSorted), elements);
    }

    private int getContentsLength() throws IOException {
        if (this.contentsLength < 0) {
            int count = this.elements.length;
            int totalLength = 0;
            for (int i = 0; i < count; ++i) {
                ASN1Primitive derObject = this.elements[i].toASN1Primitive().toDERObject();
                totalLength += derObject.encodedLength(true);
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
        DEROutputStream derOut = out.getDERSubStream();
        int count = this.elements.length;
        if (this.contentsLength >= 0 || count > 16) {
            out.writeDL(this.getContentsLength());
            for (int i = 0; i < count; ++i) {
                ASN1Primitive derObject = this.elements[i].toASN1Primitive().toDERObject();
                derObject.encode(derOut, true);
            }
        } else {
            int i;
            int totalLength = 0;
            ASN1Primitive[] derObjects = new ASN1Primitive[count];
            for (i = 0; i < count; ++i) {
                ASN1Primitive derObject;
                derObjects[i] = derObject = this.elements[i].toASN1Primitive().toDERObject();
                totalLength += derObject.encodedLength(true);
            }
            this.contentsLength = totalLength;
            out.writeDL(totalLength);
            for (i = 0; i < count; ++i) {
                derObjects[i].encode(derOut, true);
            }
        }
    }

    @Override
    ASN1Primitive toDERObject() {
        return this.sortedElements != null ? this : super.toDERObject();
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }

    private static boolean checkSorted(boolean isSorted) {
        if (!isSorted) {
            throw new IllegalStateException("DERSet elements should always be in sorted order");
        }
        return isSorted;
    }
}

