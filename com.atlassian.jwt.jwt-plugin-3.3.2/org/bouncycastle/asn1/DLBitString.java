/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.StreamUtil;

public class DLBitString
extends ASN1BitString {
    public static ASN1BitString getInstance(Object object) {
        if (object == null || object instanceof DLBitString) {
            return (DLBitString)object;
        }
        if (object instanceof DERBitString) {
            return (DERBitString)object;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1BitString)DLBitString.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1BitString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        ASN1Primitive aSN1Primitive = aSN1TaggedObject.getObject();
        if (bl || aSN1Primitive instanceof DLBitString) {
            return DLBitString.getInstance(aSN1Primitive);
        }
        return DLBitString.fromOctetString(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
    }

    protected DLBitString(byte by, int n) {
        super(by, n);
    }

    public DLBitString(byte[] byArray, int n) {
        super(byArray, n);
    }

    public DLBitString(byte[] byArray) {
        this(byArray, 0);
    }

    public DLBitString(int n) {
        super(DLBitString.getBytes(n), DLBitString.getPadBits(n));
    }

    public DLBitString(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable.toASN1Primitive().getEncoded("DER"), 0);
    }

    boolean isConstructed() {
        return false;
    }

    int encodedLength() {
        return 1 + StreamUtil.calculateBodyLength(this.data.length + 1) + this.data.length + 1;
    }

    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncoded(bl, 3, (byte)this.padBits, this.data);
    }

    ASN1Primitive toDLObject() {
        return this;
    }

    static DLBitString fromOctetString(byte[] byArray) {
        if (byArray.length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        byte by = byArray[0];
        byte[] byArray2 = new byte[byArray.length - 1];
        if (byArray2.length != 0) {
            System.arraycopy(byArray, 1, byArray2, 0, byArray.length - 1);
        }
        return new DLBitString(byArray2, (int)by);
    }
}

