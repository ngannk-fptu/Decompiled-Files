/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1BMPString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1BMPString.class, 30){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1BMPString.createPrimitive(octetString.getOctets());
        }
    };
    final char[] string;

    public static ASN1BMPString getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1BMPString) {
            return (ASN1BMPString)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1BMPString) {
            return (ASN1BMPString)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1BMPString)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1BMPString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1BMPString)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1BMPString(String string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string.toCharArray();
    }

    ASN1BMPString(byte[] string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        int byteLen = string.length;
        if (0 != (byteLen & 1)) {
            throw new IllegalArgumentException("malformed BMPString encoding encountered");
        }
        int charLen = byteLen / 2;
        char[] cs = new char[charLen];
        for (int i = 0; i != charLen; ++i) {
            cs[i] = (char)(string[2 * i] << 8 | string[2 * i + 1] & 0xFF);
        }
        this.string = cs;
    }

    ASN1BMPString(char[] string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string;
    }

    @Override
    public final String getString() {
        return new String(this.string);
    }

    public String toString() {
        return this.getString();
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1BMPString)) {
            return false;
        }
        ASN1BMPString that = (ASN1BMPString)other;
        return Arrays.areEqual(this.string, that.string);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.string);
    }

    @Override
    final boolean encodeConstructed() {
        return false;
    }

    @Override
    final int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.string.length * 2);
    }

    @Override
    final void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        int i;
        int count = this.string.length;
        out.writeIdentifier(withTag, 30);
        out.writeDL(count * 2);
        byte[] buf = new byte[8];
        int limit = count & 0xFFFFFFFC;
        for (i = 0; i < limit; i += 4) {
            char c0 = this.string[i];
            char c1 = this.string[i + 1];
            char c2 = this.string[i + 2];
            char c3 = this.string[i + 3];
            buf[0] = (byte)(c0 >> 8);
            buf[1] = (byte)c0;
            buf[2] = (byte)(c1 >> 8);
            buf[3] = (byte)c1;
            buf[4] = (byte)(c2 >> 8);
            buf[5] = (byte)c2;
            buf[6] = (byte)(c3 >> 8);
            buf[7] = (byte)c3;
            out.write(buf, 0, 8);
        }
        if (i < count) {
            int bufPos = 0;
            do {
                char c0 = this.string[i];
                buf[bufPos++] = (byte)(c0 >> 8);
                buf[bufPos++] = (byte)c0;
            } while (++i < count);
            out.write(buf, 0, bufPos);
        }
    }

    static ASN1BMPString createPrimitive(byte[] contents) {
        return new DERBMPString(contents);
    }

    static ASN1BMPString createPrimitive(char[] string) {
        return new DERBMPString(string);
    }
}

