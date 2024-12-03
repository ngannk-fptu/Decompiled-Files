/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitStringParser;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DLBitString;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1BitString
extends ASN1Primitive
implements ASN1String,
ASN1BitStringParser {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1BitString.class, 3){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1BitString.createPrimitive(octetString.getOctets());
        }

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
            return sequence.toASN1BitString();
        }
    };
    private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    final byte[] contents;

    public static ASN1BitString getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1BitString) {
            return (ASN1BitString)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1BitString) {
                return (ASN1BitString)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1BitString)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct BIT STRING from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1BitString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1BitString)TYPE.getContextInstance(taggedObject, explicit);
    }

    protected static int getPadBits(int bitString) {
        int val = 0;
        for (int i = 3; i >= 0; --i) {
            if (i != 0) {
                if (bitString >> i * 8 == 0) continue;
                val = bitString >> i * 8 & 0xFF;
                break;
            }
            if (bitString == 0) continue;
            val = bitString & 0xFF;
            break;
        }
        if (val == 0) {
            return 0;
        }
        int bits = 1;
        while (((val <<= 1) & 0xFF) != 0) {
            ++bits;
        }
        return 8 - bits;
    }

    protected static byte[] getBytes(int bitString) {
        if (bitString == 0) {
            return new byte[0];
        }
        int bytes = 4;
        for (int i = 3; i >= 1 && (bitString & 255 << i * 8) == 0; --i) {
            --bytes;
        }
        byte[] result = new byte[bytes];
        for (int i = 0; i < bytes; ++i) {
            result[i] = (byte)(bitString >> i * 8 & 0xFF);
        }
        return result;
    }

    ASN1BitString(byte data, int padBits) {
        if (padBits > 7 || padBits < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.contents = new byte[]{(byte)padBits, data};
    }

    ASN1BitString(byte[] data, int padBits) {
        if (data == null) {
            throw new NullPointerException("'data' cannot be null");
        }
        if (data.length == 0 && padBits != 0) {
            throw new IllegalArgumentException("zero length data with non-zero pad bits");
        }
        if (padBits > 7 || padBits < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.contents = Arrays.prepend(data, (byte)padBits);
    }

    ASN1BitString(byte[] contents, boolean check) {
        if (check) {
            if (null == contents) {
                throw new NullPointerException("'contents' cannot be null");
            }
            if (contents.length < 1) {
                throw new IllegalArgumentException("'contents' cannot be empty");
            }
            int padBits = contents[0] & 0xFF;
            if (padBits > 0) {
                if (contents.length < 2) {
                    throw new IllegalArgumentException("zero length data with non-zero pad bits");
                }
                if (padBits > 7) {
                    throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
                }
            }
        }
        this.contents = contents;
    }

    @Override
    public InputStream getBitStream() throws IOException {
        return new ByteArrayInputStream(this.contents, 1, this.contents.length - 1);
    }

    @Override
    public InputStream getOctetStream() throws IOException {
        int padBits = this.contents[0] & 0xFF;
        if (0 != padBits) {
            throw new IOException("expected octet-aligned bitstring, but found padBits: " + padBits);
        }
        return this.getBitStream();
    }

    public ASN1BitStringParser parser() {
        return this;
    }

    @Override
    public String getString() {
        byte[] string;
        try {
            string = this.getEncoded();
        }
        catch (IOException e) {
            throw new ASN1ParsingException("Internal error encoding BitString: " + e.getMessage(), e);
        }
        StringBuffer buf = new StringBuffer(1 + string.length * 2);
        buf.append('#');
        for (int i = 0; i != string.length; ++i) {
            byte b = string[i];
            buf.append(table[b >>> 4 & 0xF]);
            buf.append(table[b & 0xF]);
        }
        return buf.toString();
    }

    public int intValue() {
        int value = 0;
        int end = Math.min(5, this.contents.length - 1);
        for (int i = 1; i < end; ++i) {
            value |= (this.contents[i] & 0xFF) << 8 * (i - 1);
        }
        if (1 <= end && end < 5) {
            int padBits = this.contents[0] & 0xFF;
            byte der = (byte)(this.contents[end] & 255 << padBits);
            value |= (der & 0xFF) << 8 * (end - 1);
        }
        return value;
    }

    public byte[] getOctets() {
        if (this.contents[0] != 0) {
            throw new IllegalStateException("attempt to get non-octet aligned data from BIT STRING");
        }
        return Arrays.copyOfRange(this.contents, 1, this.contents.length);
    }

    public byte[] getBytes() {
        if (this.contents.length == 1) {
            return ASN1OctetString.EMPTY_OCTETS;
        }
        int padBits = this.contents[0] & 0xFF;
        byte[] rv = Arrays.copyOfRange(this.contents, 1, this.contents.length);
        int n = rv.length - 1;
        rv[n] = (byte)(rv[n] & (byte)(255 << padBits));
        return rv;
    }

    @Override
    public int getPadBits() {
        return this.contents[0] & 0xFF;
    }

    public String toString() {
        return this.getString();
    }

    @Override
    public int hashCode() {
        if (this.contents.length < 2) {
            return 1;
        }
        int padBits = this.contents[0] & 0xFF;
        int last = this.contents.length - 1;
        byte lastOctetDER = (byte)(this.contents[last] & 255 << padBits);
        int hc = Arrays.hashCode(this.contents, 0, last);
        hc *= 257;
        return hc ^= lastOctetDER;
    }

    @Override
    boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1BitString)) {
            return false;
        }
        ASN1BitString that = (ASN1BitString)other;
        byte[] thatContents = that.contents;
        byte[] thisContents = this.contents;
        int length = thisContents.length;
        if (thatContents.length != length) {
            return false;
        }
        if (length == 1) {
            return true;
        }
        int last = length - 1;
        for (int i = 0; i < last; ++i) {
            if (thisContents[i] == thatContents[i]) continue;
            return false;
        }
        int padBits = thisContents[0] & 0xFF;
        byte thisLastOctetDER = (byte)(thisContents[last] & 255 << padBits);
        byte thatLastOctetDER = (byte)(thatContents[last] & 255 << padBits);
        return thisLastOctetDER == thatLastOctetDER;
    }

    @Override
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERBitString(this.contents, false);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLBitString(this.contents, false);
    }

    static ASN1BitString createPrimitive(byte[] contents) {
        int length = contents.length;
        if (length < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int padBits = contents[0] & 0xFF;
        if (padBits > 0) {
            if (padBits > 7 || length < 2) {
                throw new IllegalArgumentException("invalid pad bits detected");
            }
            byte finalOctet = contents[length - 1];
            if (finalOctet != (byte)(finalOctet & 255 << padBits)) {
                return new DLBitString(contents, false);
            }
        }
        return new DERBitString(contents, false);
    }
}

