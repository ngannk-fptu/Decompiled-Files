/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jasper.compiler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

class EncodingDetector {
    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();
    private final String encoding;
    private final int skip;
    private final boolean encodingSpecifiedInProlog;

    EncodingDetector(BufferedInputStream bis) throws IOException {
        bis.mark(4);
        BomResult bomResult = this.processBom(bis);
        bis.reset();
        for (int i = 0; i < bomResult.skip; ++i) {
            bis.read();
        }
        String prologEncoding = this.getPrologEncoding(bis);
        if (prologEncoding == null) {
            this.encodingSpecifiedInProlog = false;
            this.encoding = bomResult.encoding;
        } else {
            this.encodingSpecifiedInProlog = true;
            this.encoding = prologEncoding;
        }
        this.skip = bomResult.skip;
    }

    String getEncoding() {
        return this.encoding;
    }

    int getSkip() {
        return this.skip;
    }

    boolean isEncodingSpecifiedInProlog() {
        return this.encodingSpecifiedInProlog;
    }

    private String getPrologEncoding(InputStream stream) {
        String encoding = null;
        try {
            XMLStreamReader xmlStreamReader = XML_INPUT_FACTORY.createXMLStreamReader(stream);
            encoding = xmlStreamReader.getCharacterEncodingScheme();
        }
        catch (XMLStreamException xMLStreamException) {
            // empty catch block
        }
        return encoding;
    }

    private BomResult processBom(InputStream stream) {
        try {
            int singleByteRead;
            int count;
            byte[] b4 = new byte[4];
            for (count = 0; count < 4 && (singleByteRead = stream.read()) != -1; ++count) {
                b4[count] = (byte)singleByteRead;
            }
            return this.parseBom(b4, count);
        }
        catch (IOException ioe) {
            return new BomResult("UTF-8", 0);
        }
    }

    private BomResult parseBom(byte[] b4, int count) {
        if (count < 2) {
            return new BomResult("UTF-8", 0);
        }
        int b0 = b4[0] & 0xFF;
        int b1 = b4[1] & 0xFF;
        if (b0 == 254 && b1 == 255) {
            return new BomResult("UTF-16BE", 2);
        }
        if (b0 == 255 && b1 == 254) {
            return new BomResult("UTF-16LE", 2);
        }
        if (count < 3) {
            return new BomResult("UTF-8", 0);
        }
        int b2 = b4[2] & 0xFF;
        if (b0 == 239 && b1 == 187 && b2 == 191) {
            return new BomResult("UTF-8", 3);
        }
        if (count < 4) {
            return new BomResult("UTF-8", 0);
        }
        int b3 = b4[3] & 0xFF;
        if (b0 == 0 && b1 == 0 && b2 == 0 && b3 == 60) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b0 == 60 && b1 == 0 && b2 == 0 && b3 == 0) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b0 == 0 && b1 == 0 && b2 == 60 && b3 == 0) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 0) {
            return new BomResult("ISO-10646-UCS-4", 0);
        }
        if (b0 == 0 && b1 == 60 && b2 == 0 && b3 == 63) {
            return new BomResult("UTF-16BE", 0);
        }
        if (b0 == 60 && b1 == 0 && b2 == 63 && b3 == 0) {
            return new BomResult("UTF-16LE", 0);
        }
        if (b0 == 76 && b1 == 111 && b2 == 167 && b3 == 148) {
            return new BomResult("CP037", 0);
        }
        return new BomResult("UTF-8", 0);
    }

    private static class BomResult {
        public final String encoding;
        public final int skip;

        BomResult(String encoding, int skip) {
            this.encoding = encoding;
            this.skip = skip;
        }
    }
}

