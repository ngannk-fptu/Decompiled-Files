/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.FinalText;
import com.lowagie.text.pdf.parser.TextAssembler;
import javax.annotation.Nullable;

public interface TextAssemblyBuffer {
    @Nullable
    public String getText();

    public FinalText getFinalText(PdfReader var1, int var2, TextAssembler var3, boolean var4);

    public void accumulate(TextAssembler var1, String var2);

    public void assemble(TextAssembler var1);
}

