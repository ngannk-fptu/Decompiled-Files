/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DLOutputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class DEROutputStream
extends DLOutputStream {
    DEROutputStream(OutputStream os) {
        super(os);
    }

    @Override
    DEROutputStream getDERSubStream() {
        return this;
    }

    @Override
    void writeElements(ASN1Encodable[] elements) throws IOException {
        int count = elements.length;
        for (int i = 0; i < count; ++i) {
            elements[i].toASN1Primitive().toDERObject().encode(this, true);
        }
    }

    @Override
    void writePrimitive(ASN1Primitive primitive, boolean withTag) throws IOException {
        primitive.toDERObject().encode(this, withTag);
    }

    @Override
    void writePrimitives(ASN1Primitive[] primitives) throws IOException {
        int count = primitives.length;
        for (int i = 0; i < count; ++i) {
            primitives[i].toDERObject().encode(this, true);
        }
    }
}

