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
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class JPXDecode {
    protected static ByteBuffer decode(PDFObject dict, ByteBuffer buf, PDFObject params) throws PDFParseException {
        BufferedImage bimg = JPXDecode.loadImageData(buf);
        byte[] output = ImageDataDecoder.decodeImageData(bimg);
        return ByteBuffer.wrap(output);
    }

    private static BufferedImage loadImageData(ByteBuffer buf) throws PDFParseException {
        try {
            byte[] input = new byte[buf.remaining()];
            buf.get(input);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType("image/jpeg2000");
            if (!readers.hasNext()) {
                throw new PDFParseException("JPXDecode failed. No reader available");
            }
            ImageReader reader = readers.next();
            reader.setInput(new MemoryCacheImageInputStream(new ByteArrayInputStream(input)));
            BufferedImage bimg = reader.read(0);
            return bimg;
        }
        catch (IOException e) {
            throw new PDFParseException("JPXDecode failed", e);
        }
    }
}

