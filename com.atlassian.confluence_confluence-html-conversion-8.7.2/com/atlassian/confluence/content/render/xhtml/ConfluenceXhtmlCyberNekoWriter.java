/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.AttributeEntityNonResolvingWriter;
import java.io.Writer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

class ConfluenceXhtmlCyberNekoWriter
extends AttributeEntityNonResolvingWriter {
    private boolean encounteredCData = false;

    public ConfluenceXhtmlCyberNekoWriter(Writer writer, String encoding) {
        super(writer, encoding);
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        this.encounteredCData = true;
        this.fPrinter.print("<![CDATA[");
        super.startCDATA(augs);
    }

    public void endCDATA(Augmentations augs) throws XNIException {
        this.fPrinter.print("]]>");
        super.endCDATA(augs);
        this.encounteredCData = false;
    }

    protected void printCharacters(XMLString text, boolean normalize) {
        if (this.encounteredCData) {
            super.printCharacters(text, false);
        } else {
            super.printCharacters(text, normalize);
        }
    }
}

