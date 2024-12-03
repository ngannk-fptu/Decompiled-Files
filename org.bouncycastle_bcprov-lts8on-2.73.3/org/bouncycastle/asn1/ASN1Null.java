/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1Null
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Null.class, 5){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1Null.createPrimitive(octetString.getOctets());
        }
    };

    public static ASN1Null getInstance(Object o) {
        if (o instanceof ASN1Null) {
            return (ASN1Null)o;
        }
        if (o != null) {
            try {
                return (ASN1Null)TYPE.fromByteArray((byte[])o);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct NULL from byte[]: " + e.getMessage());
            }
        }
        return null;
    }

    public static ASN1Null getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1Null)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1Null() {
    }

    @Override
    public int hashCode() {
        return -1;
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        return o instanceof ASN1Null;
    }

    public String toString() {
        return "NULL";
    }

    static ASN1Null createPrimitive(byte[] contents) {
        if (0 != contents.length) {
            throw new IllegalStateException("malformed NULL encoding encountered");
        }
        return DERNull.INSTANCE;
    }
}

