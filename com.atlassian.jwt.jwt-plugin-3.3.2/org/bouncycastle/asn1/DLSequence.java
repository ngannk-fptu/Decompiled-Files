/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.StreamUtil;

public class DLSequence
extends ASN1Sequence {
    private int bodyLength = -1;

    public DLSequence() {
    }

    public DLSequence(ASN1Encodable aSN1Encodable) {
        super(aSN1Encodable);
    }

    public DLSequence(ASN1EncodableVector aSN1EncodableVector) {
        super(aSN1EncodableVector);
    }

    public DLSequence(ASN1Encodable[] aSN1EncodableArray) {
        super(aSN1EncodableArray);
    }

    DLSequence(ASN1Encodable[] aSN1EncodableArray, boolean bl) {
        super(aSN1EncodableArray, bl);
    }

    private int getBodyLength() throws IOException {
        if (this.bodyLength < 0) {
            int n = this.elements.length;
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                ASN1Primitive aSN1Primitive = this.elements[i].toASN1Primitive().toDLObject();
                n2 += aSN1Primitive.encodedLength();
            }
            this.bodyLength = n2;
        }
        return this.bodyLength;
    }

    @Override
    int encodedLength() throws IOException {
        int n = this.getBodyLength();
        return 1 + StreamUtil.calculateBodyLength(n) + n;
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        if (bl) {
            aSN1OutputStream.write(48);
        }
        ASN1OutputStream aSN1OutputStream2 = aSN1OutputStream.getDLSubStream();
        int n = this.elements.length;
        if (this.bodyLength >= 0 || n > 16) {
            aSN1OutputStream.writeLength(this.getBodyLength());
            for (int i = 0; i < n; ++i) {
                aSN1OutputStream2.writePrimitive(this.elements[i].toASN1Primitive(), true);
            }
        } else {
            int n2;
            int n3 = 0;
            ASN1Primitive[] aSN1PrimitiveArray = new ASN1Primitive[n];
            for (n2 = 0; n2 < n; ++n2) {
                ASN1Primitive aSN1Primitive;
                aSN1PrimitiveArray[n2] = aSN1Primitive = this.elements[n2].toASN1Primitive().toDLObject();
                n3 += aSN1Primitive.encodedLength();
            }
            this.bodyLength = n3;
            aSN1OutputStream.writeLength(n3);
            for (n2 = 0; n2 < n; ++n2) {
                aSN1OutputStream2.writePrimitive(aSN1PrimitiveArray[n2], true);
            }
        }
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

