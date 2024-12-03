/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;
import org.apache.abdera.util.AbstractNamedWriter;
import org.apache.abdera.util.AbstractWriterOptions;
import org.apache.abdera.writer.NamedWriter;
import org.apache.abdera.writer.WriterOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.StAXUtils;

public class PrettyWriter
extends AbstractNamedWriter
implements NamedWriter {
    private static final String[] FORMATS = new String[]{"application/atom+xml", "application/atomserv+xml", "application/xml"};

    public PrettyWriter() {
        super("PrettyXML", FORMATS);
    }

    public Object write(Base base, WriterOptions options) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.writeTo(base, out, options);
        return out.toString();
    }

    public void writeTo(Base base, OutputStream out, WriterOptions options) throws IOException {
        out = this.getCompressedOutputStream(out, options);
        String charset = options.getCharset() != null ? options.getCharset() : "UTF-8";
        this.writeTo(base, new OutputStreamWriter(out, charset), options);
        this.finishCompressedOutputStream(out, options);
        if (options.getAutoClose()) {
            out.close();
        }
    }

    public void writeTo(Base base, Writer out, WriterOptions options) throws IOException {
        try {
            XMLStreamWriter w = StAXUtils.createXMLStreamWriter(out);
            PrettyStreamWriter pw = new PrettyStreamWriter(w);
            OMElement om = base instanceof Document ? this.getOMElement((Element)((Document)base).getRoot()) : (OMElement)((Object)base);
            String charset = options.getCharset();
            if (om.getParent() != null && om.getParent() instanceof OMDocument) {
                OMDocument doc = (OMDocument)om.getParent();
                pw.writeStartDocument(charset != null ? charset : doc.getCharsetEncoding(), doc.getXMLVersion());
            }
            om.serialize(pw);
            pw.writeEndDocument();
            if (options.getAutoClose()) {
                out.close();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private OMElement getOMElement(Element el) {
        if (el instanceof ElementWrapper) {
            return this.getOMElement(((ElementWrapper)el).getInternal());
        }
        return (OMElement)((Object)el);
    }

    protected WriterOptions initDefaultWriterOptions() {
        return new AbstractWriterOptions(){};
    }

    private static class PrettyStreamWriter
    implements XMLStreamWriter {
        private static final int INDENT = 2;
        private XMLStreamWriter internal = null;
        private int depth = 0;
        private boolean prev_was_end_element = false;

        public PrettyStreamWriter(XMLStreamWriter writer) {
            this.internal = writer;
        }

        public void close() throws XMLStreamException {
            this.internal.close();
        }

        public void flush() throws XMLStreamException {
            this.internal.flush();
        }

        public NamespaceContext getNamespaceContext() {
            return this.internal.getNamespaceContext();
        }

        public String getPrefix(String arg0) throws XMLStreamException {
            return this.internal.getPrefix(arg0);
        }

        public Object getProperty(String arg0) throws IllegalArgumentException {
            return this.internal.getProperty(arg0);
        }

        public void setDefaultNamespace(String arg0) throws XMLStreamException {
            this.internal.setDefaultNamespace(arg0);
        }

        public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
            this.internal.setNamespaceContext(arg0);
        }

        public void setPrefix(String arg0, String arg1) throws XMLStreamException {
            this.internal.setPrefix(arg0, arg1);
        }

        public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
            this.internal.writeAttribute(arg0, arg1);
            this.prev_was_end_element = false;
        }

        public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
            this.internal.writeAttribute(arg0, arg1, arg2);
            this.prev_was_end_element = false;
        }

        public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
            this.internal.writeAttribute(arg0, arg1, arg2, arg3);
            this.prev_was_end_element = false;
        }

        public void writeCData(String arg0) throws XMLStreamException {
            this.internal.writeCData(arg0);
            this.prev_was_end_element = false;
        }

        public void writeCharacters(String arg0) throws XMLStreamException {
            this.internal.writeCharacters(arg0);
            this.prev_was_end_element = false;
        }

        public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
            this.internal.writeCharacters(arg0, arg1, arg2);
            this.prev_was_end_element = false;
        }

        public void writeComment(String arg0) throws XMLStreamException {
            this.writeIndent();
            this.internal.writeComment(arg0);
            this.prev_was_end_element = true;
        }

        public void writeDTD(String arg0) throws XMLStreamException {
            this.internal.writeDTD(arg0);
            this.prev_was_end_element = true;
        }

        public void writeDefaultNamespace(String arg0) throws XMLStreamException {
            this.internal.writeDefaultNamespace(arg0);
            this.prev_was_end_element = false;
        }

        public void writeEmptyElement(String arg0) throws XMLStreamException {
            this.writeIndent();
            this.internal.writeEmptyElement(arg0);
            this.prev_was_end_element = true;
        }

        public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
            this.writeIndent();
            this.internal.writeEmptyElement(arg0, arg1);
            this.prev_was_end_element = true;
        }

        public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            this.writeIndent();
            this.internal.writeEmptyElement(arg0, arg1, arg2);
            this.prev_was_end_element = true;
        }

        public void writeEndDocument() throws XMLStreamException {
            this.internal.writeEndDocument();
            this.prev_was_end_element = false;
        }

        public void writeEndElement() throws XMLStreamException {
            --this.depth;
            if (this.prev_was_end_element) {
                this.writeIndent();
            }
            this.internal.writeEndElement();
            this.prev_was_end_element = true;
        }

        public void writeEntityRef(String arg0) throws XMLStreamException {
            this.internal.writeEntityRef(arg0);
            this.prev_was_end_element = false;
        }

        public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
            this.internal.writeNamespace(arg0, arg1);
            this.prev_was_end_element = false;
        }

        public void writeProcessingInstruction(String arg0) throws XMLStreamException {
            this.writeIndent();
            this.internal.writeProcessingInstruction(arg0);
            this.prev_was_end_element = true;
        }

        public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
            this.writeIndent();
            this.internal.writeProcessingInstruction(arg0, arg1);
            this.prev_was_end_element = true;
        }

        public void writeStartDocument() throws XMLStreamException {
            this.internal.writeStartDocument();
            this.prev_was_end_element = false;
        }

        public void writeStartDocument(String arg0) throws XMLStreamException {
            this.internal.writeStartDocument(arg0);
            this.prev_was_end_element = false;
        }

        public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
            this.internal.writeStartDocument(arg0, arg1);
            this.prev_was_end_element = false;
        }

        public void writeStartElement(String arg0) throws XMLStreamException {
            this.writeIndent();
            ++this.depth;
            this.internal.writeStartElement(arg0);
            this.prev_was_end_element = false;
        }

        public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
            this.writeIndent();
            ++this.depth;
            this.internal.writeStartElement(arg0, arg1);
            this.prev_was_end_element = false;
        }

        public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            this.writeIndent();
            ++this.depth;
            this.internal.writeStartElement(arg0, arg1, arg2);
            this.prev_was_end_element = false;
        }

        private void writeIndent() throws XMLStreamException {
            this.internal.writeCharacters("\n");
            char[] spaces = this.getSpaces();
            this.internal.writeCharacters(spaces, 0, spaces.length);
        }

        private char[] getSpaces() {
            char[] spaces = new char[2 * this.depth];
            Arrays.fill(spaces, ' ');
            return spaces;
        }
    }
}

