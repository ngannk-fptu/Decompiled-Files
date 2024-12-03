/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Private;

public class DERPrivate
extends ASN1Private {
    DERPrivate(boolean bl, int n, byte[] byArray) {
        super(bl, n, byArray);
    }

    public DERPrivate(int n, byte[] byArray) {
        this(false, n, byArray);
    }

    public DERPrivate(int n, ASN1Encodable aSN1Encodable) throws IOException {
        this(true, n, aSN1Encodable);
    }

    public DERPrivate(boolean bl, int n, ASN1Encodable aSN1Encodable) throws IOException {
        super(bl || aSN1Encodable.toASN1Primitive().isConstructed(), n, DERPrivate.getEncoding(bl, aSN1Encodable));
    }

    private static byte[] getEncoding(boolean bl, ASN1Encodable aSN1Encodable) throws IOException {
        byte[] byArray = aSN1Encodable.toASN1Primitive().getEncoded("DER");
        if (bl) {
            return byArray;
        }
        int n = DERPrivate.getLengthOfHeader(byArray);
        byte[] byArray2 = new byte[byArray.length - n];
        System.arraycopy(byArray, n, byArray2, 0, byArray2.length);
        return byArray2;
    }

    public DERPrivate(int n, ASN1EncodableVector aSN1EncodableVector) {
        super(true, n, DERPrivate.getEncodedVector(aSN1EncodableVector));
    }

    private static byte[] getEncodedVector(ASN1EncodableVector aSN1EncodableVector) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != aSN1EncodableVector.size(); ++i) {
            try {
                byteArrayOutputStream.write(((ASN1Object)aSN1EncodableVector.get(i)).getEncoded("DER"));
                continue;
            }
            catch (IOException iOException) {
                throw new ASN1ParsingException("malformed object: " + iOException, iOException);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        int n = 192;
        if (this.isConstructed) {
            n |= 0x20;
        }
        aSN1OutputStream.writeEncoded(bl, n, this.tag, this.octets);
    }
}

