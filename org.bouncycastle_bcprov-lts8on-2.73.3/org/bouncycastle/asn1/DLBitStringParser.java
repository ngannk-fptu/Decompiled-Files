/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1BitStringParser;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DefiniteLengthInputStream;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class DLBitStringParser
implements ASN1BitStringParser {
    private final DefiniteLengthInputStream stream;
    private int padBits = 0;

    DLBitStringParser(DefiniteLengthInputStream stream) {
        this.stream = stream;
    }

    @Override
    public InputStream getBitStream() throws IOException {
        return this.getBitStream(false);
    }

    @Override
    public InputStream getOctetStream() throws IOException {
        return this.getBitStream(true);
    }

    @Override
    public int getPadBits() {
        return this.padBits;
    }

    @Override
    public ASN1Primitive getLoadedObject() throws IOException {
        return ASN1BitString.createPrimitive(this.stream.toByteArray());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            return this.getLoadedObject();
        }
        catch (IOException e) {
            throw new ASN1ParsingException("IOException converting stream to byte array: " + e.getMessage(), e);
        }
    }

    private InputStream getBitStream(boolean octetAligned) throws IOException {
        int length = this.stream.getRemaining();
        if (length < 1) {
            throw new IllegalStateException("content octets cannot be empty");
        }
        this.padBits = this.stream.read();
        if (this.padBits > 0) {
            if (length < 2) {
                throw new IllegalStateException("zero length data with non-zero pad bits");
            }
            if (this.padBits > 7) {
                throw new IllegalStateException("pad bits cannot be greater than 7 or less than 0");
            }
            if (octetAligned) {
                throw new IOException("expected octet-aligned bitstring, but found padBits: " + this.padBits);
            }
        }
        return this.stream;
    }
}

