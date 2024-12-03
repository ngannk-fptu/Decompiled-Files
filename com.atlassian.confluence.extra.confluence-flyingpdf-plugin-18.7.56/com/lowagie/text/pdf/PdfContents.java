/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

class PdfContents
extends PdfStream {
    static final byte[] SAVESTATE = DocWriter.getISOBytes("q\n");
    static final byte[] RESTORESTATE = DocWriter.getISOBytes("Q\n");
    static final byte[] ROTATE90 = DocWriter.getISOBytes("0 1 -1 0 ");
    static final byte[] ROTATE180 = DocWriter.getISOBytes("-1 0 0 -1 ");
    static final byte[] ROTATE270 = DocWriter.getISOBytes("0 -1 1 0 ");
    static final byte[] ROTATEFINAL = DocWriter.getISOBytes(" cm\n");

    PdfContents(PdfContentByte under, PdfContentByte content, PdfContentByte text, PdfContentByte secondContent, Rectangle page) throws BadPdfFormatException {
        try {
            OutputStream out = null;
            Deflater deflater = null;
            this.streamBytes = new ByteArrayOutputStream();
            if (Document.compress) {
                this.compressed = true;
                this.compressionLevel = text.getPdfWriter().getCompressionLevel();
                deflater = new Deflater(this.compressionLevel);
                out = new DeflaterOutputStream((OutputStream)this.streamBytes, deflater);
            } else {
                out = this.streamBytes;
            }
            int rotation = page.getRotation();
            switch (rotation) {
                case 90: {
                    out.write(ROTATE90);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
                    out.write(32);
                    out.write(48);
                    out.write(ROTATEFINAL);
                    break;
                }
                case 180: {
                    out.write(ROTATE180);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
                    out.write(32);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
                    out.write(ROTATEFINAL);
                    break;
                }
                case 270: {
                    out.write(ROTATE270);
                    out.write(48);
                    out.write(32);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
                    out.write(ROTATEFINAL);
                }
            }
            if (under.size() > 0) {
                out.write(SAVESTATE);
                under.getInternalBuffer().writeTo(out);
                out.write(RESTORESTATE);
            }
            if (content.size() > 0) {
                out.write(SAVESTATE);
                content.getInternalBuffer().writeTo(out);
                out.write(RESTORESTATE);
            }
            if (text != null) {
                out.write(SAVESTATE);
                text.getInternalBuffer().writeTo(out);
                out.write(RESTORESTATE);
            }
            if (secondContent.size() > 0) {
                secondContent.getInternalBuffer().writeTo(out);
            }
            out.close();
            if (deflater != null) {
                deflater.end();
            }
        }
        catch (Exception e) {
            throw new BadPdfFormatException(e.getMessage());
        }
        this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
        if (this.compressed) {
            this.put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
    }
}

