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
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1IA5String
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1IA5String.class, 22){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1IA5String.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1IA5String getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1IA5String) {
            return (ASN1IA5String)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1IA5String) {
            return (ASN1IA5String)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1IA5String)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1IA5String getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1IA5String)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1IA5String(String string, boolean validate) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        if (validate && !ASN1IA5String.isIA5String(string)) {
            throw new IllegalArgumentException("'string' contains illegal characters");
        }
        this.contents = Strings.toByteArray(string);
    }

    ASN1IA5String(byte[] contents, boolean clone) {
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
        out.writeEncodingDL(withTag, 22, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1IA5String)) {
            return false;
        }
        ASN1IA5String that = (ASN1IA5String)other;
        return Arrays.areEqual(this.contents, that.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public static boolean isIA5String(String str) {
        for (int i = str.length() - 1; i >= 0; --i) {
            char ch = str.charAt(i);
            if (ch <= '\u007f') continue;
            return false;
        }
        return true;
    }

    static ASN1IA5String createPrimitive(byte[] contents) {
        return new DERIA5String(contents, false);
    }
}

