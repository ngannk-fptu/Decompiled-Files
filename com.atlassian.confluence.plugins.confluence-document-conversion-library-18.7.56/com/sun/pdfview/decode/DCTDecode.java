/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decode.ImageDataDecoder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.monte.media.jpeg.JPEGImageIO;

public class DCTDecode {
    protected static ByteBuffer decode(PDFObject dict, ByteBuffer buf, PDFObject params) throws PDFParseException {
        BufferedImage bimg = DCTDecode.loadImageData(buf);
        byte[] output = ImageDataDecoder.decodeImageData(bimg);
        return ByteBuffer.wrap(output);
    }

    private static BufferedImage loadImageData(ByteBuffer buf) throws PDFParseException {
        BufferedImage bimg;
        buf.rewind();
        byte[] input = new byte[buf.remaining()];
        buf.get(input);
        try {
            bimg = JPEGImageIO.read(new ByteArrayInputStream(input), false);
        }
        catch (IOException ex) {
            PDFParseException ex2 = new PDFParseException("DCTDecode failed");
            ex2.initCause(ex);
            throw ex2;
        }
        return bimg;
    }
}

