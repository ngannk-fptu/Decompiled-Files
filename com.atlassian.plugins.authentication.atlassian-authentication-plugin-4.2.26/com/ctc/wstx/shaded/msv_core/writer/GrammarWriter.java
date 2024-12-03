/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer;

import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;

public interface GrammarWriter {
    public void setDocumentHandler(DocumentHandler var1);

    public void write(Grammar var1) throws UnsupportedOperationException, SAXException;
}

