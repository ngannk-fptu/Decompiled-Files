/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.TextAssembler;
import com.lowagie.text.pdf.parser.TextAssemblyBuffer;
import javax.annotation.Nullable;

public class FinalText
implements TextAssemblyBuffer {
    private String content;

    public FinalText(String content) {
        this.content = content;
    }

    @Override
    @Nullable
    public String getText() {
        return this.content;
    }

    @Override
    public void accumulate(TextAssembler p, String contextName) {
        p.process(this, contextName);
    }

    @Override
    public void assemble(TextAssembler p) {
        p.renderText(this);
    }

    @Override
    public FinalText getFinalText(PdfReader reader, int page, TextAssembler assembler, boolean useMarkup) {
        return this;
    }

    public String toString() {
        return "[FinalText: [" + this.getText() + "] d]";
    }
}

