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
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1UTF8String
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTF8String.class, 12){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1UTF8String.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1UTF8String getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1UTF8String) {
            return (ASN1UTF8String)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1UTF8String) {
            return (ASN1UTF8String)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1UTF8String)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1UTF8String getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1UTF8String)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1UTF8String(String string) {
        this(Strings.toUTF8ByteArray(string), false);
    }

    ASN1UTF8String(byte[] contents, boolean clone) {
        this.contents = clone ? Arrays.clone(contents) : contents;
    }

    @Override
    public final String getString() {
        return Strings.fromUTF8ByteArray(this.contents);
    }

    public String toString() {
        return this.getString();
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1UTF8String)) {
            return false;
        }
        ASN1UTF8String that = (ASN1UTF8String)other;
        return Arrays.areEqual(this.contents, that.contents);
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
        out.writeEncodingDL(withTag, 12, this.contents);
    }

    static ASN1UTF8String createPrimitive(byte[] contents) {
        return new DERUTF8String(contents, false);
    }
}

