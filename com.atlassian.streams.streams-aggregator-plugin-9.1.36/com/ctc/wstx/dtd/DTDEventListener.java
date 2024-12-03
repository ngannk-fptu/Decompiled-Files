/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import java.net.URL;
import javax.xml.stream.XMLStreamException;

public interface DTDEventListener {
    public boolean dtdReportComments();

    public void dtdProcessingInstruction(String var1, String var2);

    public void dtdComment(char[] var1, int var2, int var3);

    public void dtdSkippedEntity(String var1);

    public void dtdNotationDecl(String var1, String var2, String var3, URL var4) throws XMLStreamException;

    public void dtdUnparsedEntityDecl(String var1, String var2, String var3, String var4, URL var5) throws XMLStreamException;

    public void attributeDecl(String var1, String var2, String var3, String var4, String var5);

    public void dtdElementDecl(String var1, String var2);

    public void dtdExternalEntityDecl(String var1, String var2, String var3);

    public void dtdInternalEntityDecl(String var1, String var2);
}

