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
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1PrintableString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1PrintableString.class, 19){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1PrintableString.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1PrintableString getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1PrintableString) {
            return (ASN1PrintableString)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1PrintableString) {
            return (ASN1PrintableString)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1PrintableString)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1PrintableString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1PrintableString)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1PrintableString(String string, boolean validate) {
        if (validate && !ASN1PrintableString.isPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.contents = Strings.toByteArray(string);
    }

    ASN1PrintableString(byte[] contents, boolean clone) {
        this.contents = clone ? Arrays.clone(contents) : contents;
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
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
        out.writeEncodingDL(withTag, 19, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1PrintableString)) {
            return false;
        }
        ASN1PrintableString that = (ASN1PrintableString)other;
        return Arrays.areEqual(this.contents, that.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public String toString() {
        return this.getString();
    }

    public static boolean isPrintableString(String str) {
        block3: for (int i = str.length() - 1; i >= 0; --i) {
            char ch = str.charAt(i);
            if (ch > '\u007f') {
                return false;
            }
            if ('a' <= ch && ch <= 'z' || 'A' <= ch && ch <= 'Z' || '0' <= ch && ch <= '9') continue;
            switch (ch) {
                case ' ': 
                case '\'': 
                case '(': 
                case ')': 
                case '+': 
                case ',': 
                case '-': 
                case '.': 
                case '/': 
                case ':': 
                case '=': 
                case '?': {
                    continue block3;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }

    static ASN1PrintableString createPrimitive(byte[] contents) {
        return new DERPrintableString(contents, false);
    }
}

