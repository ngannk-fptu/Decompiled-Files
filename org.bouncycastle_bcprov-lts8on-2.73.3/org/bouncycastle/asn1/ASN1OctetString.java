/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1OctetString
extends ASN1Primitive
implements ASN1OctetStringParser {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1OctetString.class, 4){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return octetString;
        }

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence sequence) {
            return sequence.toASN1OctetString();
        }
    };
    static final byte[] EMPTY_OCTETS = new byte[0];
    byte[] string;

    public static ASN1OctetString getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        return (ASN1OctetString)TYPE.getContextInstance(taggedObject, explicit);
    }

    public static ASN1OctetString getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1OctetString) {
            return (ASN1OctetString)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1OctetString) {
                return (ASN1OctetString)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1OctetString)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public ASN1OctetString(byte[] string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string;
    }

    @Override
    public InputStream getOctetStream() {
        return new ByteArrayInputStream(this.string);
    }

    public ASN1OctetStringParser parser() {
        return this;
    }

    public byte[] getOctets() {
        return this.string;
    }

    public int getOctetsLength() {
        return this.getOctets().length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getOctets());
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        if (!(o instanceof ASN1OctetString)) {
            return false;
        }
        ASN1OctetString other = (ASN1OctetString)o;
        return Arrays.areEqual(this.string, other.string);
    }

    @Override
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DEROctetString(this.string);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DEROctetString(this.string);
    }

    public String toString() {
        return "#" + Strings.fromByteArray(Hex.encode(this.string));
    }

    static ASN1OctetString createPrimitive(byte[] contents) {
        return new DEROctetString(contents);
    }
}

