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
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERUniversalString;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1UniversalString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UniversalString.class, 28){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1UniversalString.createPrimitive(octetString.getOctets());
        }
    };
    private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    final byte[] contents;

    public static ASN1UniversalString getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1UniversalString) {
            return (ASN1UniversalString)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1UniversalString) {
            return (ASN1UniversalString)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1UniversalString)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1UniversalString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1UniversalString)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1UniversalString(byte[] contents, boolean clone) {
        this.contents = clone ? Arrays.clone(contents) : contents;
    }

    @Override
    public final String getString() {
        int dl = this.contents.length;
        StringBuffer buf = new StringBuffer(3 + 2 * (ASN1OutputStream.getLengthOfDL(dl) + dl));
        buf.append("#1C");
        ASN1UniversalString.encodeHexDL(buf, dl);
        for (int i = 0; i < dl; ++i) {
            ASN1UniversalString.encodeHexByte(buf, this.contents[i]);
        }
        return buf.toString();
    }

    public String toString() {
        return this.getString();
    }

    public final byte[] getOctets() {
        return Arrays.clone(this.contents);
    }

    @Override
    final boolean encodeConstructed() {
        return false;
    }

    @Override
    final int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.contents.length);
    }

    @Override
    final void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 28, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1UniversalString)) {
            return false;
        }
        ASN1UniversalString that = (ASN1UniversalString)other;
        return Arrays.areEqual(this.contents, that.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1UniversalString createPrimitive(byte[] contents) {
        return new DERUniversalString(contents, false);
    }

    private static void encodeHexByte(StringBuffer buf, int i) {
        buf.append(table[i >>> 4 & 0xF]);
        buf.append(table[i & 0xF]);
    }

    private static void encodeHexDL(StringBuffer buf, int dl) {
        if (dl < 128) {
            ASN1UniversalString.encodeHexByte(buf, dl);
            return;
        }
        byte[] stack = new byte[5];
        int pos = 5;
        do {
            stack[--pos] = (byte)dl;
        } while ((dl >>>= 8) != 0);
        int count = stack.length - pos;
        stack[--pos] = (byte)(0x80 | count);
        do {
            ASN1UniversalString.encodeHexByte(buf, stack[pos++]);
        } while (pos < stack.length);
    }
}

