/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 */
package com.atlassian.streams.confluence.renderer;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

class GadgetMacroStripper {
    private XmlEventReaderFactory xmlEventReaderFactory;
    private XmlOutputFactory xmlOutputFactory;

    GadgetMacroStripper(XmlEventReaderFactory xmlEventReaderFactory, XmlOutputFactory xmlOutputFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    String stripGadgetMacros(String content) throws Exception {
        String xml = "<div>" + content + "</div>";
        XMLEventReader xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader((Reader)new StringReader(xml), false);
        StringWriter result = new StringWriter();
        try (XMLEventWriter xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter((Writer)result);){
            while (xmlEventReader.hasNext()) {
                XMLEvent event = xmlEventReader.peek();
                if (event.isStartElement() && this.isGadgetMacro(event.asStartElement())) {
                    this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader).close();
                    continue;
                }
                xmlEventWriter.add(xmlEventReader.nextEvent());
            }
            xmlEventWriter.flush();
        }
        return result.toString();
    }

    private boolean isGadgetMacro(StartElement element) {
        if ("macro".equals(element.getName().getLocalPart())) {
            Iterator<Attribute> attrs = element.getAttributes();
            while (attrs.hasNext()) {
                Attribute attr = attrs.next();
                if (!"name".equals(attr.getName().getLocalPart())) continue;
                return "gadget".equals(attr.getValue());
            }
        }
        return false;
    }
}

