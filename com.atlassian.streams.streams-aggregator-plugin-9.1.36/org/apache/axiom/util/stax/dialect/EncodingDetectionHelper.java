/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import javax.xml.stream.XMLStreamException;

class EncodingDetectionHelper {
    private final InputStream stream;
    private final boolean useMark;

    public EncodingDetectionHelper(InputStream stream) {
        this.useMark = stream.markSupported();
        this.stream = this.useMark ? stream : new PushbackInputStream(stream, 4);
    }

    public InputStream getInputStream() {
        return this.stream;
    }

    public String detectEncoding() throws XMLStreamException {
        byte[] startBytes = new byte[4];
        try {
            int c;
            if (this.useMark) {
                this.stream.mark(4);
            }
            int read = 0;
            do {
                if ((c = this.stream.read(startBytes, read, 4 - read)) != -1) continue;
                throw new XMLStreamException("Unexpected end of stream");
            } while ((read += c) < 4);
            if (this.useMark) {
                this.stream.reset();
            } else {
                ((PushbackInputStream)this.stream).unread(startBytes);
            }
        }
        catch (IOException ex) {
            throw new XMLStreamException("Unable to read start bytes", ex);
        }
        int marker = ((startBytes[0] & 0xFF) << 24) + ((startBytes[1] & 0xFF) << 16) + ((startBytes[2] & 0xFF) << 8) + (startBytes[3] & 0xFF);
        switch (marker) {
            case -16842752: 
            case -131072: 
            case 60: 
            case 15360: 
            case 65279: 
            case 65534: 
            case 0x3C0000: 
            case 0x3C000000: {
                return "UCS-4";
            }
            case 3932223: {
                return "UTF-16BE";
            }
            case 1006649088: {
                return "UTF-16LE";
            }
            case 1010792557: {
                return "UTF-8";
            }
        }
        if ((marker & 0xFFFF0000) == -16842752) {
            return "UTF-16BE";
        }
        if ((marker & 0xFFFF0000) == -131072) {
            return "UTF-16LE";
        }
        return "UTF-8";
    }
}

