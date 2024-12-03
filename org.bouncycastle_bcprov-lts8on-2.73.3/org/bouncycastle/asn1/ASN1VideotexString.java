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
import org.bouncycastle.asn1.DERVideotexString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1VideotexString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1VideotexString.class, 21){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1VideotexString.createPrimitive(octetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1VideotexString getInstance(Object obj) {
        ASN1Primitive primitive;
        if (obj == null || obj instanceof ASN1VideotexString) {
            return (ASN1VideotexString)obj;
        }
        if (obj instanceof ASN1Encodable && (primitive = ((ASN1Encodable)obj).toASN1Primitive()) instanceof ASN1VideotexString) {
            return (ASN1VideotexString)primitive;
        }
        if (obj instanceof byte[]) {
            try {
                return (ASN1VideotexString)TYPE.fromByteArray((byte[])obj);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("encoding error in getInstance: " + e.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1VideotexString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1VideotexString)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1VideotexString(byte[] contents, boolean clone) {
        this.contents = clone ? Arrays.clone(contents) : contents;
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
        out.writeEncodingDL(withTag, 21, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1VideotexString)) {
            return false;
        }
        ASN1VideotexString that = (ASN1VideotexString)other;
        return Arrays.areEqual(this.contents, that.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
    }

    static ASN1VideotexString createPrimitive(byte[] contents) {
        return new DERVideotexString(contents, false);
    }
}

