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

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DERBitString
extends ASN1BitString {
    public static DERBitString convert(ASN1BitString bitString) {
        return (DERBitString)bitString.toDERObject();
    }

    public DERBitString(byte[] data) {
        this(data, 0);
    }

    public DERBitString(byte data, int padBits) {
        super(data, padBits);
    }

    public DERBitString(byte[] data, int padBits) {
        super(data, padBits);
    }

    public DERBitString(int value) {
        super(DERBitString.getBytes(value), DERBitString.getPadBits(value));
    }

    public DERBitString(ASN1Encodable obj) throws IOException {
        super(obj.toASN1Primitive().getEncoded("DER"), 0);
    }

    DERBitString(byte[] contents, boolean check) {
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
        int length = this.contents.length;
        int last = length - 1;
        byte lastOctet = this.contents[last];
        int padBits = this.contents[0] & 0xFF;
        byte lastOctetDER = (byte)(this.contents[last] & 255 << padBits);
        if (lastOctet == lastOctetDER) {
            out.writeEncodingDL(withTag, 3, this.contents);
        } else {
            out.writeEncodingDL(withTag, 3, this.contents, 0, last, lastOctetDER);
        }
    }

    @Override
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }

    static DERBitString fromOctetString(ASN1OctetString octetString) {
        return new DERBitString(octetString.getOctets(), true);
    }
}

