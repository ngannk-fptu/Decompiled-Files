/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import javanet.staxutils.XMLStreamUtils;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public final class XMLWriterUtils {
    private XMLWriterUtils() {
    }

    public static final void writeQuotedValue(String value, Writer writer) throws IOException {
        int quoteChar = value.indexOf(34) < 0 ? 34 : 39;
        writer.write(quoteChar);
        writer.write(value);
        writer.write(quoteChar);
    }

    public static final void writeEncodedQuotedValue(String value, Writer writer) throws IOException {
        char quoteChar = value.indexOf(34) < 0 ? (char)'\"' : '\'';
        writer.write(quoteChar);
        XMLWriterUtils.writeEncodedValue(value, quoteChar, writer);
        writer.write(quoteChar);
    }

    public static final void writeEncodedValue(String value, char quoteChar, Writer writer) throws IOException {
        int s = value.length();
        for (int i = 0; i < s; ++i) {
            char c = value.charAt(i);
            if (c == '\'') {
                writer.write(quoteChar == '\'' ? "&apos;" : "'");
                continue;
            }
            if (c == '\"') {
                writer.write(quoteChar == '\"' ? "&quot;" : "\"");
                continue;
            }
            if (c == '\n') {
                writer.write("&#xA;");
                continue;
            }
            XMLWriterUtils.writeEncodedCharacter(c, writer);
        }
    }

    public static final void writeEncodedText(CharSequence text, Writer writer) throws IOException {
        int s = text.length();
        for (int i = 0; i < s; ++i) {
            XMLWriterUtils.writeEncodedCharacter(text.charAt(i), writer);
        }
    }

    public static final void writeEncodedText(char[] text, int start, int len, Writer writer) throws IOException {
        int s = start + len;
        for (int i = start; i < s; ++i) {
            XMLWriterUtils.writeEncodedCharacter(text[i], writer);
        }
    }

    public static final void writeEncodedCharacter(char c, Writer writer) throws IOException {
        if (c == '&') {
            writer.write("&amp;");
        } else if (c == '<') {
            writer.write("&lt;");
        } else if (c == '>') {
            writer.write("&gt;");
        } else if (c == '\r') {
            writer.write("&#xD;");
        } else {
            writer.write(c);
        }
    }

    public static final void writeQName(QName name, Writer writer) throws IOException {
        String prefix = name.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(58);
        }
        writer.write(name.getLocalPart());
    }

    public static final void writeQName(String prefix, String localPart, Writer writer) throws IOException {
        if (prefix != null && prefix.length() > 0) {
            writer.write(prefix);
            writer.write(58);
        }
        writer.write(localPart);
    }

    public static final void writeEvent(XMLEvent event, Writer writer) throws IOException, XMLStreamException {
        int eventType = event.getEventType();
        switch (eventType) {
            case 1: {
                XMLWriterUtils.writeStartElement(event.asStartElement(), false, writer);
                break;
            }
            case 2: {
                XMLWriterUtils.writeEndElement(event.asEndElement(), writer);
                break;
            }
            case 4: 
            case 6: 
            case 12: {
                XMLWriterUtils.writeCharacters(event.asCharacters(), writer);
                break;
            }
            case 5: {
                XMLWriterUtils.writeComment((Comment)event, writer);
                break;
            }
            case 9: {
                XMLWriterUtils.writeEntityReference((EntityReference)event, writer);
                break;
            }
            case 3: {
                XMLWriterUtils.writeProcessingInstruction((ProcessingInstruction)event, writer);
                break;
            }
            case 11: {
                XMLWriterUtils.writeDTD((DTD)event, writer);
                break;
            }
            case 7: {
                XMLWriterUtils.writeStartDocument((StartDocument)event, writer);
                break;
            }
            case 8: {
                XMLWriterUtils.writeEndDocument((EndDocument)event, writer);
                break;
            }
            case 13: {
                XMLWriterUtils.writeNamespace((Namespace)event, writer);
                break;
            }
            case 10: {
                XMLWriterUtils.writeAttribute((Attribute)event, writer);
                break;
            }
            case 15: {
                XMLWriterUtils.writeEntityDeclaration((EntityDeclaration)event, writer);
                break;
            }
            case 14: {
                XMLWriterUtils.writeNotationDeclaration((NotationDeclaration)event, writer);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unrecognized event (" + XMLStreamUtils.getEventTypeName(eventType) + "): " + event);
            }
        }
    }

    public static final void writeStartDocument(StartDocument start, Writer writer) throws IOException {
        String version = start.getVersion();
        String encoding = start.getCharacterEncodingScheme();
        if (start.standaloneSet()) {
            XMLWriterUtils.writeStartDocument(version, encoding, start.isStandalone(), writer);
        } else {
            XMLWriterUtils.writeStartDocument(version, encoding, writer);
        }
    }

    public static final void writeStartDocument(Writer writer) throws IOException {
        XMLWriterUtils.writeStartDocument("1.0", null, null, writer);
    }

    public static final void writeStartDocument(String version, Writer writer) throws IOException {
        XMLWriterUtils.writeStartDocument(version, null, null, writer);
    }

    public static final void writeStartDocument(String version, String encoding, Writer writer) throws IOException {
        XMLWriterUtils.writeStartDocument(version, encoding, null, writer);
    }

    public static final void writeStartDocument(String version, String encoding, boolean standalone, Writer writer) throws IOException {
        XMLWriterUtils.writeStartDocument(version, encoding, standalone ? "yes" : "no", writer);
    }

    public static final void writeStartDocument(String version, String encoding, String standalone, Writer writer) throws IOException {
        writer.write("<?xml version=");
        XMLWriterUtils.writeQuotedValue(version, writer);
        if (encoding != null) {
            writer.write(" encoding=");
            XMLWriterUtils.writeQuotedValue(encoding, writer);
        }
        if (standalone != null) {
            writer.write(" standalone=");
            XMLWriterUtils.writeQuotedValue(standalone, writer);
        }
        writer.write("?>");
    }

    public static final void writeEndDocument(EndDocument end, Writer writer) throws IOException {
        XMLWriterUtils.writeEndDocument(writer);
    }

    public static final void writeEndDocument(Writer writer) throws IOException {
    }

    public static final void writeStartElement(StartElement start, Writer writer) throws IOException, XMLStreamException {
        XMLWriterUtils.writeStartElement(start.getName(), start.getAttributes(), start.getNamespaces(), false, writer);
    }

    public static final void writeStartElement(StartElement start, boolean empty, Writer writer) throws IOException, XMLStreamException {
        XMLWriterUtils.writeStartElement(start.getName(), start.getAttributes(), start.getNamespaces(), empty, writer);
    }

    public static final void writeStartElement(QName name, Iterator attributes, Iterator namespaces, Writer writer) throws IOException, XMLStreamException {
        XMLWriterUtils.writeStartElement(name, attributes, namespaces, false, writer);
    }

    public static final void writeStartElement(QName name, Iterator attributes, Iterator namespaces, boolean empty, Writer writer) throws IOException, XMLStreamException {
        writer.write(60);
        XMLWriterUtils.writeQName(name, writer);
        if (namespaces != null) {
            while (namespaces.hasNext()) {
                Namespace ns = (Namespace)namespaces.next();
                writer.write(32);
                ns.writeAsEncodedUnicode(writer);
            }
        }
        if (attributes != null) {
            while (attributes.hasNext()) {
                Attribute attr = (Attribute)attributes.next();
                writer.write(32);
                attr.writeAsEncodedUnicode(writer);
            }
        }
        if (empty) {
            writer.write("/>");
        } else {
            writer.write(62);
        }
    }

    public static final void writeStartElement(QName name, Map attributes, Map namespaces, boolean empty, Writer writer) throws IOException {
        Map.Entry entry;
        Iterator i;
        writer.write(60);
        XMLWriterUtils.writeQName(name, writer);
        if (namespaces != null) {
            i = namespaces.entrySet().iterator();
            while (i.hasNext()) {
                entry = i.next();
                writer.write(32);
                XMLWriterUtils.writeNamespace((String)entry.getKey(), (String)entry.getValue(), writer);
            }
        }
        if (attributes != null) {
            i = attributes.entrySet().iterator();
            while (i.hasNext()) {
                entry = i.next();
                writer.write(32);
                XMLWriterUtils.writeAttribute((QName)entry.getKey(), (String)entry.getValue(), writer);
            }
        }
        if (empty) {
            writer.write("/>");
        } else {
            writer.write(62);
        }
    }

    public static final void writeAttribute(Attribute attr, Writer writer) throws IOException {
        QName name = attr.getName();
        String value = attr.getValue();
        XMLWriterUtils.writeAttribute(name, value, writer);
    }

    public static final void writeAttribute(QName name, String value, Writer writer) throws IOException {
        XMLWriterUtils.writeQName(name, writer);
        writer.write(61);
        XMLWriterUtils.writeEncodedQuotedValue(value, writer);
    }

    public static final void writeNamespace(Namespace ns, Writer writer) throws IOException {
        String prefix = ns.getPrefix();
        String uri = ns.getNamespaceURI();
        XMLWriterUtils.writeNamespace(prefix, uri, writer);
    }

    public static final void writeNamespace(String prefix, String uri, Writer writer) throws IOException {
        writer.write("xmlns");
        if (prefix != null && prefix.length() > 0) {
            writer.write(58);
            writer.write(prefix);
        }
        writer.write(61);
        XMLWriterUtils.writeEncodedQuotedValue(uri, writer);
    }

    public static final void writeEndElement(EndElement end, Writer writer) throws IOException {
        XMLWriterUtils.writeEndElement(end.getName(), writer);
    }

    public static final void writeEndElement(QName name, Writer writer) throws IOException {
        writer.write("</");
        XMLWriterUtils.writeQName(name, writer);
        writer.write(62);
    }

    public static final void writeCharacters(Characters chars, Writer writer) throws IOException {
        if (chars.isCData()) {
            XMLWriterUtils.writeCData(chars.getData(), writer);
        } else {
            XMLWriterUtils.writeCharacters(chars.getData(), writer);
        }
    }

    public static final void writeCharacters(CharSequence text, Writer writer) throws IOException {
        XMLWriterUtils.writeEncodedText(text, writer);
    }

    public static final void writeCharacters(char[] data, int start, int length, Writer writer) throws IOException {
        XMLWriterUtils.writeEncodedText(data, start, length, writer);
    }

    public static final void writeCData(String text, Writer writer) throws IOException {
        writer.write("<![CDATA[");
        writer.write(text);
        writer.write("]]>");
    }

    public static final void writeCData(char[] data, int start, int length, Writer writer) throws IOException {
        writer.write("<![CDATA[");
        writer.write(data, start, length);
        writer.write("]]>");
    }

    public static final void writeComment(Comment comment, Writer writer) throws IOException {
        XMLWriterUtils.writeComment(comment.getText(), writer);
    }

    public static final void writeComment(String comment, Writer writer) throws IOException {
        writer.write("<!--");
        writer.write(comment);
        writer.write("-->");
    }

    public static final void writeEntityReference(EntityReference entityRef, Writer writer) throws IOException {
        XMLWriterUtils.writeEntityReference(entityRef.getName(), writer);
    }

    public static final void writeEntityReference(String entityRef, Writer writer) throws IOException {
        writer.write(38);
        writer.write(entityRef);
        writer.write(59);
    }

    public static final void writeEntityDeclaration(EntityDeclaration declaration, Writer writer) throws IOException {
        String name = declaration.getName();
        String notation = declaration.getNotationName();
        String text = declaration.getReplacementText();
        if (text != null) {
            XMLWriterUtils.writeEntityDeclaration(name, text, notation, writer);
        } else {
            String publicId = declaration.getPublicId();
            String systemId = declaration.getSystemId();
            XMLWriterUtils.writeEntityDeclaration(name, publicId, systemId, notation, writer);
        }
    }

    public static final void writeEntityDeclaration(String name, String publicId, String systemId, String notation, Writer writer) throws IOException {
        writer.write("<!ENTITY ");
        writer.write(name);
        if (publicId != null) {
            writer.write("PUBLIC ");
            XMLWriterUtils.writeQuotedValue(publicId, writer);
            if (systemId != null) {
                writer.write(" ");
                XMLWriterUtils.writeQuotedValue(systemId, writer);
            }
        } else {
            writer.write("SYSTEM ");
            XMLWriterUtils.writeQuotedValue(systemId, writer);
        }
        if (notation != null) {
            writer.write(" NDATA");
            writer.write(notation);
        }
        writer.write(">");
    }

    public static final void writeEntityDeclaration(String name, String text, String notation, Writer writer) throws IOException {
        writer.write("<!ENTITY ");
        writer.write(name);
        XMLWriterUtils.writeEncodedQuotedValue(text, writer);
        if (notation != null) {
            writer.write(" NDATA");
            writer.write(notation);
        }
        writer.write(">");
    }

    public static final void writeNotationDeclaration(NotationDeclaration declaration, Writer writer) throws IOException {
        String name = declaration.getName();
        String publicId = declaration.getPublicId();
        String systemId = declaration.getSystemId();
        XMLWriterUtils.writeNotationDeclaration(name, publicId, systemId, writer);
    }

    public static final void writeNotationDeclaration(String name, String publicId, String systemId, Writer writer) throws IOException {
        writer.write("<!NOTATION ");
        writer.write(name);
        if (publicId != null) {
            writer.write("PUBLIC ");
            XMLWriterUtils.writeQuotedValue(publicId, writer);
            if (systemId != null) {
                writer.write(" ");
                XMLWriterUtils.writeQuotedValue(systemId, writer);
            }
        } else {
            writer.write("SYSTEM ");
            XMLWriterUtils.writeQuotedValue(systemId, writer);
        }
        writer.write(">");
    }

    public static final void writeProcessingInstruction(ProcessingInstruction procInst, Writer writer) throws IOException {
        XMLWriterUtils.writeProcessingInstruction(procInst.getTarget(), procInst.getData(), writer);
    }

    public static final void writeProcessingInstruction(String target, String data, Writer writer) throws IOException {
        writer.write("<?");
        writer.write(target);
        if (data != null) {
            writer.write(32);
            writer.write(data);
        }
        writer.write("?>");
    }

    public static final void writeDTD(DTD dtd, Writer writer) throws IOException {
        XMLWriterUtils.writeDTD(dtd, writer);
    }

    public static final void writeDTD(String dtd, Writer writer) throws IOException {
        writer.write(dtd);
    }

    public static final void writeEvent(XMLEvent event, XMLStreamWriter writer) throws XMLStreamException {
        int eventType = event.getEventType();
        switch (eventType) {
            case 1: {
                XMLWriterUtils.writeStartElement(event.asStartElement(), false, writer);
                break;
            }
            case 2: {
                XMLWriterUtils.writeEndElement(event.asEndElement(), writer);
                break;
            }
            case 4: 
            case 6: 
            case 12: {
                XMLWriterUtils.writeCharacters(event.asCharacters(), writer);
                break;
            }
            case 5: {
                XMLWriterUtils.writeComment((Comment)event, writer);
                break;
            }
            case 9: {
                XMLWriterUtils.writeEntityReference((EntityReference)event, writer);
                break;
            }
            case 3: {
                XMLWriterUtils.writeProcessingInstruction((ProcessingInstruction)event, writer);
                break;
            }
            case 11: {
                XMLWriterUtils.writeDTD((DTD)event, writer);
                break;
            }
            case 7: {
                XMLWriterUtils.writeStartDocument((StartDocument)event, writer);
                break;
            }
            case 8: {
                XMLWriterUtils.writeEndDocument((EndDocument)event, writer);
                break;
            }
            case 13: {
                XMLWriterUtils.writeNamespace((Namespace)event, writer);
                break;
            }
            case 10: {
                XMLWriterUtils.writeAttribute((Attribute)event, writer);
                break;
            }
            default: {
                throw new XMLStreamException("Unrecognized event (" + XMLStreamUtils.getEventTypeName(eventType) + "): " + event);
            }
        }
    }

    public static final void writeStartElement(StartElement start, boolean empty, XMLStreamWriter writer) throws XMLStreamException {
        QName name = start.getName();
        String nsURI = name.getNamespaceURI();
        String localName = name.getLocalPart();
        String prefix = name.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            if (empty) {
                writer.writeEmptyElement(prefix, localName, nsURI);
            } else {
                writer.writeStartElement(prefix, localName, nsURI);
            }
        } else if (nsURI != null && nsURI.length() > 0) {
            if (empty) {
                writer.writeEmptyElement(nsURI, localName);
            } else {
                writer.writeStartElement(nsURI, localName);
            }
        } else if (empty) {
            writer.writeEmptyElement(localName);
        } else {
            writer.writeStartElement(localName);
        }
        Iterator<Namespace> nsIter = start.getNamespaces();
        while (nsIter.hasNext()) {
            Namespace ns = nsIter.next();
            XMLWriterUtils.writeNamespace(ns, writer);
        }
        Iterator<Attribute> attrIter = start.getAttributes();
        while (attrIter.hasNext()) {
            Attribute attr = attrIter.next();
            XMLWriterUtils.writeAttribute(attr, writer);
        }
    }

    public static final void writeEndElement(EndElement end, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    public static final void writeAttribute(Attribute attr, XMLStreamWriter writer) throws XMLStreamException {
        QName name = attr.getName();
        String nsURI = name.getNamespaceURI();
        String localName = name.getLocalPart();
        String prefix = name.getPrefix();
        String value = attr.getValue();
        if (prefix != null) {
            writer.writeAttribute(prefix, nsURI, localName, value);
        } else if (nsURI != null) {
            writer.writeAttribute(nsURI, localName, value);
        } else {
            writer.writeAttribute(localName, value);
        }
    }

    public static final void writeNamespace(Namespace ns, XMLStreamWriter writer) throws XMLStreamException {
        if (ns.isDefaultNamespaceDeclaration()) {
            writer.writeDefaultNamespace(ns.getNamespaceURI());
        } else {
            writer.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
        }
    }

    public static final void writeStartDocument(StartDocument start, XMLStreamWriter writer) throws XMLStreamException {
        String version = start.getVersion();
        if (start.encodingSet()) {
            String encoding = start.getCharacterEncodingScheme();
            writer.writeStartDocument(encoding, version);
        } else {
            writer.writeStartDocument(version);
        }
    }

    public static final void writeEndDocument(EndDocument end, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndDocument();
    }

    public static final void writeCharacters(Characters chars, XMLStreamWriter writer) throws XMLStreamException {
        if (chars.isCData()) {
            writer.writeCData(chars.getData());
        } else {
            writer.writeCharacters(chars.getData());
        }
    }

    public static final void writeComment(Comment comment, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeComment(comment.getText());
    }

    public static final void writeEntityReference(EntityReference entityRef, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEntityRef(entityRef.getName());
    }

    public static final void writeProcessingInstruction(ProcessingInstruction procInst, XMLStreamWriter writer) throws XMLStreamException {
        String data = procInst.getData();
        if (data != null) {
            writer.writeProcessingInstruction(procInst.getTarget(), data);
        } else {
            writer.writeProcessingInstruction(procInst.getTarget());
        }
    }

    public static final void writeDTD(DTD dtd, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeDTD(dtd.getDocumentTypeDeclaration());
    }
}

