/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfMediaClipData;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfString;
import java.io.IOException;

public class PdfRendition
extends PdfDictionary {
    PdfRendition(String file, PdfFileSpecification fs, String mimeType) throws IOException {
        this.put(PdfName.S, new PdfName("MR"));
        this.put(PdfName.N, new PdfString("Rendition for " + file));
        this.put(PdfName.C, new PdfMediaClipData(file, fs, mimeType));
    }
}

