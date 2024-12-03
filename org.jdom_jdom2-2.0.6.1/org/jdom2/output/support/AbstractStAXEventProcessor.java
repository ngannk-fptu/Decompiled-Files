/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.XMLEventConsumer;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.Verifier;
import org.jdom2.output.Format;
import org.jdom2.output.support.AbstractOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.StAXEventProcessor;
import org.jdom2.output.support.Walker;
import org.jdom2.util.NamespaceStack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractStAXEventProcessor
extends AbstractOutputProcessor
implements StAXEventProcessor {
    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, Document doc) throws XMLStreamException {
        this.printDocument(out, new FormatStack(format), new NamespaceStack(), eventfactory, doc);
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, DocType doctype) throws XMLStreamException {
        this.printDocType(out, new FormatStack(format), eventfactory, doctype);
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, Element element) throws XMLStreamException {
        this.printElement(out, new FormatStack(format), new NamespaceStack(), eventfactory, element);
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, List<? extends Content> list) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        Walker walker = this.buildWalker(fstack, list, false);
        this.printContent(out, new FormatStack(format), new NamespaceStack(), eventfactory, walker);
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, CDATA cdata) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        List<CDATA> list = Collections.singletonList(cdata);
        Walker walker = this.buildWalker(fstack, list, false);
        if (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                this.printCDATA(out, fstack, eventfactory, new CDATA(walker.text()));
            } else if (c.getCType() == Content.CType.CDATA) {
                this.printCDATA(out, fstack, eventfactory, (CDATA)c);
            }
        }
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, Text text) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        List<Text> list = Collections.singletonList(text);
        Walker walker = this.buildWalker(fstack, list, false);
        if (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                this.printText(out, fstack, eventfactory, new Text(walker.text()));
            } else if (c.getCType() == Content.CType.Text) {
                this.printText(out, fstack, eventfactory, (Text)c);
            }
        }
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, Comment comment) throws XMLStreamException {
        this.printComment(out, new FormatStack(format), eventfactory, comment);
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, ProcessingInstruction pi) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        fstack.setIgnoreTrAXEscapingPIs(true);
        this.printProcessingInstruction(out, fstack, eventfactory, pi);
    }

    @Override
    public void process(XMLEventConsumer out, Format format, XMLEventFactory eventfactory, EntityRef entity) throws XMLStreamException {
        this.printEntityRef(out, new FormatStack(format), eventfactory, entity);
    }

    protected void printDocument(XMLEventConsumer out, FormatStack fstack, NamespaceStack nstack, XMLEventFactory eventfactory, Document doc) throws XMLStreamException {
        Walker walker;
        ArrayList<Content> list;
        if (fstack.isOmitDeclaration()) {
            out.add(eventfactory.createStartDocument(null, null));
        } else if (fstack.isOmitEncoding()) {
            out.add(eventfactory.createStartDocument(null, "1.0"));
            if (fstack.getLineSeparator() != null) {
                out.add(eventfactory.createCharacters(fstack.getLineSeparator()));
            }
        } else {
            out.add(eventfactory.createStartDocument(fstack.getEncoding(), "1.0"));
            if (fstack.getLineSeparator() != null) {
                out.add(eventfactory.createCharacters(fstack.getLineSeparator()));
            }
        }
        ArrayList<Content> arrayList = list = doc.hasRootElement() ? doc.getContent() : new ArrayList<Content>(doc.getContentSize());
        if (list.isEmpty()) {
            int sz = doc.getContentSize();
            for (int i = 0; i < sz; ++i) {
                list.add(doc.getContent(i));
            }
        }
        if ((walker = this.buildWalker(fstack, list, false)).hasNext()) {
            while (walker.hasNext()) {
                Content c = walker.next();
                if (c == null) {
                    String padding = walker.text();
                    if (padding == null || !Verifier.isAllXMLWhitespace(padding) || walker.isCDATA()) continue;
                    out.add(eventfactory.createCharacters(padding));
                    continue;
                }
                switch (c.getCType()) {
                    case Comment: {
                        this.printComment(out, fstack, eventfactory, (Comment)c);
                        break;
                    }
                    case DocType: {
                        this.printDocType(out, fstack, eventfactory, (DocType)c);
                        break;
                    }
                    case Element: {
                        this.printElement(out, fstack, nstack, eventfactory, (Element)c);
                        break;
                    }
                    case ProcessingInstruction: {
                        this.printProcessingInstruction(out, fstack, eventfactory, (ProcessingInstruction)c);
                        break;
                    }
                }
            }
            if (fstack.getLineSeparator() != null) {
                out.add(eventfactory.createCharacters(fstack.getLineSeparator()));
            }
        }
        out.add(eventfactory.createEndDocument());
    }

    protected void printDocType(XMLEventConsumer out, FormatStack fstack, XMLEventFactory eventfactory, DocType docType) throws XMLStreamException {
        String publicID = docType.getPublicID();
        String systemID = docType.getSystemID();
        String internalSubset = docType.getInternalSubset();
        boolean hasPublic = false;
        StringWriter sw = new StringWriter();
        sw.write("<!DOCTYPE ");
        sw.write(docType.getElementName());
        if (publicID != null) {
            sw.write(" PUBLIC \"");
            sw.write(publicID);
            sw.write("\"");
            hasPublic = true;
        }
        if (systemID != null) {
            if (!hasPublic) {
                sw.write(" SYSTEM");
            }
            sw.write(" \"");
            sw.write(systemID);
            sw.write("\"");
        }
        if (internalSubset != null && !internalSubset.equals("")) {
            sw.write(" [");
            sw.write(fstack.getLineSeparator());
            sw.write(docType.getInternalSubset());
            sw.write("]");
        }
        sw.write(">");
        out.add(eventfactory.createDTD(sw.toString()));
    }

    protected void printProcessingInstruction(XMLEventConsumer out, FormatStack fstack, XMLEventFactory eventfactory, ProcessingInstruction pi) throws XMLStreamException {
        String target = pi.getTarget();
        String rawData = pi.getData();
        if (rawData != null && rawData.trim().length() > 0) {
            out.add(eventfactory.createProcessingInstruction(target, rawData));
        } else {
            out.add(eventfactory.createProcessingInstruction(target, ""));
        }
    }

    protected void printComment(XMLEventConsumer out, FormatStack fstack, XMLEventFactory eventfactory, Comment comment) throws XMLStreamException {
        out.add(eventfactory.createComment(comment.getText()));
    }

    protected void printEntityRef(XMLEventConsumer out, FormatStack fstack, XMLEventFactory eventfactory, EntityRef entity) throws XMLStreamException {
        out.add(eventfactory.createEntityReference(entity.getName(), null));
    }

    protected void printCDATA(XMLEventConsumer out, FormatStack fstack, XMLEventFactory eventfactory, CDATA cdata) throws XMLStreamException {
        out.add(eventfactory.createCData(cdata.getText()));
    }

    protected void printText(XMLEventConsumer out, FormatStack fstack, XMLEventFactory eventfactory, Text text) throws XMLStreamException {
        out.add(eventfactory.createCharacters(text.getText()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void printElement(XMLEventConsumer out, FormatStack fstack, NamespaceStack nstack, XMLEventFactory eventfactory, Element element) throws XMLStreamException {
        nstack.push(element);
        try {
            Iterator<Attribute> ait;
            Namespace ns = element.getNamespace();
            Iterator<Attribute> iterator = ait = element.hasAttributes() ? element.getAttributes().iterator() : null;
            if (ns == Namespace.NO_NAMESPACE) {
                out.add(eventfactory.createStartElement("", "", element.getName(), new AttIterator(ait, eventfactory, fstack.isSpecifiedAttributesOnly()), new NSIterator(nstack.addedForward().iterator(), eventfactory)));
            } else if ("".equals(ns.getPrefix())) {
                out.add(eventfactory.createStartElement("", ns.getURI(), element.getName(), new AttIterator(ait, eventfactory, fstack.isSpecifiedAttributesOnly()), new NSIterator(nstack.addedForward().iterator(), eventfactory)));
            } else {
                out.add(eventfactory.createStartElement(ns.getPrefix(), ns.getURI(), element.getName(), new AttIterator(ait, eventfactory, fstack.isSpecifiedAttributesOnly()), new NSIterator(nstack.addedForward().iterator(), eventfactory)));
            }
            ait = null;
            List<Content> content = element.getContent();
            if (!content.isEmpty()) {
                Format.TextMode textmode = fstack.getTextMode();
                String space = element.getAttributeValue("space", Namespace.XML_NAMESPACE);
                if ("default".equals(space)) {
                    textmode = fstack.getDefaultMode();
                } else if ("preserve".equals(space)) {
                    textmode = Format.TextMode.PRESERVE;
                }
                fstack.push();
                try {
                    fstack.setTextMode(textmode);
                    Walker walker = this.buildWalker(fstack, content, false);
                    if (walker.hasNext()) {
                        String indent;
                        if (!walker.isAllText() && fstack.getPadBetween() != null) {
                            indent = fstack.getPadBetween();
                            this.printText(out, fstack, eventfactory, new Text(indent));
                        }
                        this.printContent(out, fstack, nstack, eventfactory, walker);
                        if (!walker.isAllText() && fstack.getPadLast() != null) {
                            indent = fstack.getPadLast();
                            this.printText(out, fstack, eventfactory, new Text(indent));
                        }
                    }
                }
                finally {
                    fstack.pop();
                }
            }
            out.add(eventfactory.createEndElement(element.getNamespacePrefix(), element.getNamespaceURI(), element.getName(), new NSIterator(nstack.addedReverse().iterator(), eventfactory)));
        }
        finally {
            nstack.pop();
        }
    }

    protected void printContent(XMLEventConsumer out, FormatStack fstack, NamespaceStack nstack, XMLEventFactory eventfactory, Walker walker) throws XMLStreamException {
        block9: while (walker.hasNext()) {
            Content content = walker.next();
            if (content == null) {
                if (walker.isCDATA()) {
                    this.printCDATA(out, fstack, eventfactory, new CDATA(walker.text()));
                    continue;
                }
                this.printText(out, fstack, eventfactory, new Text(walker.text()));
                continue;
            }
            switch (content.getCType()) {
                case CDATA: {
                    this.printCDATA(out, fstack, eventfactory, (CDATA)content);
                    continue block9;
                }
                case Comment: {
                    this.printComment(out, fstack, eventfactory, (Comment)content);
                    continue block9;
                }
                case Element: {
                    this.printElement(out, fstack, nstack, eventfactory, (Element)content);
                    continue block9;
                }
                case EntityRef: {
                    this.printEntityRef(out, fstack, eventfactory, (EntityRef)content);
                    continue block9;
                }
                case ProcessingInstruction: {
                    this.printProcessingInstruction(out, fstack, eventfactory, (ProcessingInstruction)content);
                    continue block9;
                }
                case Text: {
                    this.printText(out, fstack, eventfactory, (Text)content);
                    continue block9;
                }
                case DocType: {
                    this.printDocType(out, fstack, eventfactory, (DocType)content);
                    continue block9;
                }
            }
            throw new IllegalStateException("Unexpected Content " + (Object)((Object)content.getCType()));
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class AttIterator
    implements Iterator<javax.xml.stream.events.Attribute> {
        private final Iterator<Attribute> source;
        private final XMLEventFactory fac;

        public AttIterator(Iterator<Attribute> source, XMLEventFactory fac, boolean specifiedAttributesOnly) {
            this.source = specifiedAttributesOnly ? this.specified(source) : source;
            this.fac = fac;
        }

        private Iterator<Attribute> specified(Iterator<Attribute> src) {
            if (src == null) {
                return null;
            }
            ArrayList<Attribute> al = new ArrayList<Attribute>();
            while (src.hasNext()) {
                Attribute att = src.next();
                if (!att.isSpecified()) continue;
                al.add(att);
            }
            return al.isEmpty() ? null : al.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.source != null && this.source.hasNext();
        }

        @Override
        public javax.xml.stream.events.Attribute next() {
            Attribute att = this.source.next();
            Namespace ns = att.getNamespace();
            if (ns == Namespace.NO_NAMESPACE) {
                return this.fac.createAttribute(att.getName(), att.getValue());
            }
            return this.fac.createAttribute(ns.getPrefix(), ns.getURI(), att.getName(), att.getValue());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove attributes");
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class NSIterator
    implements Iterator<javax.xml.stream.events.Namespace> {
        private final Iterator<Namespace> source;
        private final XMLEventFactory fac;

        public NSIterator(Iterator<Namespace> source, XMLEventFactory fac) {
            this.source = source;
            this.fac = fac;
        }

        @Override
        public boolean hasNext() {
            return this.source.hasNext();
        }

        @Override
        public javax.xml.stream.events.Namespace next() {
            Namespace ns = this.source.next();
            return this.fac.createNamespace(ns.getPrefix(), ns.getURI());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove Namespaces");
        }
    }
}

