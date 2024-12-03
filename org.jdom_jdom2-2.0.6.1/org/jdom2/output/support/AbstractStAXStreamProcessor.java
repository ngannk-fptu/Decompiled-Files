/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
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
import org.jdom2.output.support.StAXStreamProcessor;
import org.jdom2.output.support.Walker;
import org.jdom2.util.NamespaceStack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractStAXStreamProcessor
extends AbstractOutputProcessor
implements StAXStreamProcessor {
    @Override
    public void process(XMLStreamWriter out, Format format, Document doc) throws XMLStreamException {
        this.printDocument(out, new FormatStack(format), new NamespaceStack(), doc);
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, DocType doctype) throws XMLStreamException {
        this.printDocType(out, new FormatStack(format), doctype);
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, Element element) throws XMLStreamException {
        this.printElement(out, new FormatStack(format), new NamespaceStack(), element);
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, List<? extends Content> list) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        Walker walker = this.buildWalker(fstack, list, false);
        this.printContent(out, fstack, new NamespaceStack(), walker);
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, CDATA cdata) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        List<CDATA> list = Collections.singletonList(cdata);
        Walker walker = this.buildWalker(fstack, list, false);
        if (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                this.printCDATA(out, fstack, new CDATA(walker.text()));
            } else if (c.getCType() == Content.CType.CDATA) {
                this.printCDATA(out, fstack, (CDATA)c);
            }
        }
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, Text text) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        List<Text> list = Collections.singletonList(text);
        Walker walker = this.buildWalker(fstack, list, false);
        if (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                this.printText(out, fstack, new Text(walker.text()));
            } else if (c.getCType() == Content.CType.Text) {
                this.printText(out, fstack, (Text)c);
            }
        }
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, Comment comment) throws XMLStreamException {
        this.printComment(out, new FormatStack(format), comment);
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, ProcessingInstruction pi) throws XMLStreamException {
        FormatStack fstack = new FormatStack(format);
        fstack.setIgnoreTrAXEscapingPIs(true);
        this.printProcessingInstruction(out, fstack, pi);
        out.flush();
    }

    @Override
    public void process(XMLStreamWriter out, Format format, EntityRef entity) throws XMLStreamException {
        this.printEntityRef(out, new FormatStack(format), entity);
        out.flush();
    }

    protected void printDocument(XMLStreamWriter out, FormatStack fstack, NamespaceStack nstack, Document doc) throws XMLStreamException {
        Walker walker;
        ArrayList<Content> list;
        if (fstack.isOmitDeclaration()) {
            out.writeStartDocument();
            if (fstack.getLineSeparator() != null) {
                out.writeCharacters(fstack.getLineSeparator());
            }
        } else if (fstack.isOmitEncoding()) {
            out.writeStartDocument("1.0");
            if (fstack.getLineSeparator() != null) {
                out.writeCharacters(fstack.getLineSeparator());
            }
        } else {
            out.writeStartDocument(fstack.getEncoding(), "1.0");
            if (fstack.getLineSeparator() != null) {
                out.writeCharacters(fstack.getLineSeparator());
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
                String padding;
                Content c = walker.next();
                if (c == null) {
                    padding = walker.text();
                    if (padding == null || !Verifier.isAllXMLWhitespace(padding) || walker.isCDATA()) continue;
                    out.writeCharacters(padding);
                    continue;
                }
                switch (c.getCType()) {
                    case Comment: {
                        this.printComment(out, fstack, (Comment)c);
                        break;
                    }
                    case DocType: {
                        this.printDocType(out, fstack, (DocType)c);
                        break;
                    }
                    case Element: {
                        this.printElement(out, fstack, nstack, (Element)c);
                        break;
                    }
                    case ProcessingInstruction: {
                        this.printProcessingInstruction(out, fstack, (ProcessingInstruction)c);
                        break;
                    }
                    case Text: {
                        padding = ((Text)c).getText();
                        if (padding == null || !Verifier.isAllXMLWhitespace(padding)) break;
                        out.writeCharacters(padding);
                    }
                }
            }
            if (fstack.getLineSeparator() != null) {
                out.writeCharacters(fstack.getLineSeparator());
            }
        }
        out.writeEndDocument();
    }

    protected void printDocType(XMLStreamWriter out, FormatStack fstack, DocType docType) throws XMLStreamException {
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
        out.writeDTD(sw.toString());
    }

    protected void printProcessingInstruction(XMLStreamWriter out, FormatStack fstack, ProcessingInstruction pi) throws XMLStreamException {
        String target = pi.getTarget();
        String rawData = pi.getData();
        if (rawData != null && rawData.trim().length() > 0) {
            out.writeProcessingInstruction(target, rawData);
        } else {
            out.writeProcessingInstruction(target);
        }
    }

    protected void printComment(XMLStreamWriter out, FormatStack fstack, Comment comment) throws XMLStreamException {
        out.writeComment(comment.getText());
    }

    protected void printEntityRef(XMLStreamWriter out, FormatStack fstack, EntityRef entity) throws XMLStreamException {
        out.writeEntityRef(entity.getName());
    }

    protected void printCDATA(XMLStreamWriter out, FormatStack fstack, CDATA cdata) throws XMLStreamException {
        out.writeCData(cdata.getText());
    }

    protected void printText(XMLStreamWriter out, FormatStack fstack, Text text) throws XMLStreamException {
        out.writeCharacters(text.getText());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void printElement(XMLStreamWriter out, FormatStack fstack, NamespaceStack nstack, Element element) throws XMLStreamException {
        block29: {
            nstack.push(element);
            try {
                for (Namespace nsa : nstack.addedForward()) {
                    if ("".equals(nsa.getPrefix())) {
                        out.setDefaultNamespace(nsa.getURI());
                        continue;
                    }
                    out.setPrefix(nsa.getPrefix(), nsa.getURI());
                }
                List<Content> content = element.getContent();
                Format.TextMode textmode = fstack.getTextMode();
                Walker walker = null;
                if (!content.isEmpty()) {
                    String space = element.getAttributeValue("space", Namespace.XML_NAMESPACE);
                    if ("default".equals(space)) {
                        textmode = fstack.getDefaultMode();
                    } else if ("preserve".equals(space)) {
                        textmode = Format.TextMode.PRESERVE;
                    }
                    fstack.push();
                    try {
                        fstack.setTextMode(textmode);
                        walker = this.buildWalker(fstack, content, false);
                        if (!walker.hasNext()) {
                            walker = null;
                        }
                    }
                    finally {
                        fstack.pop();
                    }
                }
                boolean expandit = walker != null || fstack.isExpandEmptyElements();
                Namespace ns = element.getNamespace();
                if (expandit) {
                    out.writeStartElement(ns.getPrefix(), element.getName(), ns.getURI());
                    for (Namespace nsd : nstack.addedForward()) {
                        this.printNamespace(out, fstack, nsd);
                    }
                    if (element.hasAttributes()) {
                        for (Attribute attribute : element.getAttributes()) {
                            this.printAttribute(out, fstack, attribute);
                        }
                    }
                    out.writeCharacters("");
                    if (walker != null) {
                        fstack.push();
                        try {
                            String indent;
                            fstack.setTextMode(textmode);
                            if (!walker.isAllText() && fstack.getPadBetween() != null) {
                                indent = fstack.getPadBetween();
                                this.printText(out, fstack, new Text(indent));
                            }
                            this.printContent(out, fstack, nstack, walker);
                            if (!walker.isAllText() && fstack.getPadLast() != null) {
                                indent = fstack.getPadLast();
                                this.printText(out, fstack, new Text(indent));
                            }
                        }
                        finally {
                            fstack.pop();
                        }
                    }
                    out.writeEndElement();
                    break block29;
                }
                out.writeEmptyElement(ns.getPrefix(), element.getName(), ns.getURI());
                for (Namespace nsd : nstack.addedForward()) {
                    this.printNamespace(out, fstack, nsd);
                }
                for (Attribute attribute : element.getAttributes()) {
                    this.printAttribute(out, fstack, attribute);
                }
                out.writeCharacters("");
            }
            finally {
                for (Namespace nsr : nstack.addedForward()) {
                    Namespace nsa = nstack.getRebound(nsr.getPrefix());
                    if (nsa == null) continue;
                    if ("".equals(nsa.getPrefix())) {
                        out.setDefaultNamespace(nsa.getURI());
                        continue;
                    }
                    out.setPrefix(nsa.getPrefix(), nsa.getURI());
                }
                nstack.pop();
            }
        }
    }

    protected void printContent(XMLStreamWriter out, FormatStack fstack, NamespaceStack nstack, Walker walker) throws XMLStreamException {
        block9: while (walker.hasNext()) {
            Content content = walker.next();
            if (content == null) {
                if (walker.isCDATA()) {
                    this.printCDATA(out, fstack, new CDATA(walker.text()));
                    continue;
                }
                this.printText(out, fstack, new Text(walker.text()));
                continue;
            }
            switch (content.getCType()) {
                case CDATA: {
                    this.printCDATA(out, fstack, (CDATA)content);
                    continue block9;
                }
                case Comment: {
                    this.printComment(out, fstack, (Comment)content);
                    continue block9;
                }
                case Element: {
                    this.printElement(out, fstack, nstack, (Element)content);
                    continue block9;
                }
                case EntityRef: {
                    this.printEntityRef(out, fstack, (EntityRef)content);
                    continue block9;
                }
                case ProcessingInstruction: {
                    this.printProcessingInstruction(out, fstack, (ProcessingInstruction)content);
                    continue block9;
                }
                case Text: {
                    this.printText(out, fstack, (Text)content);
                    continue block9;
                }
                case DocType: {
                    this.printDocType(out, fstack, (DocType)content);
                    continue block9;
                }
            }
            throw new IllegalStateException("Unexpected Content " + (Object)((Object)content.getCType()));
        }
    }

    protected void printNamespace(XMLStreamWriter out, FormatStack fstack, Namespace ns) throws XMLStreamException {
        String prefix = ns.getPrefix();
        String uri = ns.getURI();
        out.writeNamespace(prefix, uri);
    }

    protected void printAttribute(XMLStreamWriter out, FormatStack fstack, Attribute attribute) throws XMLStreamException {
        if (!attribute.isSpecified() && fstack.isSpecifiedAttributesOnly()) {
            return;
        }
        Namespace ns = attribute.getNamespace();
        if (ns == Namespace.NO_NAMESPACE) {
            out.writeAttribute(attribute.getName(), attribute.getValue());
        } else {
            out.writeAttribute(ns.getPrefix(), ns.getURI(), attribute.getName(), attribute.getValue());
        }
    }
}

