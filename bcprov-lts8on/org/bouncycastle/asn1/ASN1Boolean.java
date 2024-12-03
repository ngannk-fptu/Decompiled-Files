/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1Boolean
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Boolean.class, 1){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1Boolean.createPrimitive(octetString.getOctets());
        }
    };
    private static final byte FALSE_VALUE = 0;
    private static final byte TRUE_VALUE = -1;
    public static final ASN1Boolean FALSE = new ASN1Boolean(0);
    public static final ASN1Boolean TRUE = new ASN1Boolean(-1);
    private final byte value;

    public static ASN1Boolean getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1Boolean) {
            return (ASN1Boolean)obj;
        }
        if (obj instanceof byte[]) {
            byte[] enc = (byte[])obj;
            try {
                return (ASN1Boolean)TYPE.fromByteArray(enc);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct boolean from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1Boolean getInstance(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static ASN1Boolean getInstance(int value) {
        return value != 0 ? TRUE : FALSE;
    }

    public static ASN1Boolean getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1Boolean)TYPE.getContextInstance(taggedObject, explicit);
    }

    private ASN1Boolean(byte value) {
        this.value = value;
    }

    public boolean isTrue() {
        return this.value != 0;
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, 1);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 1, this.value);
    }

    @Override
    boolean asn1Equals(ASN1Primitive other) {
        if (!(other instanceof ASN1Boolean)) {
            return false;
        }
        ASN1Boolean that = (ASN1Boolean)other;
        return this.isTrue() == that.isTrue();
    }

    @Override
    public int hashCode() {
        return this.isTrue() ? 1 : 0;
    }

    @Override
    ASN1Primitive toDERObject() {
        return this.isTrue() ? TRUE : FALSE;
    }

    public String toString() {
        return this.isTrue() ? "TRUE" : "FALSE";
    }

    static ASN1Boolean createPrimitive(byte[] contents) {
        if (contents.length != 1) {
            throw new IllegalArgumentException("BOOLEAN value should have 1 byte in it");
        }
        byte b = contents[0];
        switch (b) {
            case 0: {
                return FALSE;
            }
            case -1: {
                return TRUE;
            }
        }
        return new ASN1Boolean(b);
    }
}

