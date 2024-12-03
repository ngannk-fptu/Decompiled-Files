/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DLBitString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BERBitString
extends ASN1BitString {
    private static final int DEFAULT_SEGMENT_LIMIT = 1000;
    private final int segmentLimit;
    private final ASN1BitString[] elements;

    static byte[] flattenBitStrings(ASN1BitString[] bitStrings) {
        int count = bitStrings.length;
        switch (count) {
            case 0: {
                return new byte[]{0};
            }
            case 1: {
                return bitStrings[0].contents;
            }
        }
        int last = count - 1;
        int totalLength = 0;
        for (int i = 0; i < last; ++i) {
            byte[] elementContents = bitStrings[i].contents;
            if (elementContents[0] != 0) {
                throw new IllegalArgumentException("only the last nested bitstring can have padding");
            }
            totalLength += elementContents.length - 1;
        }
        byte[] lastElementContents = bitStrings[last].contents;
        byte padBits = lastElementContents[0];
        byte[] contents = new byte[totalLength += lastElementContents.length];
        contents[0] = padBits;
        int pos = 1;
        for (int i = 0; i < count; ++i) {
            byte[] elementContents = bitStrings[i].contents;
            int length = elementContents.length - 1;
            System.arraycopy(elementContents, 1, contents, pos, length);
            pos += length;
        }
        return contents;
    }

    public BERBitString(byte[] data) {
        this(data, 0);
    }

    public BERBitString(byte data, int padBits) {
        super(data, padBits);
        this.elements = null;
        this.segmentLimit = 1000;
    }

    public BERBitString(byte[] data, int padBits) {
        this(data, padBits, 1000);
    }

    public BERBitString(byte[] data, int padBits, int segmentLimit) {
        super(data, padBits);
        this.elements = null;
        this.segmentLimit = segmentLimit;
    }

    public BERBitString(ASN1Encodable obj) throws IOException {
        this(obj.toASN1Primitive().getEncoded("DER"), 0);
    }

    public BERBitString(ASN1BitString[] elements) {
        this(elements, 1000);
    }

    public BERBitString(ASN1BitString[] elements, int segmentLimit) {
        super(BERBitString.flattenBitStrings(elements), false);
        this.elements = elements;
        this.segmentLimit = segmentLimit;
    }

    BERBitString(byte[] contents, boolean check) {
        super(contents, check);
        this.elements = null;
        this.segmentLimit = 1000;
    }

    @Override
    boolean encodeConstructed() {
        return null != this.elements || this.contents.length > this.segmentLimit;
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        int totalLength;
        if (!this.encodeConstructed()) {
            return DLBitString.encodedLength(withTag, this.contents.length);
        }
        int n = totalLength = withTag ? 4 : 3;
        if (null != this.elements) {
            for (int i = 0; i < this.elements.length; ++i) {
                totalLength += this.elements[i].encodedLength(true);
            }
        } else if (this.contents.length >= 2) {
            int extraSegments = (this.contents.length - 2) / (this.segmentLimit - 1);
            totalLength += extraSegments * DLBitString.encodedLength(true, this.segmentLimit);
            int lastSegmentLength = this.contents.length - extraSegments * (this.segmentLimit - 1);
            totalLength += DLBitString.encodedLength(true, lastSegmentLength);
        }
        return totalLength;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        if (!this.encodeConstructed()) {
            DLBitString.encode(out, withTag, this.contents, 0, this.contents.length);
            return;
        }
        out.writeIdentifier(withTag, 35);
        out.write(128);
        if (null != this.elements) {
            out.writePrimitives(this.elements);
        } else if (this.contents.length >= 2) {
            int remaining;
            byte pad = this.contents[0];
            int length = this.contents.length;
            int segmentLength = this.segmentLimit - 1;
            for (remaining = length - 1; remaining > segmentLength; remaining -= segmentLength) {
                DLBitString.encode(out, true, (byte)0, this.contents, length - remaining, segmentLength);
            }
            DLBitString.encode(out, true, pad, this.contents, length - remaining, remaining);
        }
        out.write(0);
        out.write(0);
    }
}

