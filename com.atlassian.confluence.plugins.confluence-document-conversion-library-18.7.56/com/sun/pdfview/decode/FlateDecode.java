/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decode.Predictor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class FlateDecode {
    public static ByteBuffer decode(PDFObject dict, ByteBuffer buf, PDFObject params) throws IOException {
        Predictor predictor;
        Inflater inf = new Inflater(false);
        int bufSize = buf.remaining();
        byte[] data = new byte[bufSize];
        buf.get(data);
        inf.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] decomp = new byte[bufSize];
        boolean loc = false;
        int read = 0;
        try {
            while (!inf.finished()) {
                read = inf.inflate(decomp);
                if (read <= 0) {
                    if (inf.needsDictionary()) {
                        throw new PDFParseException("Don't know how to ask for a dictionary in FlateDecode");
                    }
                    return ByteBuffer.allocate(0);
                }
                baos.write(decomp, 0, read);
            }
        }
        catch (DataFormatException dfe) {
            throw new PDFParseException("Data format exception:" + dfe.getMessage());
        }
        ByteBuffer outBytes = ByteBuffer.wrap(baos.toByteArray());
        if (params != null && params.getDictionary().containsKey("Predictor") && (predictor = Predictor.getPredictor(params)) != null) {
            outBytes = predictor.unpredict(outBytes);
        }
        return outBytes;
    }
}

