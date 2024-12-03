/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.parser.FinalText;
import com.lowagie.text.pdf.parser.ParsedText;
import com.lowagie.text.pdf.parser.ParsedTextImpl;
import com.lowagie.text.pdf.parser.Word;

public interface TextAssembler {
    public void process(FinalText var1, String var2);

    public void process(Word var1, String var2);

    public void process(ParsedText var1, String var2);

    public void renderText(FinalText var1);

    public void renderText(ParsedTextImpl var1);

    public FinalText endParsingContext(String var1);

    public String getWordId();

    public void setPage(int var1);

    public void reset();
}

