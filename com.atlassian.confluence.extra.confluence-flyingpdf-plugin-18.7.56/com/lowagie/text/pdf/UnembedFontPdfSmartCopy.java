/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BadPdfFormatException;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSmartCopy;
import java.io.IOException;
import java.io.OutputStream;

public class UnembedFontPdfSmartCopy
extends PdfSmartCopy {
    public UnembedFontPdfSmartCopy(Document document, OutputStream os) throws DocumentException {
        super(document, os);
    }

    @Override
    protected PdfDictionary copyDictionary(PdfDictionary in) throws IOException, BadPdfFormatException {
        PdfDictionary out = new PdfDictionary();
        PdfObject type = PdfReader.getPdfObjectRelease(in.get(PdfName.TYPE));
        for (PdfName key : in.getKeys()) {
            PdfObject value = in.get(key);
            if ((PdfName.FONTFILE.equals(key) || PdfName.FONTFILE2.equals(key) || PdfName.FONTFILE3.equals(key)) && !PdfReader.isFontSubset(PdfReader.getFontNameFromDescriptor(in))) continue;
            out.put(key, this.copyObject(value));
        }
        return out;
    }
}

