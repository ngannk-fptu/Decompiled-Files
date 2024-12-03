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
import org.bouncycastle.asn1.DERNumericString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1NumericString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1NumericString.class, 18){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1NumericString.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1NumericString getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1NumericString) {
            return (ASN1NumericString)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1NumericString) {
            return (ASN1NumericString)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1NumericString)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1NumericString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1NumericString)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1NumericString(String string, boolean validate) {
        if (validate && !ASN1NumericString.isNumericString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.contents = Strings.toByteArray(string);
    }

    ASN1NumericString(byte[] contents, boolean clone) {
        this.contents = clone ? Arrays.clone(contents) : contents;
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
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
        out.writeEncodingDL(withTag, 18, this.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1NumericString)) {
            return false;
        }
        ASN1NumericString that = (ASN1NumericString)other;
        return Arrays.areEqual(this.contents, that.contents);
    }

    public static boolean isNumericString(String str) {
        for (int i = str.length() - 1; i >= 0; --i) {
            char ch = str.charAt(i);
            if (ch > '\u007f') {
                return false;
            }
            if ('0' <= ch && ch <= '9' || ch == ' ') continue;
            return false;
        }
        return true;
    }

    static boolean isNumericString(byte[] contents) {
        block3: for (int i = 0; i < contents.length; ++i) {
            switch (contents[i]) {
                case 32: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    continue block3;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }

    static ASN1NumericString createPrimitive(byte[] contents) {
        return new DERNumericString(contents, false);
    }
}

