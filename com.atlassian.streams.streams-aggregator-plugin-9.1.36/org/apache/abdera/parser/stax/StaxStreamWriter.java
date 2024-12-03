/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser.stax;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.abdera.Abdera;
import org.apache.abdera.parser.stax.util.FOMHelper;
import org.apache.abdera.util.AbstractStreamWriter;
import org.apache.abdera.writer.StreamWriter;
import org.apache.axiom.om.util.StAXUtils;
import org.apache.axiom.om.util.StAXWriterConfiguration;
import org.apache.axiom.util.stax.dialect.StAXDialect;

public class StaxStreamWriter
extends AbstractStreamWriter {
    private static final StAXWriterConfiguration ABDERA_WRITER_CONFIGURATION = new StAXWriterConfiguration(){

        public XMLOutputFactory configure(XMLOutputFactory factory, StAXDialect dialect) {
            factory.setProperty("javax.xml.stream.isRepairingNamespaces", true);
            return factory;
        }

        public String toString() {
            return "ABDERA";
        }
    };
    private static final String NAME = "default";
    private XMLStreamWriter writer;
    private int depth = 0;
    private int textwritten = 0;
    private final Stack<Map<String, String>> namespaces = new Stack();

    public StaxStreamWriter(Abdera abdera) {
        super(abdera, NAME);
    }

    public StaxStreamWriter(Abdera abdera, Writer writer) {
        super(abdera, NAME);
        this.setWriter(writer);
    }

    public StaxStreamWriter(Abdera abdera, OutputStream out) {
        super(abdera, NAME);
        this.setOutputStream(out);
    }

    public StaxStreamWriter(Abdera abdera, OutputStream out, String charset) {
        super(abdera, NAME);
        this.setOutputStream(out, charset);
    }

    public StreamWriter setWriter(Writer writer) {
        try {
            this.writer = StaxStreamWriter.createXMLStreamWriter(writer);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private static XMLStreamWriter createXMLStreamWriter(Writer out) throws XMLStreamException {
        XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(ABDERA_WRITER_CONFIGURATION);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out);
        return writer;
    }

    public StreamWriter setOutputStream(OutputStream out) {
        try {
            this.writer = StaxStreamWriter.createXMLStreamWriter(out, "UTF-8");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private static XMLStreamWriter createXMLStreamWriter(OutputStream out, String encoding) throws XMLStreamException {
        XMLOutputFactory outputFactory = StAXUtils.getXMLOutputFactory(ABDERA_WRITER_CONFIGURATION);
        XMLStreamWriter writer = outputFactory.createXMLStreamWriter(out, encoding);
        return writer;
    }

    public StreamWriter setOutputStream(OutputStream out, String charset) {
        try {
            this.writer = StaxStreamWriter.createXMLStreamWriter(out, charset);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter startDocument(String xmlversion, String charset) {
        try {
            this.writer.writeStartDocument(xmlversion, charset);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter startDocument(String xmlversion) {
        try {
            this.writer.writeStartDocument(xmlversion);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter endDocument() {
        try {
            this.writer.writeEndDocument();
            this.writer.flush();
            if (this.autoclose) {
                this.writer.close();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter endElement() {
        try {
            if (this.autoindent && this.textwritten == 0) {
                this.pop();
                this.indent();
            } else {
                this.pop();
            }
            this.writer.writeEndElement();
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void writeNamespace(String prefix, String namespace, boolean attr) throws XMLStreamException {
        String string = prefix = prefix != null ? prefix : "";
        if (!this.declared(prefix, namespace)) {
            if (attr && (namespace == null || "".equals(namespace))) {
                return;
            }
            if (prefix != null) {
                this.writer.writeNamespace(prefix, namespace);
            } else {
                this.writer.writeDefaultNamespace(namespace);
            }
            this.declare(prefix, namespace);
            if (this.autoflush) {
                this.writer.flush();
            }
        }
    }

    private boolean needToWriteNamespace(String prefix, String namespace) {
        NamespaceContext nc = this.writer.getNamespaceContext();
        String uri = nc.getNamespaceURI(prefix != null ? prefix : "");
        return uri != null ? !uri.equals(namespace) : true;
    }

    public StreamWriter startElement(String name, String namespace, String prefix) {
        try {
            if (prefix == null || prefix.equals("")) {
                prefix = this.writer.getPrefix(namespace);
            }
            if (this.autoindent && this.textwritten == 0) {
                this.indent();
            }
            this.push();
            if (prefix != null && !prefix.equals("")) {
                this.writer.writeStartElement(prefix, name, namespace);
                if (this.needToWriteNamespace(prefix, namespace)) {
                    this.writeNamespace(prefix, namespace, false);
                }
            } else if (namespace != null) {
                this.writer.writeStartElement("", name, namespace);
                if (this.needToWriteNamespace(prefix, namespace)) {
                    this.writeNamespace(prefix, namespace, false);
                }
            } else {
                this.writer.writeStartElement("", name, "");
                this.writer.writeDefaultNamespace("");
            }
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeElementText(String value) {
        try {
            ++this.textwritten;
            this.writer.writeCharacters(value);
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeComment(String value) {
        try {
            if (this.autoindent) {
                this.indent();
            }
            this.writer.writeComment(value);
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writePI(String value) {
        try {
            if (this.autoindent) {
                this.indent();
            }
            this.writer.writeProcessingInstruction(value);
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writePI(String value, String target) {
        try {
            if (this.autoindent) {
                this.indent();
            }
            this.writer.writeProcessingInstruction(value, target);
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeId() {
        return this.writeId(FOMHelper.generateUuid());
    }

    public StreamWriter writeDefaultNamespace(String uri) {
        try {
            this.writer.writeDefaultNamespace(uri);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeNamespace(String prefix, String uri) {
        try {
            this.writer.writeNamespace(prefix, uri);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter writeAttribute(String name, String namespace, String prefix, String value) {
        if (value == null) {
            return this;
        }
        try {
            if (prefix != null) {
                if (!prefix.equals("xml")) {
                    this.writeNamespace(prefix, namespace, true);
                }
                this.writer.writeAttribute(prefix, namespace, name, value);
            } else if (namespace != null) {
                if (!namespace.equals("http://www.w3.org/XML/1998/namespace")) {
                    // empty if block
                }
                this.writeNamespace(prefix, namespace, true);
                this.writer.writeAttribute(namespace, name, value);
            } else {
                this.writer.writeAttribute(name, value);
            }
            if (this.autoflush) {
                this.writer.flush();
            }
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void push() {
        this.namespaces.push(new HashMap());
        ++this.depth;
    }

    private void pop() {
        --this.depth;
        if (this.textwritten > 0) {
            --this.textwritten;
        }
        if (!this.namespaces.isEmpty()) {
            this.namespaces.pop();
        }
    }

    private void declare(String prefix, String namespace) {
        if (this.namespaces.isEmpty()) {
            return;
        }
        Map<String, String> frame = this.namespaces.peek();
        frame.put(prefix, namespace);
    }

    private boolean declared(String prefix, String namespace) {
        for (int n = this.namespaces.size() - 1; n >= 0; --n) {
            Map frame = (Map)this.namespaces.get(n);
            String chk = (String)frame.get(prefix);
            if (chk == null && namespace == null) {
                return true;
            }
            if (chk == null && namespace != null || chk != null && namespace == null || !chk.equals(namespace)) continue;
            return true;
        }
        return false;
    }

    public StreamWriter flush() {
        try {
            this.writer.flush();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public StreamWriter indent() {
        try {
            char[] indent = new char[this.depth * 2];
            Arrays.fill(indent, ' ');
            this.writer.writeCharacters("\n");
            this.writer.writeCharacters(indent, 0, indent.length);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public void close() throws IOException {
        try {
            this.writer.close();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public StreamWriter setPrefix(String prefix, String uri) {
        try {
            this.writer.setPrefix(prefix, uri);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}

