/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Image;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfImage
extends PdfStream {
    static final int TRANSFERSIZE = 4096;
    protected PdfName name = null;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public PdfImage(Image image, String name, PdfIndirectReference maskRef) throws BadPdfFormatException {
        this.name = new PdfName(name);
        this.put(PdfName.TYPE, PdfName.XOBJECT);
        this.put(PdfName.SUBTYPE, PdfName.IMAGE);
        this.put(PdfName.WIDTH, new PdfNumber(image.getWidth()));
        this.put(PdfName.HEIGHT, new PdfNumber(image.getHeight()));
        if (image.getLayer() != null) {
            this.put(PdfName.OC, image.getLayer().getRef());
        }
        if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 255)) {
            this.put(PdfName.IMAGEMASK, PdfBoolean.PDFTRUE);
        }
        if (maskRef != null) {
            if (image.isSmask()) {
                this.put(PdfName.SMASK, maskRef);
            } else {
                this.put(PdfName.MASK, maskRef);
            }
        }
        if (image.isMask() && image.isInverted()) {
            this.put(PdfName.DECODE, new PdfLiteral("[1 0]"));
        }
        if (image.isInterpolation()) {
            this.put(PdfName.INTERPOLATE, PdfBoolean.PDFTRUE);
        }
        InputStream is = null;
        try {
            String errorID;
            if (image.isImgRaw()) {
                PdfDictionary additional;
                int colorspace = image.getColorspace();
                int[] transparency = image.getTransparency();
                if (transparency != null && !image.isMask() && maskRef == null) {
                    String s = "[";
                    for (int i : transparency) {
                        s = s + i + " ";
                    }
                    s = s + "]";
                    this.put(PdfName.MASK, new PdfLiteral(s));
                }
                this.bytes = image.getRawData();
                this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                int bpc = image.getBpc();
                if (bpc > 255) {
                    if (!image.isMask()) {
                        this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                    }
                    this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
                    this.put(PdfName.FILTER, PdfName.CCITTFAXDECODE);
                    int k = bpc - 257;
                    PdfDictionary decodeparms = new PdfDictionary();
                    if (k != 0) {
                        decodeparms.put(PdfName.K, new PdfNumber(k));
                    }
                    if ((colorspace & 1) != 0) {
                        decodeparms.put(PdfName.BLACKIS1, PdfBoolean.PDFTRUE);
                    }
                    if ((colorspace & 2) != 0) {
                        decodeparms.put(PdfName.ENCODEDBYTEALIGN, PdfBoolean.PDFTRUE);
                    }
                    if ((colorspace & 4) != 0) {
                        decodeparms.put(PdfName.ENDOFLINE, PdfBoolean.PDFTRUE);
                    }
                    if ((colorspace & 8) != 0) {
                        decodeparms.put(PdfName.ENDOFBLOCK, PdfBoolean.PDFFALSE);
                    }
                    decodeparms.put(PdfName.COLUMNS, new PdfNumber(image.getWidth()));
                    decodeparms.put(PdfName.ROWS, new PdfNumber(image.getHeight()));
                    this.put(PdfName.DECODEPARMS, decodeparms);
                    return;
                }
                switch (colorspace) {
                    case 1: {
                        this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                        if (!image.isInverted()) break;
                        this.put(PdfName.DECODE, new PdfLiteral("[1 0]"));
                        break;
                    }
                    case 3: {
                        this.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                        if (!image.isInverted()) break;
                        this.put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0]"));
                        break;
                    }
                    default: {
                        this.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                        if (!image.isInverted()) break;
                        this.put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
                    }
                }
                if ((additional = image.getAdditional()) != null) {
                    this.putAll(additional);
                }
                if (image.isMask() && (image.getBpc() == 1 || image.getBpc() > 8)) {
                    this.remove(PdfName.COLORSPACE);
                }
                this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(image.getBpc()));
                if (image.isDeflated()) {
                    this.put(PdfName.FILTER, PdfName.FLATEDECODE);
                    return;
                }
                this.flateCompress(image.getCompressionLevel());
                return;
            }
            if (image.getRawData() == null) {
                is = image.getUrl().openStream();
                errorID = image.getUrl().toString();
            } else {
                is = new ByteArrayInputStream(image.getRawData());
                errorID = "Byte array";
            }
            switch (image.type()) {
                case 32: {
                    this.put(PdfName.FILTER, PdfName.DCTDECODE);
                    switch (image.getColorspace()) {
                        case 1: {
                            this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                            break;
                        }
                        case 3: {
                            this.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                            break;
                        }
                        default: {
                            this.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                            if (!image.isInverted()) break;
                            this.put(PdfName.DECODE, new PdfLiteral("[1 0 1 0 1 0 1 0]"));
                        }
                    }
                    this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                    if (image.getRawData() != null) {
                        this.bytes = image.getRawData();
                        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                        return;
                    }
                    this.streamBytes = new ByteArrayOutputStream();
                    PdfImage.transferBytes(is, this.streamBytes, -1);
                    break;
                }
                case 33: {
                    this.put(PdfName.FILTER, PdfName.JPXDECODE);
                    if (image.getColorspace() > 0) {
                        switch (image.getColorspace()) {
                            case 1: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                                break;
                            }
                            case 3: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                break;
                            }
                            default: {
                                this.put(PdfName.COLORSPACE, PdfName.DEVICECMYK);
                            }
                        }
                        this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(image.getBpc()));
                    }
                    if (image.getRawData() != null) {
                        this.bytes = image.getRawData();
                        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                        return;
                    }
                    this.streamBytes = new ByteArrayOutputStream();
                    PdfImage.transferBytes(is, this.streamBytes, -1);
                    break;
                }
                case 36: {
                    this.put(PdfName.FILTER, PdfName.JBIG2DECODE);
                    this.put(PdfName.COLORSPACE, PdfName.DEVICEGRAY);
                    this.put(PdfName.BITSPERCOMPONENT, new PdfNumber(1));
                    if (image.getRawData() != null) {
                        this.bytes = image.getRawData();
                        this.put(PdfName.LENGTH, new PdfNumber(this.bytes.length));
                        return;
                    }
                    this.streamBytes = new ByteArrayOutputStream();
                    PdfImage.transferBytes(is, this.streamBytes, -1);
                    break;
                }
                default: {
                    throw new BadPdfFormatException(MessageLocalization.getComposedMessage("1.is.an.unknown.image.format", errorID));
                }
            }
            this.put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            return;
        }
        catch (IOException ioe) {
            throw new BadPdfFormatException(ioe.getMessage());
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    public PdfName name() {
        return this.name;
    }

    static void transferBytes(InputStream in, OutputStream out, int len) throws IOException {
        byte[] buffer = new byte[4096];
        if (len < 0) {
            len = 0x7FFF0000;
        }
        while (len != 0) {
            int size = in.read(buffer, 0, Math.min(len, 4096));
            if (size < 0) {
                return;
            }
            out.write(buffer, 0, size);
            len -= size;
        }
    }

    protected void importAll(PdfImage dup) {
        this.name = dup.name;
        this.compressed = dup.compressed;
        this.compressionLevel = dup.compressionLevel;
        this.streamBytes = dup.streamBytes;
        this.bytes = dup.bytes;
        this.hashMap = dup.hashMap;
    }
}

