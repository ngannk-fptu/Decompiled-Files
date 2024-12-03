/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitStringParser;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1StreamParser;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class ConstructedBitStream
extends InputStream {
    private final ASN1StreamParser _parser;
    private final boolean _octetAligned;
    private boolean _first = true;
    private int _padBits = 0;
    private ASN1BitStringParser _currentParser;
    private InputStream _currentStream;

    ConstructedBitStream(ASN1StreamParser parser, boolean octetAligned) {
        this._parser = parser;
        this._octetAligned = octetAligned;
    }

    int getPadBits() {
        return this._padBits;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = this._currentParser.getBitStream();
        }
        int totalRead = 0;
        while (true) {
            int numRead;
            if ((numRead = this._currentStream.read(b, off + totalRead, len - totalRead)) >= 0) {
                if ((totalRead += numRead) != len) continue;
                return totalRead;
            }
            this._padBits = this._currentParser.getPadBits();
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                this._currentStream = null;
                return totalRead < 1 ? -1 : totalRead;
            }
            this._currentStream = this._currentParser.getBitStream();
        }
    }

    @Override
    public int read() throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = this._currentParser.getBitStream();
        }
        int b;
        while ((b = this._currentStream.read()) < 0) {
            this._padBits = this._currentParser.getPadBits();
            this._currentParser = this.getNextParser();
            if (this._currentParser == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = this._currentParser.getBitStream();
        }
        return b;
    }

    private ASN1BitStringParser getNextParser() throws IOException {
        ASN1Encodable asn1Obj = this._parser.readObject();
        if (asn1Obj == null) {
            if (this._octetAligned && this._padBits != 0) {
                throw new IOException("expected octet-aligned bitstring, but found padBits: " + this._padBits);
            }
            return null;
        }
        if (asn1Obj instanceof ASN1BitStringParser) {
            if (this._padBits != 0) {
                throw new IOException("only the last nested bitstring can have padding");
            }
            return (ASN1BitStringParser)asn1Obj;
        }
        throw new IOException("unknown object encountered: " + asn1Obj.getClass());
    }
}

