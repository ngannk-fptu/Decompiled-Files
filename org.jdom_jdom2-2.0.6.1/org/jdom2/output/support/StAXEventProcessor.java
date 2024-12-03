/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventConsumer;
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
public interface StAXEventProcessor {
    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, Document var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, DocType var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, Element var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, List<? extends Content> var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, CDATA var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, Text var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, Comment var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, ProcessingInstruction var4) throws XMLStreamException;

    public void process(XMLEventConsumer var1, Format var2, XMLEventFactory var3, EntityRef var4) throws XMLStreamException;
}

