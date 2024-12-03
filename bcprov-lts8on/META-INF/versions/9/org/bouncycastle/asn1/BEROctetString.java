/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DEROctetString;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class BEROctetString
extends ASN1OctetString {
    private static final int DEFAULT_SEGMENT_LIMIT = 1000;
    private final int segmentLimit;
    private final ASN1OctetString[] elements;

    static byte[] flattenOctetStrings(ASN1OctetString[] octetStrings) {
        int count = octetStrings.length;
        switch (count) {
            case 0: {
                return EMPTY_OCTETS;
            }
            case 1: {
                return octetStrings[0].string;
            }
        }
        int totalOctets = 0;
        for (int i = 0; i < count; ++i) {
            totalOctets += octetStrings[i].string.length;
        }
        byte[] string = new byte[totalOctets];
        int pos = 0;
        for (int i = 0; i < count; ++i) {
            byte[] octets = octetStrings[i].string;
            System.arraycopy(octets, 0, string, pos, octets.length);
            pos += octets.length;
        }
        return string;
    }

    public BEROctetString(byte[] string) {
        this(string, 1000);
    }

    public BEROctetString(ASN1OctetString[] elements) {
        this(elements, 1000);
    }

    public BEROctetString(byte[] string, int segmentLimit) {
        this(string, null, segmentLimit);
    }

    public BEROctetString(ASN1OctetString[] elements, int segmentLimit) {
        this(BEROctetString.flattenOctetStrings(elements), elements, segmentLimit);
    }

    private BEROctetString(byte[] string, ASN1OctetString[] elements, int segmentLimit) {
        super(string);
        this.elements = elements;
        this.segmentLimit = segmentLimit;
    }

    @Override
    boolean encodeConstructed() {
        return true;
    }

    @Override
    int encodedLength(boolean withTag) throws IOException {
        int totalLength;
        int n = totalLength = withTag ? 4 : 3;
        if (null != this.elements) {
            for (int i = 0; i < this.elements.length; ++i) {
                totalLength += this.elements[i].encodedLength(true);
            }
        } else {
            int fullSegments = this.string.length / this.segmentLimit;
            totalLength += fullSegments * DEROctetString.encodedLength(true, this.segmentLimit);
            int lastSegmentLength = this.string.length - fullSegments * this.segmentLimit;
            if (lastSegmentLength > 0) {
                totalLength += DEROctetString.encodedLength(true, lastSegmentLength);
            }
        }
        return totalLength;
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeIdentifier(withTag, 36);
        out.write(128);
        if (null != this.elements) {
            out.writePrimitives(this.elements);
        } else {
            int segmentLength;
            for (int pos = 0; pos < this.string.length; pos += segmentLength) {
                segmentLength = Math.min(this.string.length - pos, this.segmentLimit);
                DEROctetString.encode(out, true, this.string, pos, segmentLength);
            }
        }
        out.write(0);
        out.write(0);
    }
}

