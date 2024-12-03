/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.io.StreamNormalizingReader
 *  org.apache.batik.util.io.UTF16Decoder
 */
package org.apache.batik.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.Reader;
import org.apache.batik.util.io.StreamNormalizingReader;
import org.apache.batik.util.io.UTF16Decoder;
import org.apache.batik.xml.XMLUtilities;

public class XMLStreamNormalizingReader
extends StreamNormalizingReader {
    public XMLStreamNormalizingReader(InputStream is, String encod) throws IOException {
        PushbackInputStream pbis = new PushbackInputStream(is, 128);
        byte[] buf = new byte[4];
        int len = pbis.read(buf);
        if (len > 0) {
            pbis.unread(buf, 0, len);
        }
        if (len == 4) {
            switch (buf[0] & 0xFF) {
                case 0: {
                    if (buf[1] != 60 || buf[2] != 0 || buf[3] != 63) break;
                    this.charDecoder = new UTF16Decoder((InputStream)pbis, true);
                    return;
                }
                case 60: {
                    switch (buf[1] & 0xFF) {
                        case 0: {
                            if (buf[2] != 63 || buf[3] != 0) break;
                            this.charDecoder = new UTF16Decoder((InputStream)pbis, false);
                            return;
                        }
                        case 63: {
                            if (buf[2] != 120 || buf[3] != 109) break;
                            Reader r = XMLUtilities.createXMLDeclarationReader(pbis, "UTF8");
                            String enc = XMLUtilities.getXMLDeclarationEncoding(r, "UTF-8");
                            this.charDecoder = this.createCharDecoder(pbis, enc);
                            return;
                        }
                    }
                    break;
                }
                case 76: {
                    if (buf[1] != 111 || (buf[2] & 0xFF) != 167 || (buf[3] & 0xFF) != 148) break;
                    Reader r = XMLUtilities.createXMLDeclarationReader(pbis, "CP037");
                    String enc = XMLUtilities.getXMLDeclarationEncoding(r, "EBCDIC-CP-US");
                    this.charDecoder = this.createCharDecoder(pbis, enc);
                    return;
                }
                case 254: {
                    if ((buf[1] & 0xFF) != 255) break;
                    this.charDecoder = this.createCharDecoder(pbis, "UTF-16");
                    return;
                }
                case 255: {
                    if ((buf[1] & 0xFF) != 254) break;
                    this.charDecoder = this.createCharDecoder(pbis, "UTF-16");
                    return;
                }
            }
        }
        encod = encod == null ? "UTF-8" : encod;
        this.charDecoder = this.createCharDecoder(pbis, encod);
    }
}

