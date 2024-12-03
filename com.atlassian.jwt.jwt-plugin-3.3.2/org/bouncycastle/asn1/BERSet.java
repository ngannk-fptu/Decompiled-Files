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

public class BERSet
extends ASN1Set {
    public BERSet() {
    }

    public BERSet(ASN1Encodable aSN1Encodable) {
        super(aSN1Encodable);
    }

    public BERSet(ASN1EncodableVector aSN1EncodableVector) {
        super(aSN1EncodableVector, false);
    }

    public BERSet(ASN1Encodable[] aSN1EncodableArray) {
        super(aSN1EncodableArray, false);
    }

    BERSet(boolean bl, ASN1Encodable[] aSN1EncodableArray) {
        super(bl, aSN1EncodableArray);
    }

    int encodedLength() throws IOException {
        int n = this.elements.length;
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            ASN1Primitive aSN1Primitive = this.elements[i].toASN1Primitive();
            n2 += aSN1Primitive.encodedLength();
        }
        return 2 + n2 + 2;
    }

    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodedIndef(bl, 49, this.elements);
    }
}

