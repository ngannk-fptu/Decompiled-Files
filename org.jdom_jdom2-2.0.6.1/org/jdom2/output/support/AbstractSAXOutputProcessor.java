/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.AttributeType;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.support.AbstractOutputProcessor;
import org.jdom2.output.support.FormatStack;
import org.jdom2.output.support.SAXOutputProcessor;
import org.jdom2.output.support.SAXTarget;
import org.jdom2.output.support.Walker;
import org.jdom2.util.NamespaceStack;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AbstractSAXOutputProcessor
extends AbstractOutputProcessor
implements SAXOutputProcessor {
    private static void locate(SAXTarget out) {
        out.getContentHandler().setDocumentLocator(out.getLocator());
    }

    @Override
    public void process(SAXTarget out, Format format, Document doc) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            this.printDocument(out, new FormatStack(format), new NamespaceStack(), doc);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the Document: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, DocType doctype) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            this.printDocType(out, new FormatStack(format), doctype);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the DocType: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, Element element) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            this.printElement(out, new FormatStack(format), new NamespaceStack(), element);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the Element: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, List<? extends Content> list) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            FormatStack fstack = new FormatStack(format);
            Walker walker = this.buildWalker(fstack, list, false);
            this.printContent(out, fstack, new NamespaceStack(), walker);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the List: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, CDATA cdata) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            List<CDATA> list = Collections.singletonList(cdata);
            FormatStack fstack = new FormatStack(format);
            Walker walker = this.buildWalker(fstack, list, false);
            this.printContent(out, fstack, new NamespaceStack(), walker);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the CDATA: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, Text text) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            List<Text> list = Collections.singletonList(text);
            FormatStack fstack = new FormatStack(format);
            Walker walker = this.buildWalker(fstack, list, false);
            this.printContent(out, fstack, new NamespaceStack(), walker);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the Text: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, Comment comment) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            this.printComment(out, new FormatStack(format), comment);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the Comment: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, ProcessingInstruction pi) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            this.printProcessingInstruction(out, new FormatStack(format), pi);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the ProcessingInstruction: ", se);
        }
    }

    @Override
    public void process(SAXTarget out, Format format, EntityRef entity) throws JDOMException {
        try {
            AbstractSAXOutputProcessor.locate(out);
            this.printEntityRef(out, new FormatStack(format), entity);
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the EntityRef: ", se);
        }
    }

    @Override
    public void processAsDocument(SAXTarget out, Format format, List<? extends Content> nodes) throws JDOMException {
        try {
            if (nodes == null || nodes.size() == 0) {
                return;
            }
            AbstractSAXOutputProcessor.locate(out);
            out.getContentHandler().startDocument();
            FormatStack fstack = new FormatStack(format);
            if (out.isReportDTDEvents()) {
                for (Content content : nodes) {
                    if (!(content instanceof DocType)) continue;
                    this.printDocType(out, fstack, (DocType)content);
                    break;
                }
            }
            Walker walker = this.buildWalker(fstack, nodes, false);
            this.printContent(out, fstack, new NamespaceStack(), walker);
            out.getContentHandler().endDocument();
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the List: ", se);
        }
    }

    @Override
    public void processAsDocument(SAXTarget out, Format format, Element node) throws JDOMException {
        try {
            if (node == null) {
                return;
            }
            AbstractSAXOutputProcessor.locate(out);
            out.getContentHandler().startDocument();
            this.printElement(out, new FormatStack(format), new NamespaceStack(), node);
            out.getContentHandler().endDocument();
        }
        catch (SAXException se) {
            throw new JDOMException("Encountered a SAX exception processing the Element: ", se);
        }
    }

    protected void printDocument(SAXTarget out, FormatStack fstack, NamespaceStack nstack, Document document) throws SAXException {
        int sz;
        if (document == null) {
            return;
        }
        out.getContentHandler().startDocument();
        if (out.isReportDTDEvents()) {
            this.printDocType(out, fstack, document.getDocType());
        }
        if ((sz = document.getContentSize()) > 0) {
            block6: for (int i = 0; i < sz; ++i) {
                Content c = document.getContent(i);
                out.getLocator().setNode(c);
                switch (c.getCType()) {
                    case Comment: {
                        this.printComment(out, fstack, (Comment)c);
                        continue block6;
                    }
                    case DocType: {
                        continue block6;
                    }
                    case Element: {
                        this.printElement(out, fstack, nstack, (Element)c);
                        continue block6;
                    }
                    case ProcessingInstruction: {
                        this.printProcessingInstruction(out, fstack, (ProcessingInstruction)c);
                        continue block6;
                    }
                }
            }
        }
        out.getContentHandler().endDocument();
    }

    protected void printDocType(SAXTarget out, FormatStack fstack, DocType docType) throws SAXException {
        DTDHandler dtdHandler = out.getDTDHandler();
        DeclHandler declHandler = out.getDeclHandler();
        if (docType != null && (dtdHandler != null || declHandler != null)) {
            String dtdDoc = new XMLOutputter().outputString(docType);
            try {
                this.createDTDParser(out).parse(new InputSource(new StringReader(dtdDoc)));
            }
            catch (SAXParseException sAXParseException) {
            }
            catch (IOException e) {
                throw new SAXException("DTD parsing error", e);
            }
        }
    }

    protected void printProcessingInstruction(SAXTarget out, FormatStack fstack, ProcessingInstruction pi) throws SAXException {
        out.getContentHandler().processingInstruction(pi.getTarget(), pi.getData());
    }

    protected void printComment(SAXTarget out, FormatStack fstack, Comment comment) throws SAXException {
        if (out.getLexicalHandler() != null) {
            char[] c = comment.getText().toCharArray();
            out.getLexicalHandler().comment(c, 0, c.length);
        }
    }

    protected void printEntityRef(SAXTarget out, FormatStack fstack, EntityRef entity) throws SAXException {
        out.getContentHandler().skippedEntity(entity.getName());
    }

    protected void printCDATA(SAXTarget out, FormatStack fstack, CDATA cdata) throws SAXException {
        LexicalHandler lexicalHandler = out.getLexicalHandler();
        char[] chars = cdata.getText().toCharArray();
        if (lexicalHandler != null) {
            lexicalHandler.startCDATA();
            out.getContentHandler().characters(chars, 0, chars.length);
            lexicalHandler.endCDATA();
        } else {
            out.getContentHandler().characters(chars, 0, chars.length);
        }
    }

    protected void printText(SAXTarget out, FormatStack fstack, Text text) throws SAXException {
        char[] chars = text.getText().toCharArray();
        out.getContentHandler().characters(chars, 0, chars.length);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void printElement(SAXTarget out, FormatStack fstack, NamespaceStack nstack, Element element) throws SAXException {
        ContentHandler ch = out.getContentHandler();
        Object origloc = out.getLocator().getNode();
        nstack.push(element);
        try {
            out.getLocator().setNode(element);
            AttributesImpl atts = new AttributesImpl();
            for (Namespace ns : nstack.addedForward()) {
                ch.startPrefixMapping(ns.getPrefix(), ns.getURI());
                if (!out.isDeclareNamespaces()) continue;
                String prefix = ns.getPrefix();
                if (prefix.equals("")) {
                    atts.addAttribute("", "", "xmlns", "CDATA", ns.getURI());
                    continue;
                }
                atts.addAttribute("", "", "xmlns:" + ns.getPrefix(), "CDATA", ns.getURI());
            }
            if (element.hasAttributes()) {
                for (Attribute a : element.getAttributes()) {
                    if (!a.isSpecified() && fstack.isSpecifiedAttributesOnly()) continue;
                    atts.addAttribute(a.getNamespaceURI(), a.getName(), a.getQualifiedName(), AbstractSAXOutputProcessor.getAttributeTypeName(a.getAttributeType()), a.getValue());
                }
            }
            ch.startElement(element.getNamespaceURI(), element.getName(), element.getQualifiedName(), atts);
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
                            this.printText(out, fstack, new Text(indent));
                        }
                        this.printContent(out, fstack, nstack, walker);
                        if (!walker.isAllText() && fstack.getPadLast() != null) {
                            indent = fstack.getPadLast();
                            this.printText(out, fstack, new Text(indent));
                        }
                    }
                }
                finally {
                    fstack.pop();
                }
            }
            out.getContentHandler().endElement(element.getNamespaceURI(), element.getName(), element.getQualifiedName());
            for (Namespace ns : nstack.addedReverse()) {
                ch.endPrefixMapping(ns.getPrefix());
            }
        }
        finally {
            nstack.pop();
            out.getLocator().setNode(origloc);
        }
    }

    protected void printContent(SAXTarget out, FormatStack fstack, NamespaceStack nstack, Walker walker) throws SAXException {
        while (walker.hasNext()) {
            Content c = walker.next();
            if (c == null) {
                String text = walker.text();
                if (walker.isCDATA()) {
                    this.printCDATA(out, fstack, new CDATA(text));
                    continue;
                }
                this.printText(out, fstack, new Text(text));
                continue;
            }
            switch (c.getCType()) {
                case CDATA: {
                    this.printCDATA(out, fstack, (CDATA)c);
                    break;
                }
                case Comment: {
                    this.printComment(out, fstack, (Comment)c);
                    break;
                }
                case DocType: {
                    break;
                }
                case Element: {
                    this.printElement(out, fstack, nstack, (Element)c);
                    break;
                }
                case EntityRef: {
                    this.printEntityRef(out, fstack, (EntityRef)c);
                    break;
                }
                case ProcessingInstruction: {
                    this.printProcessingInstruction(out, fstack, (ProcessingInstruction)c);
                    break;
                }
                case Text: {
                    this.printText(out, fstack, (Text)c);
                }
            }
        }
    }

    private static String getAttributeTypeName(AttributeType type) {
        switch (type) {
            case UNDECLARED: {
                return "CDATA";
            }
        }
        return type.name();
    }

    protected XMLReader createParser() throws Exception {
        XMLReader parser = null;
        try {
            Class<?> factoryClass = Class.forName("javax.xml.parsers.SAXParserFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", new Class[0]);
            Object factory = newParserInstance.invoke(null, new Object[0]);
            Method newSAXParser = factoryClass.getMethod("newSAXParser", new Class[0]);
            Object jaxpParser = newSAXParser.invoke(factory, new Object[0]);
            Class<?> parserClass = jaxpParser.getClass();
            Method getXMLReader = parserClass.getMethod("getXMLReader", new Class[0]);
            parser = (XMLReader)getXMLReader.invoke(jaxpParser, new Object[0]);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (InvocationTargetException invocationTargetException) {
        }
        catch (NoSuchMethodException noSuchMethodException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        if (parser == null) {
            parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        }
        return parser;
    }

    private XMLReader createDTDParser(SAXTarget out) throws SAXException {
        XMLReader parser = null;
        try {
            parser = this.createParser();
        }
        catch (Exception ex1) {
            throw new SAXException("Error in SAX parser allocation", ex1);
        }
        if (out.getDTDHandler() != null) {
            parser.setDTDHandler(out.getDTDHandler());
        }
        if (out.getEntityResolver() != null) {
            parser.setEntityResolver(out.getEntityResolver());
        }
        if (out.getLexicalHandler() != null) {
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", out.getLexicalHandler());
            }
            catch (SAXException ex1) {
                try {
                    parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", out.getLexicalHandler());
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
            }
        }
        if (out.getDeclHandler() != null) {
            try {
                parser.setProperty("http://xml.org/sax/properties/declaration-handler", out.getDeclHandler());
            }
            catch (SAXException ex1) {
                try {
                    parser.setProperty("http://xml.org/sax/handlers/DeclHandler", out.getDeclHandler());
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
            }
        }
        parser.setErrorHandler(new DefaultHandler());
        return parser;
    }
}

