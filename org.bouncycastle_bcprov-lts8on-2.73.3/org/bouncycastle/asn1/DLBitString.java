/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLBitString
extends ASN1BitString {
    public DLBitString(byte[] data) {
        this(data, 0);
    }

    public DLBitString(byte data, int padBits) {
        super(data, padBits);
    }

    public DLBitString(byte[] data, int padBits) {
        super(data, padBits);
    }

    public DLBitString(int value) {
        super(DLBitString.getBytes(value), DLBitString.getPadBits(value));
    }

    public DLBitString(ASN1Encodable obj) throws IOException {
        super(obj.toASN1Primitive().getEncoded("DER"), 0);
    }

    DLBitString(byte[] contents, boolean check) {
        super(contents, check);
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.contents.length);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 3, this.contents);
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }

    static int encodedLength(boolean withTag, int contentsLength) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, contentsLength);
    }

    static void encode(ASN1OutputStream out, boolean withTag, byte[] buf, int off, int len) throws IOException {
        out.writeEncodingDL(withTag, 3, buf, off, len);
    }

    static void encode(ASN1OutputStream out, boolean withTag, byte pad, byte[] buf, int off, int len) throws IOException {
        out.writeEncodingDL(withTag, 3, pad, buf, off, len);
    }
}

