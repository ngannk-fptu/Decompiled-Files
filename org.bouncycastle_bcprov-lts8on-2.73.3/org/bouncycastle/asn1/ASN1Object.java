/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Encodable;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class ASN1Object
implements ASN1Encodable,
Encodable {
    public void encodeTo(OutputStream output) throws IOException {
        this.toASN1Primitive().encodeTo(output);
    }

    public void encodeTo(OutputStream output, String encoding) throws IOException {
        this.toASN1Primitive().encodeTo(output, encoding);
    }

    @Override
    public byte[] getEncoded() throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        this.toASN1Primitive().encodeTo(bOut);
        return bOut.toByteArray();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        this.toASN1Primitive().encodeTo(bOut, encoding);
        return bOut.toByteArray();
    }

    public int hashCode() {
        return this.toASN1Primitive().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ASN1Encodable)) {
            return false;
        }
        ASN1Encodable other = (ASN1Encodable)o;
        return this.toASN1Primitive().equals(other.toASN1Primitive());
    }

    protected static boolean hasEncodedTagValue(Object obj, int tagValue) {
        return obj instanceof byte[] && ((byte[])obj)[0] == tagValue;
    }

    @Override
    public abstract ASN1Primitive toASN1Primitive();
}

