/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface StAXStreamProcessor {
    public void process(XMLStreamWriter var1, Format var2, Document var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, DocType var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, Element var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, List<? extends Content> var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, CDATA var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, Text var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, Comment var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, ProcessingInstruction var3) throws XMLStreamException;

    public void process(XMLStreamWriter var1, Format var2, EntityRef var3) throws XMLStreamException;
}

