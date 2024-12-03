/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.xhtml.api.MacroDefinition;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface CommonMacroAttributeWriter {
    public void writeCommonAttributes(MacroDefinition var1, XMLStreamWriter var2) throws XMLStreamException;
}

