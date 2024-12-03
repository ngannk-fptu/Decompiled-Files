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

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BERSet
extends ASN1Set {
    public BERSet() {
    }

    public BERSet(ASN1Encodable element) {
        super(element);
    }

    public BERSet(ASN1EncodableVector elementVector) {
        super(elementVector, false);
    }

    public BERSet(ASN1Encodable[] elements) {
        super(elements, false);
    }

    BERSet(boolean isSorted, ASN1Encodable[] elements) {
        super(isSorted, elements);
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        int totalLength = withTag ? 4 : 3;
        int count = this.elements.length;
        for (int i = 0; i < count; ++i) {
            ASN1Primitive p = this.elements[i].toASN1Primitive();
            totalLength += p.encodedLength(true);
        }
        return totalLength;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingIL(withTag, 49, this.elements);
    }
}

