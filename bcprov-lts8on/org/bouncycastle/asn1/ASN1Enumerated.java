/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1Enumerated
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Enumerated.class, 10){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1Enumerated.createPrimitive(octetString.getOctets(), false);
        }
    };
    private final byte[] contents;
    private final int start;
    private static final ASN1Enumerated[] cache = new ASN1Enumerated[12];

    public static ASN1Enumerated getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1Enumerated) {
            return (ASN1Enumerated)obj;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1Enumerated)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Enumerated getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1Enumerated)TYPE.getContextInstance(taggedObject, explicit);
    }

    public ASN1Enumerated(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.contents = BigInteger.valueOf(value).toByteArray();
        this.start = 0;
    }

    public ASN1Enumerated(BigInteger value) {
        if (value.signum() < 0) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.contents = value.toByteArray();
        this.start = 0;
    }

    public ASN1Enumerated(byte[] contents) {
        this(contents, true);
    }

    ASN1Enumerated(byte[] contents, boolean clone) {
        if (ASN1Integer.isMalformed(contents)) {
            throw new IllegalArgumentException("malformed enumerated");
        }
        if (0 != (contents[0] & 0x80)) {
            throw new IllegalArgumentException("enumerated must be non-negative");
        }
        this.contents = clone ? Arrays.clone(contents) : contents;
        this.start = ASN1Integer.signBytesToSkip(contents);
    }

    public BigInteger getValue() {
        return new BigInteger(this.contents);
    }

    public boolean hasValue(int x) {
        return this.contents.length - this.start <= 4 && ASN1Integer.intValue(this.contents, this.start, -1) == x;
    }

    public boolean hasValue(BigInteger x) {
        return null != x && ASN1Integer.intValue(this.contents, this.start, -1) == x.intValue() && this.getValue().equals(x);
    }

    public int intValueExact() {
        int count = this.contents.length - this.start;
        if (count > 4) {
            throw new ArithmeticException("ASN.1 Enumerated out of int range");
        }
        return ASN1Integer.intValue(this.contents, this.start, -1);
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
        out.writeEncodingDL(withTag, 10, this.contents);
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1Enumerated)) {
            return false;
        }
        ASN1Enumerated other = (ASN1Enumerated)o;
        return Arrays.areEqual(this.contents, other.contents);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1Enumerated createPrimitive(byte[] contents, boolean clone) {
        if (contents.length > 1) {
            return new ASN1Enumerated(contents, clone);
        }
        if (contents.length == 0) {
            throw new IllegalArgumentException("ENUMERATED has zero length");
        }
        int value = contents[0] & 0xFF;
        if (value >= cache.length) {
            return new ASN1Enumerated(contents, clone);
        }
        ASN1Enumerated possibleMatch = cache[value];
        if (possibleMatch == null) {
            possibleMatch = ASN1Enumerated.cache[value] = new ASN1Enumerated(contents, clone);
        }
        return possibleMatch;
    }
}

