/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1StreamParser;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class ConstructedOctetStream
extends InputStream {
    private final ASN1StreamParser _parser;
    private boolean _first = true;
    private InputStream _currentStream;

    ConstructedOctetStream(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            ASN1OctetStringParser next = this.getNextParser();
            if (next == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = next.getOctetStream();
        }
        int totalRead = 0;
        while (true) {
            int numRead;
            if ((numRead = this._currentStream.read(b, off + totalRead, len - totalRead)) >= 0) {
                if ((totalRead += numRead) != len) continue;
                return totalRead;
            }
            ASN1OctetStringParser next = this.getNextParser();
            if (next == null) {
                this._currentStream = null;
                return totalRead < 1 ? -1 : totalRead;
            }
            this._currentStream = next.getOctetStream();
        }
    }

    @Override
    public int read() throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            ASN1OctetStringParser next = this.getNextParser();
            if (next == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = next.getOctetStream();
        }
        int b;
        while ((b = this._currentStream.read()) < 0) {
            ASN1OctetStringParser next = this.getNextParser();
            if (next == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = next.getOctetStream();
        }
        return b;
    }

    private ASN1OctetStringParser getNextParser() throws IOException {
        ASN1Encodable asn1Obj = this._parser.readObject();
        if (asn1Obj == null) {
            return null;
        }
        if (asn1Obj instanceof ASN1OctetStringParser) {
            return (ASN1OctetStringParser)asn1Obj;
        }
        throw new IOException("unknown object encountered: " + asn1Obj.getClass());
    }
}

