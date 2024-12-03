/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OutputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1Primitive
extends ASN1Object {
    ASN1Primitive() {
    }

    @Override
    public void encodeTo(OutputStream output) throws IOException {
        ASN1OutputStream asn1Out = ASN1OutputStream.create(output);
        asn1Out.writePrimitive(this, true);
        asn1Out.flushInternal();
    }

    @Override
    public void encodeTo(OutputStream output, String encoding) throws IOException {
        ASN1OutputStream asn1Out = ASN1OutputStream.create(output, encoding);
        asn1Out.writePrimitive(this, true);
        asn1Out.flushInternal();
    }

    public static ASN1Primitive fromByteArray(byte[] data) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(data);
        try {
            ASN1Primitive o = aIn.readObject();
            if (aIn.available() != 0) {
                throw new IOException("Extra data detected in stream");
            }
            return o;
        }
        catch (ClassCastException e) {
            throw new IOException("cannot recognise object in stream");
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof ASN1Encodable && this.asn1Equals(((ASN1Encodable)o).toASN1Primitive());
    }

    public final boolean equals(ASN1Encodable other) {
        return this == other || null != other && this.asn1Equals(other.toASN1Primitive());
    }

    public final boolean equals(ASN1Primitive other) {
        return this == other || this.asn1Equals(other);
    }

    @Override
    public final ASN1Primitive toASN1Primitive() {
        return this;
    }

    ASN1Primitive toDERObject() {
        return this;
    }

    ASN1Primitive toDLObject() {
        return this;
    }

    @Override
    public abstract int hashCode();

    abstract boolean encodeConstructed();

    abstract int encodedLength(boolean var1) throws IOException;

    abstract void encode(ASN1OutputStream var1, boolean var2) throws IOException;

    abstract boolean asn1Equals(ASN1Primitive var1);
}

