/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.parser.PdfContentStreamHandler;
import java.util.List;
import javax.annotation.Nonnull;

public interface ContentOperator {
    public void invoke(List<PdfObject> var1, PdfContentStreamHandler var2, PdfDictionary var3);

    @Nonnull
    public String getOperatorName();
}

