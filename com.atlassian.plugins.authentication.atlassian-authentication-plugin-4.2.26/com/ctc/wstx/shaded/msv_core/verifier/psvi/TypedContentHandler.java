/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.psvi;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import org.xml.sax.SAXException;

public interface TypedContentHandler {
    public void startDocument(ValidationContext var1) throws SAXException;

    public void endDocument() throws SAXException;

    public void characterChunk(String var1, Datatype var2) throws SAXException;

    public void startElement(String var1, String var2, String var3) throws SAXException;

    public void endElement(String var1, String var2, String var3, ElementExp var4) throws SAXException;

    public void startAttribute(String var1, String var2, String var3) throws SAXException;

    public void endAttribute(String var1, String var2, String var3, AttributeExp var4) throws SAXException;

    public void endAttributePart() throws SAXException;
}

