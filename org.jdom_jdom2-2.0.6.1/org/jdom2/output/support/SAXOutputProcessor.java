/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMException;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.support.SAXTarget;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SAXOutputProcessor {
    public void process(SAXTarget var1, Format var2, Document var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, DocType var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, Element var3) throws JDOMException;

    public void processAsDocument(SAXTarget var1, Format var2, Element var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, List<? extends Content> var3) throws JDOMException;

    public void processAsDocument(SAXTarget var1, Format var2, List<? extends Content> var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, CDATA var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, Text var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, Comment var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, ProcessingInstruction var3) throws JDOMException;

    public void process(SAXTarget var1, Format var2, EntityRef var3) throws JDOMException;
}

