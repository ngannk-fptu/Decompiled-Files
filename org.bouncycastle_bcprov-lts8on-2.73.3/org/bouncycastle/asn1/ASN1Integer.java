/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1Integer
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Integer.class, 2){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1Integer.createPrimitive(octetString.getOctets());
        }
    };
    static final int SIGN_EXT_SIGNED = -1;
    static final int SIGN_EXT_UNSIGNED = 255;
    private final byte[] bytes;
    private final int start;

    public static ASN1Integer getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1Integer) {
            return (ASN1Integer)obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1Integer)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Integer getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1Integer)TYPE.getContextInstance(taggedObject, explicit);
    }

    public ASN1Integer(long value) {
        this.bytes = BigInteger.valueOf(value).toByteArray();
        this.start = 0;
    }

    public ASN1Integer(BigInteger value) {
        this.bytes = value.toByteArray();
        this.start = 0;
    }

    public ASN1Integer(byte[] bytes) {
        this(bytes, true);
    }

    ASN1Integer(byte[] bytes, boolean clone) {
        if (ASN1Integer.isMalformed(bytes)) {
            throw new IllegalArgumentException("malformed integer");
        }
        this.bytes = clone ? Arrays.clone(bytes) : bytes;
        this.start = ASN1Integer.signBytesToSkip(bytes);
    }

    public BigInteger getPositiveValue() {
        return new BigInteger(1, this.bytes);
    }

    public BigInteger getValue() {
        return new BigInteger(this.bytes);
    }

    public boolean hasValue(int x) {
        return this.bytes.length - this.start <= 4 && ASN1Integer.intValue(this.bytes, this.start, -1) == x;
    }

    public boolean hasValue(long x) {
        return this.bytes.length - this.start <= 8 && ASN1Integer.longValue(this.bytes, this.start, -1) == x;
    }

    public boolean hasValue(BigInteger x) {
        return null != x && ASN1Integer.intValue(this.bytes, this.start, -1) == x.intValue() && this.getValue().equals(x);
    }

    public int intPositiveValueExact() {
        int count = this.bytes.length - this.start;
        if (count > 4 || count == 4 && 0 != (this.bytes[this.start] & 0x80)) {
            throw new ArithmeticException("ASN.1 Integer out of positive int range");
        }
        return ASN1Integer.intValue(this.bytes, this.start, 255);
    }

    public int intValueExact() {
        int count = this.bytes.length - this.start;
        if (count > 4) {
            throw new ArithmeticException("ASN.1 Integer out of int range");
        }
        return ASN1Integer.intValue(this.bytes, this.start, -1);
    }

    public long longValueExact() {
        int count = this.bytes.length - this.start;
        if (count > 8) {
            throw new ArithmeticException("ASN.1 Integer out of long range");
        }
        return ASN1Integer.longValue(this.bytes, this.start, -1);
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.bytes.length);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 2, this.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.bytes);
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1Integer)) {
            return false;
        }
        ASN1Integer other = (ASN1Integer)o;
        return Arrays.areEqual(this.bytes, other.bytes);
    }

    public String toString() {
        return this.getValue().toString();
    }

    static ASN1Integer createPrimitive(byte[] contents) {
        return new ASN1Integer(contents, false);
    }

    static int intValue(byte[] bytes, int start, int signExt) {
        int length = bytes.length;
        int pos = Math.max(start, length - 4);
        int val = bytes[pos] & signExt;
        while (++pos < length) {
            val = val << 8 | bytes[pos] & 0xFF;
        }
        return val;
    }

    static long longValue(byte[] bytes, int start, int signExt) {
        int length = bytes.length;
        int pos = Math.max(start, length - 8);
        long val = bytes[pos] & signExt;
        while (++pos < length) {
            val = val << 8 | (long)(bytes[pos] & 0xFF);
        }
        return val;
    }

    static boolean isMalformed(byte[] bytes) {
        switch (bytes.length) {
            case 0: {
                return true;
            }
            case 1: {
                return false;
            }
        }
        return bytes[0] == bytes[1] >> 7 && !Properties.isOverrideSet("org.bouncycastle.asn1.allow_unsafe_integer");
    }

    static int signBytesToSkip(byte[] bytes) {
        int pos;
        int last = bytes.length - 1;
        for (pos = 0; pos < last && bytes[pos] == bytes[pos + 1] >> 7; ++pos) {
        }
        return pos;
    }
}

