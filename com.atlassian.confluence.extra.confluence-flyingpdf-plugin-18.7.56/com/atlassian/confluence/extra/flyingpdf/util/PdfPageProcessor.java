/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

public interface PdfPageProcessor {
    public void processPage(PdfReader var1, PdfStamper var2, int var3);
}

