/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.interfaces;

import com.lowagie.text.pdf.PdfDeveloperExtension;
import com.lowagie.text.pdf.PdfName;

public interface PdfVersion {
    public void setPdfVersion(char var1);

    public void setAtLeastPdfVersion(char var1);

    public void setPdfVersion(PdfName var1);

    public void addDeveloperExtension(PdfDeveloperExtension var1);
}

