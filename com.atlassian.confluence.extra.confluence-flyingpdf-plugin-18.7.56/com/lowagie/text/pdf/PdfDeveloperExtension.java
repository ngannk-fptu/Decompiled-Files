/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfWriter;

public class PdfDeveloperExtension {
    public static final PdfDeveloperExtension ADOBE_1_7_EXTENSIONLEVEL3 = new PdfDeveloperExtension(PdfName.ADBE, PdfWriter.PDF_VERSION_1_7, 3);
    protected PdfName prefix;
    protected PdfName baseversion;
    protected int extensionLevel;

    public PdfDeveloperExtension(PdfName prefix, PdfName baseversion, int extensionLevel) {
        this.prefix = prefix;
        this.baseversion = baseversion;
        this.extensionLevel = extensionLevel;
    }

    public PdfName getPrefix() {
        return this.prefix;
    }

    public PdfName getBaseversion() {
        return this.baseversion;
    }

    public int getExtensionLevel() {
        return this.extensionLevel;
    }

    public PdfDictionary getDeveloperExtensions() {
        PdfDictionary developerextensions = new PdfDictionary();
        developerextensions.put(PdfName.BASEVERSION, this.baseversion);
        developerextensions.put(PdfName.EXTENSIONLEVEL, new PdfNumber(this.extensionLevel));
        return developerextensions;
    }
}

